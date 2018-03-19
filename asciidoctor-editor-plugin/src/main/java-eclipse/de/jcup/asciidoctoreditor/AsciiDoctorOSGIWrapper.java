package de.jcup.asciidoctoreditor;

import static org.asciidoctor.Asciidoctor.Factory.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
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
	private Path tempFolder;
	private EclipseResourceHelper helper;

	private AsciiDoctorOSGIWrapper() {
		helper = EclipseResourceHelper.DEFAULT;

		/* load asciidoctor OSGI conform */
		Bundle bundle = Platform.getBundle(LIBS_PLUGIN_ID);
		String versionName = bundle.getVersion().toString();
		ClassLoader libsClassLoader = fetchClassLoader(bundle);
		
		initAscIIDoctor(versionName, libsClassLoader);
		initTempFolderOrFail();
		asciidoctor = create(libsClassLoader);
	}

	protected void initAscIIDoctor(String versionName, ClassLoader libsClassLoader) {
		// https://github.com/asciidoctor/asciidoctorj#using-asciidoctorj-in-an-osgi-environment
		RubyInstanceConfig config = new RubyInstanceConfig();
		config.setLoader(libsClassLoader);
		initializeRubyConfig(versionName, config);
	}

	protected void initTempFolderOrFail() {
		try {
			tempFolder = Files.createTempDirectory("ascii-doctor-eclipse");
			tempFolder.toFile().deleteOnExit();
		} catch (IOException e) {
			throw new IllegalStateException("Not able to provide tempfolder",e);
		}
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

	public String convertToHTML(String asciiDoc) {
		String html = asciidoctor.convert(asciiDoc, getDefaultOptions());
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head>");

		sb.append("<style>");
		File defaultCSSFileInPlugin;
		File codeRayCSSFileInPlugin;
		try{
			defaultCSSFileInPlugin = helper.getFileInPlugin("css/default.css");
			codeRayCSSFileInPlugin = helper.getFileInPlugin("css/coderay.css");
			
		}catch(IOException e){
			String message = "NO css data available! Cannot render files";
			AsciiDoctorEditorUtil.logError(message,e);
			return message;
		}
		if (defaultCSSFileInPlugin==null || codeRayCSSFileInPlugin==null){
			String message = "NO css data found in plugins! Cannot render files";
			AsciiDoctorEditorUtil.logError(message,null);
			return message;
		}
		
		try (FileInputStream defaultFOS = new FileInputStream(defaultCSSFileInPlugin);
				FileInputStream coderayFOS = new FileInputStream(codeRayCSSFileInPlugin)) {
			/*
			 * adopted from
			 * https://github.com/asciidoctor/asciidoctor-intellij-plugin :
			 */
			String myInlineCss = IOUtils.toString(defaultFOS);
			// myInlineCssDarcula = myInlineCss +
			// IOUtils.toString(JavaFxHtmlPanel.class.getResourceAsStream("darcula.css"));
			// myInlineCssDarcula +=
			// IOUtils.toString(JavaFxHtmlPanel.class.getResourceAsStream("coderay-darcula.css"));
			myInlineCss += IOUtils.toString(coderayFOS);
			sb.append(myInlineCss);

		} catch (IOException e) {
			String message = "Was not able load css data. Cannot render file.";
			AsciiDoctorEditorUtil.logError(message,e);
			return message;
		}

		sb.append("</style>");
		try {
			/*
			 * FIXME ATR, 15.03.2018: replacwe the regexp replaceAll with static
			 * one (at least)
			 */
			File fontAwesomeCSSfile = helper.getFileInPlugin("css/font-awesome/css/font-awesome.min.css");
			String fontAwesomeCssPath = fontAwesomeCSSfile.toURI().toURL().toExternalForm();// fontAwesomeCSSfile.getAbsolutePath().replaceAll("\\\\",
																							// "/"
			/* FIXME ATR, 19.03.2018: remove sysout when stable */																	// );
			System.out.println(fontAwesomeCssPath);
			String fontAwesomeCssLink = "<link rel=\"stylesheet\" href=\"" + fontAwesomeCssPath + "\">";
			sb.append(fontAwesomeCssLink);

			File dejavouFile = helper.getFileInPlugin("css/dejavu/dejavu.css");
			String dejavouPath = dejavouFile.getAbsolutePath().replaceAll("\\\\", "/");
			String dejavuCssLink = "<link rel=\"stylesheet\" href=\"" + dejavouPath + "\">";
			sb.append(dejavuCssLink);
		} catch (IOException e) {
			String message = "Was not able load additional css data. Cannot render file.";
			AsciiDoctorEditorUtil.logError(message,e);
			return message;
		}
		sb.append("</head>");
		sb.append("<body>");
		sb.append(html);
		sb.append("</body>");
		sb.append("</html>");

		return sb.toString();
	}

	private Map<String, Object> getDefaultOptions() {
		Attributes attrs = AttributesBuilder.attributes().showTitle(true).sourceHighlighter("coderay")
				.attribute("coderay-css", "style").attribute("env", "eclipse").attribute("env-eclipse").get();
		if (tempFolder != null) {
			System.out.println("Tempfolder:" + tempFolder);
			attrs.setAttribute("outdir", tempFolder.toAbsolutePath().normalize().toString());
		}
		OptionsBuilder opts = OptionsBuilder.options().safe(SafeMode.UNSAFE).backend("html5").headerFooter(false)
				.attributes(attrs).option("sourcemap", "true").baseDir(new File("."));
		return opts.asMap();
	}
}
