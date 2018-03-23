package de.jcup.asciidoctoreditor;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.asciidoctor.AsciiDocDirectoryWalker;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.DirectoryWalker;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
import org.asciidoctor.ast.DocumentHeader;

public class AsciiDoctorWrapper {
	private Path tempFolder;
	private static Object HTML_PREFIX_MONITOR = new Object();
	private static FileFilter ADOC_FILE_FILTER = new ADocFilter();

	private Asciidoctor asciiDoctor;
	private EclipseResourceHelper helper;
	private String cachedImagesPath;
	private File cachedBaseDir;

	private Map<String, Object> cachedAttributes;
	private boolean tocVisible;

	private static String prefixHTML;

	public AsciiDoctorWrapper() {
		this.asciiDoctor = AsciiDoctorOSGIWrapper.INSTANCE.getAsciidoctor();
		this.helper = EclipseResourceHelper.DEFAULT;
		initTempFolderOrFail();
	}

	public String convertToHTML(File asciiDocFile) {
		File baseDir = findBaseDir(asciiDocFile.getParentFile());
		if (cachedImagesPath == null) {
			cachedImagesPath = resolveImagesDirPath(baseDir);
		}
		String message = asciiDoctor.convertFile(asciiDocFile, getDefaultOptions(baseDir, cachedImagesPath));
		return message;
	}

	private File findBaseDir(File dir) {
		if (cachedBaseDir == null) {
			cachedBaseDir = findBaseDirNotCached(dir);
		}
		return cachedBaseDir;
	}

	private File findBaseDirNotCached(File dir) {
		// very simple approach just go up until no longer any asciidoc files
		// are found
		// if no longer .adoc files assume this is the end and use directory
		if (dir == null) {
			return new File(".");// should not happen but fall back...
		}
		File parentFile = dir.getParentFile();
		if (containsADocFiles(parentFile)) {
			return findBaseDir(parentFile);
		}
		return dir;
	}

	private boolean containsADocFiles(File dir) {
		if (!dir.isDirectory()) {
			return false;
		}
		File[] files = dir.listFiles(ADOC_FILE_FILTER);
		if (files.length == 0) {
			return false;
		}
		return true;
	}

	private static class ADocFilter implements FileFilter {

		@Override
		public boolean accept(File file) {
			if (file == null || !file.isFile()) {
				return false;
			}
			if (!file.getName().endsWith(".adoc")) {
				return false;
			}
			return true;
		}

	}

	/**
	 * Resets cached values: baseDir, imagesDir
	 */
	public void resetCaches() {
		cachedImagesPath = null;
		cachedBaseDir = null;
		cachedAttributes = null;
	}

	protected Map<String, Object> getCachedAttributes(File baseDir) {
		if (cachedAttributes == null) {
			cachedAttributes = resolveAttributes(baseDir);
		}
		return cachedAttributes;
	}

	protected String resolveImagesDirPath(File baseDir) {

		Object imagesDir = getCachedAttributes(baseDir).get("imagesdir");

		String imagesDirPath = null;
		if (imagesDir != null) {
			imagesDirPath = imagesDir.toString();
			if (imagesDirPath.startsWith("./")) {
				File imagePathNew = new File(baseDir, imagesDirPath.substring(2));
				imagesDirPath = imagePathNew.getAbsolutePath();
			}
		} else {
			imagesDirPath = baseDir.getAbsolutePath();
		}
		return imagesDirPath;
	}

	protected Map<String, Object> resolveAttributes(File baseDir) {
		Map<String, Object> map = new HashMap<>();
		Set<DocumentHeader> documentIndex = new HashSet<DocumentHeader>();
		DirectoryWalker directoryWalker = new AsciiDocDirectoryWalker(baseDir.getAbsolutePath());

		for (File file : directoryWalker.scan()) {
			documentIndex.add(asciiDoctor.readDocumentHeader(file));
		}
		for (DocumentHeader header : documentIndex) {
			map.putAll(header.getAttributes());
		}
		return map;
	}

