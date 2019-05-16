/*
 * Copyright 2018 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.asciidoctoreditor;

import static org.asciidoctor.Asciidoctor.Factory.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.OptionsBuilder;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.jruby.CompatVersion;
import org.jruby.RubyInstanceConfig;
import org.jruby.javasupport.JavaEmbedUtils;
import org.osgi.framework.Bundle;

/**
 * This wrapper is used to have correct access to asciidoctor inside
 * OSGI/eclipse.<br>
 * <br>
 * <u>In a nutshell: </u><br>
 * <ol>
 * <li>Using the lib plugin</li>
 * <li>Handling OSGI issues</li>
 * </ol>
 * <br>
 * <u>Details: </u><br>
 * <br>
 * The dependencies (jruby-complete, asciidoctorj etc.) are very,very big. So
 * decided to put asciidoctor parts into an dedicated plugin which will be kept
 * stable and does only contain those dependencies (asciidoctor-editor-libs). So
 * users will only need to download the lib-plugin only one time from
 * marketplace no matter how much updates the asciidoctor-editor-plugin has...
 * <br>
 * <br>
 * OSGI makes much problems when using asciidoctorj and ruby.<br>
 * So we use JavaEmbedUtils inside this wrapper. But even with using
 * JavaEmbedUtils the gems parts -e.g. coderay - are still problematic. So we
 * unzip gems parts to a user home sub directory
 * 
 * @author Albert Tregnaghi
 *
 */
public class AsciiDoctorOSGIWrapper {

	private static final String LIBS_PLUGIN_ID = "de.jcup.asciidoctoreditor.libs";
	private static final String CSS_PLUGIN_ID = "de.jcup.asciidoctoreditor.css";
	public static final AsciiDoctorOSGIWrapper INSTANCE = new AsciiDoctorOSGIWrapper();
	private Asciidoctor asciidoctor;
	private Object monitor = new Object();
	
	private AsciiDoctorOSGIWrapper() {
		
	}

	/**
	 * @return asciidoctor instance lazy but threadsafe
	 */
	public Asciidoctor getAsciidoctor() {
		synchronized (monitor) {
			if (asciidoctor==null){
				loadAsciidoctor();
			}
			return asciidoctor;
			
		}
	}

