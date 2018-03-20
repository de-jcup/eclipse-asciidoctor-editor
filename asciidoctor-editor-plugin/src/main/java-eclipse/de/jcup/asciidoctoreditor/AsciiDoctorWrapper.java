package de.jcup.asciidoctoreditor;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
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

		List<File> list = new ArrayList<>();
		try {
			File fontAwesomeCSSfile = helper.getFileInPlugin("css/font-awesome/css/font-awesome.min.css");
			File dejavouFile = helper.getFileInPlugin("css/dejavu/dejavu.css");

			list.add(fontAwesomeCSSfile);
			list.add(dejavouFile);
		} catch (IOException e) {
			String message = "Was not able load additional css data. Cannot render file.";
			AsciiDoctorEditorUtil.logError(message,e);
		}
		File unzipFolder = AsciiDoctorOSGIWrapper.INSTANCE.getUnzipFolder();
		list.add(new File(unzipFolder,"/gems/asciidoctor-1.5.6.1/data/stylesheets/asciidoctor-default.css"));
		list.add(new File(unzipFolder,"/gems/asciidoctor-1.5.6.1/data/stylesheets/coderay-asciidoctor.css"));

		StringBuilder prefixSb = new StringBuilder();
		prefixSb.append("<html>\n");
		prefixSb.append("<head>\n");
		for (File file: list){
			prefixSb.append(createLinkToCSSFile(file));
		}
		prefixSb.append("</head>\n");
		prefixSb.append("<body>\n");
		return prefixSb.toString();
	}
	protected String createLinkToCSSFile(File file) {
		String pathToCSSFile;
		try {
			pathToCSSFile = file.toURI().toURL().toExternalForm();
		} catch (MalformedURLException e) {
			pathToCSSFile= file.getAbsolutePath();
		};
		String dejavuCssLink = "<link rel=\"stylesheet\" href=\"" + pathToCSSFile + "\">\n";
		return dejavuCssLink;
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
