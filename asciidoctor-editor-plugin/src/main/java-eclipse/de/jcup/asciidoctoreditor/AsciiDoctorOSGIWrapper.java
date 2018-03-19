package de.jcup.asciidoctoreditor;

import static org.asciidoctor.Asciidoctor.Factory.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.asciidoctor.Asciidoctor;
import org.eclipse.core.runtime.Platform;
import org.jruby.RubyInstanceConfig;
import org.jruby.javasupport.JavaEmbedUtils;
import org.osgi.framework.Bundle;

/**
 * This wrapper is used to have correct access to asciidoctor inside
 * OSGI/eclipse.<br>
 * <br>
 * <u>In a nutshell: </u><br>
 * <ol>
 * 	<li>Using the lib plugin</li>
 *  <li>Handling OSGI issues</li>
 * </ol>
 * <br>
 * <u>Details: </u><br>
 * <br>
 * The dependencies (jruby-complete, asciidoctorj etc.) are very,very big. So
 * decided to put asciidoctor parts into an dedicated plugin which will be kept
 * stable and does only contain those dependencies (asciidoctor-editor-libs). So
 * users will only need to download the lib-plugin only one time from
 * marketplace no matter how much updates the asciidoctor-editor-plugin has...
 * <br><br>
 * OSGI makes much problems when using asciidoctorj and ruby.<br>
 * So we use JavaEmbedUtils inside this wrapper. But even with using JavaEmbedUtils the gems parts -e.g. coderay - 
 * are still problematic. So we unzip gems parts to a user home sub directory
 * 
 * @author Albert Tregnaghi
 *
 */
public class AsciiDoctorOSGIWrapper {

	private static final String LIBS_PLUGIN_ID = "de.jcup.asciidoctoreditor.libs";
	public static final AsciiDoctorOSGIWrapper INSTANCE = new AsciiDoctorOSGIWrapper();
	private Asciidoctor asciidoctor;
	
	private AsciiDoctorOSGIWrapper() {
		/* load asciidoctor OSGI conform */
		Bundle bundle = Platform.getBundle(LIBS_PLUGIN_ID);
		String versionName = bundle.getVersion().toString();
		ClassLoader libsClassLoader = fetchClassLoader(bundle);
		
		initAscIIDoctor(versionName, libsClassLoader);
		asciidoctor = create(libsClassLoader);
	}
	
	public Asciidoctor getAsciidoctor() {
		return asciidoctor;
	}

	protected void initAscIIDoctor(String versionName, ClassLoader libsClassLoader) {
		// https://github.com/asciidoctor/asciidoctorj#using-asciidoctorj-in-an-osgi-environment
		RubyInstanceConfig config = new RubyInstanceConfig();
		config.setLoader(libsClassLoader);
		initializeRubyConfig(versionName, config);
	}


	private void initializeRubyConfig(String versionName, RubyInstanceConfig config) {
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
		config.setCurrentDirectory(unzippedGEMSfolder.getAbsolutePath());
		JavaEmbedUtils.initialize(Arrays.asList(
				"META-INF/jruby.home/lib/ruby/2.0", 
				"gems/asciidoctor-1.5.6.1/lib",
				"gems/coderay-1.1.0/lib",
				"gems/erubis-2.7.0/lib",
				"gems/haml-4.0.5/lib",
				"gems/open-uri-cached-0.0.5/lib",
				"gems/slim-3.0.6/lib",
				"gems/temple-0.7.7/lib",
				"gems/thread_safe-0.3.6/lib",
				"gems/tilt-2.0.1/lib"
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
		File homeSubFolder = new File(System.getProperty("user.home"), ".eclipse-asciidoctor-editor");
		File libFolder = new File(homeSubFolder, "libs");
		
		File unzippedGEMSfolder = new File(libFolder,versionName);
		if (!unzippedGEMSfolder.exists()) {
			unzippedGEMSfolder.mkdirs();

			try {
				unzipOrFail(unzippedGEMSfolder, "asciidoctorj-1.5.6.jar");
				unzipOrFail(unzippedGEMSfolder, "asciidoctorj-diagram-1.5.4.1.jar");
			} catch (IOException e) {
				throw new IllegalStateException("Unzip problem with asciidcotor", e);
			}

		}
		return unzippedGEMSfolder;
	}

	private void unzipOrFail(File unzippedGEMSfolder, String zipFileName) throws IOException {
		File zipFile = EclipseResourceHelper.DEFAULT.getFileInPlugin(zipFileName,
				LIBS_PLUGIN_ID);
		if (!zipFile.exists()) {
			throw new IllegalStateException(
					"file:" + zipFile.getAbsolutePath() + " does not exist!");
		}
		
		ZipSupport support = new ZipSupport();
		support.unzip(zipFile, unzippedGEMSfolder);
	}

	
}