	/**
	 * This method should normally only be used when we have no file access -
	 * e.g. in compare modes etc.
	 * 
	 * @param asciiDoc
	 * @return html string
	 */
	public String convertToHTML(String asciiDoc) {
		File baseFile = new File(".");
		/* FIXME ATR, 22.03.2018: VERY IMPORTANT: supply next line! */
		/*
		 * FIXME ATR, 22.03.2018: wrap up not only images dir but ALL
		 * attributes!!!! so e.g. ":icons: font" will be available everywher!
		 */
		String imagesPath = asciiDoc.indexOf(":imagesDir") == -1 ? baseFile.getAbsolutePath() : null;
		String html = asciiDoctor.convert(asciiDoc, getDefaultOptions(baseFile, imagesPath));
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
		synchronized (HTML_PREFIX_MONITOR) {
			if (EclipseDevelopmentSettings.DEBUG_RELOAD_HTML_PREFIX) {
				AsciiDoctorEditorUtil
						.logInfo("Reloading html prefix - for details look into EclipseDevelopmentSettings.java");
				return buildPrefixHTML();
			}

			/*
			 * we build this only one time - so not always reloading necessary
			 */
			if (prefixHTML == null) {
				prefixHTML = buildPrefixHTML();
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
			AsciiDoctorEditorUtil.logError(message, e);
		}
		File unzipFolder = AsciiDoctorOSGIWrapper.INSTANCE.getUnzipFolder();
		list.add(new File(unzipFolder, "/gems/asciidoctor-1.5.6.1/data/stylesheets/asciidoctor-default.css"));
		list.add(new File(unzipFolder, "/gems/asciidoctor-1.5.6.1/data/stylesheets/coderay-asciidoctor.css"));

		StringBuilder prefixSb = new StringBuilder();
		prefixSb.append("<html>\n");
		prefixSb.append("<head>\n");
		for (File file : list) {
			prefixSb.append(createLinkToCSSFile(file));
		}
		prefixSb.append("</head>\n");
		// prefixSb.append("<body >\n");
		prefixSb.append("<body class=\"article toc2 toc-left\">");
		return prefixSb.toString();
	}

	protected String createLinkToCSSFile(File file) {
		String pathToCSSFile;
		try {
			pathToCSSFile = file.toURI().toURL().toExternalForm();
		} catch (MalformedURLException e) {
			pathToCSSFile = file.getAbsolutePath();
		}
		;
		String dejavuCssLink = "<link rel=\"stylesheet\" href=\"" + pathToCSSFile + "\">\n";
		return dejavuCssLink;
	}

	protected void initTempFolderOrFail() {
		try {
			tempFolder = Files.createTempDirectory("ascii-doctor-eclipse");
			tempFolder.toFile().deleteOnExit();
		} catch (IOException e) {
			throw new IllegalStateException("Not able to provide tempfolder", e);
		}
	}

	private Map<String, Object> getDefaultOptions(File baseDir, String imagesDir) {
		/* @formatter:off*/
		Attributes attrs;
		AttributesBuilder attrBuilder = AttributesBuilder.
				attributes().
					showTitle(true).
					sourceHighlighter("coderay").
				    attribute("icons", "font").
					attribute("source-highlighter","coderay").
					attribute("coderay-css", "style").
					attribute("env", "eclipse").
					attribute("env-eclipse");
		
		Map<String, Object> cachedAttributes = getCachedAttributes(baseDir);
		for (String key: cachedAttributes.keySet()){
			Object value = cachedAttributes.get(key);
			if (value!=null && value.toString().isEmpty()){
				if ("toc".equals(key)){
					// currently we always remove the TOC (we do show the TOC only by the internal boolean flag
					// also the TOC is not correctly positioned - (always on top instead of being at left side)
					continue;
				}
				attrBuilder.attribute(key,value);
			}
		}
		if (isTocVisible()){
			attrBuilder.attribute("toc","left");
//			attrBuilder.attribute("basebackend-html");
		}
		if (imagesDir!=null){
			attrBuilder.imagesDir(imagesDir);
		}
		
		
		attrs=attrBuilder.get();
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

	public File getTempFileFor(File editorFile, boolean full) {
		File parent = null;
		if (tempFolder == null) {
			parent = new File(".");
		} else {
			parent = tempFolder.toFile();
		}
		String baseName = FilenameUtils.getBaseName(editorFile.getName());
		StringBuilder sb = new StringBuilder();
		if (full) {
			sb.append("full_");
		}
		sb.append(baseName);
		sb.append(".html");
		return new File(parent, sb.toString());
	}

	public void dispose() {
		if (tempFolder == null) {
			return;
		}
		File temp = tempFolder.toFile();
		if (!temp.exists()) {
			return;
		}
		try {
			FileUtils.deleteDirectory(temp);
		} catch (IOException e) {
			AsciiDoctorEditorUtil.logError("Was not able to delete temp folder", e);
		}

	}

	public void setTocVisible(boolean tocVisible) {
		this.tocVisible = tocVisible;
	}
	
	public boolean isTocVisible() {
		return tocVisible;
	}
}