	private void loadAsciidoctor() {
		/* load asciidoctor OSGI conform */
		Bundle bundle = Platform.getBundle(LIBS_PLUGIN_ID);
		ClassLoader libsClassLoader = fetchClassLoader(bundle);
		
		initAsciidoctor(libsClassLoader);
		asciidoctor = create(libsClassLoader);

		asciidoctor.requireLibrary("asciidoctor-diagram");
//		asciidoctor.requireLibrary("asciidoctor-pdf");
//		asciidoctor.javaExtensionRegistry().includeProcessor(FileIncludeIncludeProcessor.class);
		
		/* initialize...*/
		Job job = Job.create("Initialize ascii doctor access", (ICoreRunnable) monitor -> {
			monitor.beginTask("Initializing...", IProgressMonitor.UNKNOWN);
			/* start a simple convert to ensure asciidoctor has been started*/
			asciidoctor.convert("== headline2", OptionsBuilder.options().toFile(false).asMap());
			monitor.done();
		});
		try {
			job.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
	}

	public File getLibsUnzipFolder(){
		String versionName = getLibVersionName();
		return ensureUnzippedRubGemsArtefactsAvailable(versionName);
	}
	
	public File getCSSFolder(){
		String versionName = getCSSVersionName();
		return ensureCSSArtefactsAreAvailable(versionName);
	}
	
	public File getAddonsFolder(){
		return ensureEditorAddonsAreAvailable();
	}
	
	private String getEditorVersionName() {
		return getPluginVersion(AsciiDoctorEditorActivator.PLUGIN_ID);
	}
	private String getLibVersionName() {
		return getPluginVersion(LIBS_PLUGIN_ID);
	}
	
	private String getCSSVersionName() {
		return getPluginVersion(CSS_PLUGIN_ID);
	}
	
	private String getPluginVersion(String pluginId){
		Bundle bundle = Platform.getBundle(pluginId);
		String versionName = bundle.getVersion().toString();
		return versionName;
	}

	protected void initAsciidoctor(ClassLoader libsClassLoader) {
		// https://github.com/asciidoctor/asciidoctorj#using-asciidoctorj-in-an-osgi-environment
		RubyInstanceConfig config = new RubyInstanceConfig();
		config.setLoader(libsClassLoader);
		initializeRubyConfig(config);
	}

	private void initializeRubyConfig(RubyInstanceConfig config) {
		String versionName = getLibVersionName();
		File unzippedGEMSfolder = ensureUnzippedRubGemsArtefactsAvailable(versionName);

		/* @formatter:off*/
		/*
		 * 
		 * Workaround: 
		 * - I always got problems with jruby not finding gems artefacts
		 * - No way out in OSGI even when using the java embedder utils
		 * - e.g. coderay duo was always not found
		 * - But when setting the current directory to a folder where ascidoctorj gems 
		 *   are extract there the loader does find them !!
		 * 
		 */
		// PDF parts using pawn which uses double splat, which was introduced with Ruby2.0
		// https://medium.freecodecamp.org/rubys-splat-and-double-splat-operators-ceb753329a78
		// https://stackoverflow.com/questions/15281036/how-to-run-ruby-2-0-with-jruby-1-7
		config.setCompatVersion(CompatVersion.RUBY2_0);
		
		config.setCurrentDirectory(unzippedGEMSfolder.getAbsolutePath());
		JavaEmbedUtils.initialize(Arrays.asList(
				"META-INF/jruby.home/lib/ruby/2.0", 
				/* asciidoctor + asciidoctor-diagram dependencies:*/
				"gems/asciidoctor-1.5.6.1/lib",
				"gems/asciidoctor-diagram-1.5.4.1/lib",
				"gems/coderay-1.1.0/lib",
				"gems/erubis-2.7.0/lib",
				"gems/haml-4.0.5/lib",
				"gems/open-uri-cached-0.0.5/lib",
				"gems/slim-3.0.6/lib",
				"gems/temple-0.7.7/lib",
				"gems/thread_safe-0.3.6/lib",
				"gems/tilt-2.0.1/lib",
				/* asciidoctor-pdf dependencies:*/
				"gems/ttfunk-1.5.1/lib",
				"gems/treetop-1.5.3/lib",
				//threadsafe 0.3.6 - already before
				"gems/safe_yaml-1.0.4/lib",
				"gems/ruby_rc4-0.1.5/lib",
				"gems/rouge-2.0.7/lib",
				"gems/public_suffix-1.4.6/lib",
				"gems/prawn-templates-0.1.1/lib",
				"gems/prawn-table-0.2.2/lib",
				"gems/prawn-svg-0.27.1/lib",
				"gems/prawn-icon-1.3.0/lib",
				"gems/prawn-2.2.2/lib",
				"gems/polyglot-0.3.5/lib",
				"gems/pdf-reader-2.0.0/lib",
				"gems/pdf-core-0.7.0/lib",
				"gems/hashery-2.1.2/lib",
				"gems/css_parser-1.5.0/lib",
				"gems/asciidoctor-pdf-1.5.0.alpha.16/lib",
				"gems/Ascii85-1.0.2/lib",
				"gems/afm-0.2.2/lib",
				"gems/addressable-2.4.0/lib"
				
		), config);
		/* @formatter:on*/
	}

	private ClassLoader fetchClassLoader(Bundle bundle) {
		ClassLoader libsClassLoader;
		try {
			Class<?> clazz = bundle.loadClass(RubyInstanceConfig.class.getName());
			Object obj = clazz.newInstance();
			libsClassLoader = obj.getClass().getClassLoader();
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("cannot access ruby config!", e);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException("cannot access ruby config!", e);
		}
		return libsClassLoader;
	}

	private File ensureUnzippedRubGemsArtefactsAvailable(String versionName) {
		File libFolder = getHomeSubSubFolder("libs");

		File unzippedGEMSfolder = new File(libFolder, versionName);
		if (!unzippedGEMSfolder.exists()) {
			unzippedGEMSfolder.mkdirs();

			try {
				unzipOrFail(unzippedGEMSfolder, "asciidoctorj-1.5.6.jar");
				unzipOrFail(unzippedGEMSfolder, "asciidoctorj-diagram-1.5.4.1.jar");
				unzipOrFail(unzippedGEMSfolder, "asciidoctorj-pdf-1.5.0-alpha.16.jar");
			} catch (IOException e) {
				throw new IllegalStateException("Unzip problem with asciidcotor", e);
			}

		}
		return unzippedGEMSfolder;
	}
	
	private File ensureCSSArtefactsAreAvailable(String versionName) {
		File cssFolder = getHomeSubSubFolder("css");

		File targetVersionCSSfolder = new File(cssFolder, versionName);
		if (!targetVersionCSSfolder.exists()) {
			targetVersionCSSfolder.mkdirs();

			try {
				copyFolderOrFail(targetVersionCSSfolder, "css",CSS_PLUGIN_ID);
			} catch (IOException e) {
				throw new IllegalStateException("Not able to install CSS files from css plugin", e);
			}

		}
		return targetVersionCSSfolder;
	}
	
	private File ensureEditorAddonsAreAvailable() {
		String versionName = getEditorVersionName();
		File cssFolder = getHomeSubSubFolder("addons");

		File targetVersionAddonsfolder = new File(cssFolder, versionName);
		if (!targetVersionAddonsfolder.exists()) {
			targetVersionAddonsfolder.mkdirs();

			try {
				copyFolderOrFail(targetVersionAddonsfolder, "addons",AsciiDoctorEditorActivator.PLUGIN_ID);
			} catch (IOException e) {
				throw new IllegalStateException("Not able to install addon files from editor plugin", e);
			}

		}
		return targetVersionAddonsfolder;
	}

	private void unzipOrFail(File unzippedGEMSfolder, String zipFileName) throws IOException {
		File zipFile = EclipseResourceHelper.DEFAULT.getFileInPlugin(zipFileName, LIBS_PLUGIN_ID);
		if (zipFile==null) {
            throw new IllegalStateException("file:" + zipFileName + " not found!");
        }
		if (!zipFile.exists()) {
			throw new IllegalStateException("file:" + zipFile.getAbsolutePath() + " does not exist!");
		}

		ZipSupport support = new ZipSupport();
		support.unzip(zipFile, unzippedGEMSfolder);
	}
	
	private void copyFolderOrFail(File targetFolder, String sourceFolder, String pluginID) throws IOException {
		File folderInPlugin = EclipseResourceHelper.DEFAULT.getFileInPlugin(sourceFolder, pluginID);
		if (!folderInPlugin.exists()) {
			throw new IllegalStateException("folder:" + folderInPlugin.getAbsolutePath() + " does not exist!");
		}

		FileUtils.copyDirectory(folderInPlugin,targetFolder);
	}

	private File getHomeSubSubFolder(String name){
		return new File(getHomeSubFolder(), name);
	}

	private File getHomeSubFolder() {
		File homeSubFolder = new File(System.getProperty("user.home"), ".eclipse-asciidoctor-editor");
		return homeSubFolder;
	}

}
