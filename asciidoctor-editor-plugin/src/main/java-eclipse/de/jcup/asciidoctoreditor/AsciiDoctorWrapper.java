package de.jcup.asciidoctoreditor;

import java.awt.JobAttributes.DestinationType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import javax.print.attribute.standard.Destination;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;


public class AsciiDoctorWrapper {
	private Path tempFolder;
	private static Object HTML_PREFIX_MONITOR = new Object();

	private Asciidoctor asciiDoctor;
	private EclipseResourceHelper helper;

	private static String prefixHTML;

	public AsciiDoctorWrapper(){
		this.asciiDoctor = AsciiDoctorOSGIWrapper.INSTANCE.getAsciidoctor();
		this.helper = EclipseResourceHelper.DEFAULT;
		initTempFolderOrFail();
	}
	public String convertToHTML(File file) {
		String message = asciiDoctor.convertFile(file, getDefaultOptions(file.getParentFile()));
		return message;
	}
	public String convertToHTML(String asciiDoc) {
		String html = asciiDoctor.convert(asciiDoc, getDefaultOptions(new File(".")));
		return html;
	}
	public String buildHTMLWithCSS(String html) {
		StringBuilder sb = new StringBuilder();
		sb.append(getPrefixHTML());
		sb.append(html);
		sb.append("</body>");
		sb.append("</html>");

		return sb.toString();
	}
	private String getPrefixHTML() {
		synchronized(HTML_PREFIX_MONITOR){
			if (EclipseDevelopmentSettings.DEBUG_RELOAD_HTML_PREFIX){
				AsciiDoctorEditorUtil.logInfo("Reloading html prefix - for details look into EclipseDevelopmentSettings.java");
				return buildPrefixHTML();
			}
			
			/* we build this only one time - so not always reloading necessary*/
			if (prefixHTML==null){
				prefixHTML=buildPrefixHTML();
			}
			return prefixHTML;
		}
	}
	
	private String buildPrefixHTML() {
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
		return sb.toString();
	}

	protected void initTempFolderOrFail() {
		try {
			tempFolder = Files.createTempDirectory("ascii-doctor-eclipse");
			tempFolder.toFile().deleteOnExit();
		} catch (IOException e) {
			throw new IllegalStateException("Not able to provide tempfolder",e);
		}
	}
	
	private Map<String, Object> getDefaultOptions(File baseDir) {
		/* @formatter:off*/
		Attributes attrs = AttributesBuilder.
				attributes().
					showTitle(true).
					sourceHighlighter("coderay").
					attribute("coderay-css", "style").
					attribute("env", "eclipse").attribute("env-eclipse").get();
		if (tempFolder != null) {
			System.out.println("Tempfolder:" + tempFolder);
			attrs.setAttribute("outdir", tempFolder.toAbsolutePath().normalize().toString());
		}
		File destionationFolder= null;
		if (tempFolder!=null){
			destionationFolder= tempFolder.toFile();
		}else{
			destionationFolder= baseDir;
		}
		
		OptionsBuilder opts = OptionsBuilder.options().
				toDir(destionationFolder).
//				destinationDir(destionationFolder).
				safe(SafeMode.UNSAFE).
				backend("html5").
				headerFooter(false).
				attributes(attrs).
				option("sourcemap", "true").
				baseDir(baseDir);
		/* @formatter:on*/
		return opts.asMap();
	}
	
	public File getTempFileFor(File editorFile,boolean full) {
		File parent = null;
		if (tempFolder==null){
			parent = new File(".");
		}else {
			parent = tempFolder.toFile();
		}
		String baseName = FilenameUtils.getBaseName(editorFile.getName());
		StringBuilder sb = new StringBuilder();
		if (full){
			sb.append("full_");
		}
		sb.append(baseName);
		sb.append(".html");
		return new File(parent,sb.toString());
	}
}
