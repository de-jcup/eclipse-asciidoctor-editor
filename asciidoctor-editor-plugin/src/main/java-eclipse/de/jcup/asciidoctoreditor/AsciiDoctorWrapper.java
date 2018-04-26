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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
	private static FileFilter ADOC_FILE_FILTER = new ADocFilter();

	private String cachedImagesPath;
	private File cachedBaseDir;

	private Map<String, Object> cachedAttributes;
	private boolean tocVisible;
	private LogAdapter logAdapter;

	public AsciiDoctorWrapper(LogAdapter logAdapter) {
		if (logAdapter==null){
			throw new IllegalArgumentException("log adapter may not be null!");
		}
		this.logAdapter=logAdapter;
		initTempFolderOrFail();
	}

	public void convertToHTML(File asciiDocFile) throws Exception{
		File baseDir = findBaseDir(asciiDocFile.getParentFile());
		if (cachedImagesPath == null) {
			cachedImagesPath = resolveImagesDirPath(baseDir);
		}
		try{
			getAsciiDoctor().convertFile(asciiDocFile, getDefaultOptions(baseDir, cachedImagesPath));
		}catch(Exception e){
			logAdapter.logError("Cannot convert to html:"+asciiDocFile, e);
			throw e;
		}
	}

	private Asciidoctor getAsciiDoctor() {
		return AsciiDoctorOSGIWrapper.INSTANCE.getAsciidoctor();
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
			documentIndex.add(getAsciiDoctor().readDocumentHeader(file));
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
	 * @throws Exception 
	 */
	public String convertToHTML(String asciiDoc)  throws Exception{
		File baseFile = new File(".");
		String imagesPath = asciiDoc.indexOf(":imagesDir") == -1 ? baseFile.getAbsolutePath() : null;
		try{
			return getAsciiDoctor().convert(asciiDoc, getDefaultOptions(baseFile, imagesPath));
		}catch(Exception e){
			logAdapter.logError("Cannot convert html from string", e);
			throw e;
		}
	}

	public String buildHTMLWithCSS(String html, int refreshAutomaticallyInSeconds) {
		StringBuilder sb = new StringBuilder();
		sb.append(buildPrefixHTML(refreshAutomaticallyInSeconds));
		sb.append(html);
		if (refreshAutomaticallyInSeconds > 0) {
			sb.append("<script type=\"text/javascript\">pageloadEvery("+refreshAutomaticallyInSeconds*1000+");</script>");
		}
		sb.append("</body>");
		sb.append("</html>");

		return sb.toString();
	}

	private String buildPrefixHTML(int refreshAutomaticallyInSeconds) {

		List<File> list = new ArrayList<>();
		File unzipFolder = AsciiDoctorOSGIWrapper.INSTANCE.getLibsUnzipFolder();
		File cssFolder = AsciiDoctorOSGIWrapper.INSTANCE.getCSSFolder();
		File addonsFolder = AsciiDoctorOSGIWrapper.INSTANCE.getAddonsFolder();

		list.add(new File(unzipFolder, "/gems/asciidoctor-1.5.6.1/data/stylesheets/asciidoctor-default.css"));
		list.add(new File(unzipFolder, "/gems/asciidoctor-1.5.6.1/data/stylesheets/coderay-asciidoctor.css"));
		list.add(new File(cssFolder, "/font-awesome/css/font-awesome.min.css"));
		list.add(new File(cssFolder, "/dejavu/dejavu.css"));
		list.add(new File(cssFolder, "/MathJax/MathJax.js"));
		list.add(new File(addonsFolder, "/javascript/document-autorefresh.js"));

		StringBuilder prefixSb = new StringBuilder();
		prefixSb.append("<html>\n");
		prefixSb.append("<head>\n");
		prefixSb.append("  <meta charset=\"UTF-8\">\n");
		prefixSb.append("  <!--[if IE]><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><![endif]-->\n");
		prefixSb.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
		prefixSb.append("  <meta name=\"generator\" content=\"Eclipse Asciidoctor Editor\">\n");
		prefixSb.append("  <title>AsciiDoctor Editor temporary output</title>\n");
		for (File file : list) {
			prefixSb.append(createLinkToFile(file));
		}
		prefixSb.append("</head>\n");
		
		
		prefixSb.append("<body ");
		if (tocVisible) {
			prefixSb.append("class=\"article toc2 toc-left\">");
		} else {
			prefixSb.append("class=\"article\" style=\"margin-left:10px\">");
		}
		return prefixSb.toString();
	}

	protected String createLinkToFile(File file) {
		String pathToFile;
		try {
			pathToFile = file.toURI().toURL().toExternalForm();
		} catch (MalformedURLException e) {
			pathToFile = file.getAbsolutePath();
		}
		if (pathToFile.endsWith(".js")) {
			return "<script type=\"text/javascript\" src=\"" + pathToFile + "\"></script>\n";
		}
		return "<link rel=\"stylesheet\" href=\"" + pathToFile + "\">\n";
	}

	protected void initTempFolderOrFail() {
		try {
			tempFolder = Files.createTempDirectory("ascii-doctor-eclipse");
			tempFolder.toFile().deleteOnExit();
		} catch (IOException e) {
			throw new IllegalStateException("Not able to provide tempfolder", e);
		}
	}

	private Map<String, Object> getDefaultOptions(File baseDir, String sourceImagesDir) {
		/* @formatter:off*/
		Attributes attrs;
		File targetImagesDir =null;
		if (tempFolder == null) {
			initTempFolderOrFail();
		}
		targetImagesDir = new File(tempFolder.toFile(),"images");
		targetImagesDir.deleteOnExit();
		copyImagesToOutputFolder(sourceImagesDir,targetImagesDir);
		
		AttributesBuilder attrBuilder = AttributesBuilder.
				attributes().
					showTitle(true).
					sourceHighlighter("coderay").
					attribute("imagesoutdir", createAbsolutePath(targetImagesDir.toPath())).
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
		}
		if (targetImagesDir!=null){
			attrBuilder.imagesDir(targetImagesDir.getAbsolutePath());
		}
		
		
		attrs=attrBuilder.get();
		if (tempFolder != null) {
			System.out.println("Tempfolder:" + tempFolder);
			attrs.setAttribute("outdir", createAbsolutePath(tempFolder));
		}
		File destionationFolder= null;
		if (tempFolder!=null){
			destionationFolder= tempFolder.toFile();
		}else{
			destionationFolder= baseDir;
		}
		
		OptionsBuilder opts = OptionsBuilder.options().
				toDir(destionationFolder).
				safe(SafeMode.UNSAFE).
				backend("html5").
				headerFooter(tocVisible).
				attributes(attrs).
				option("sourcemap", "true").
				baseDir(baseDir);
		/* @formatter:on*/
		return opts.asMap();
	}

	private void copyImagesToOutputFolder(String sourcePath, File target) {
		File cachedImagesFile = new File(sourcePath);
		if (!cachedImagesFile.exists()){
			return;
		}
		try {
			FileUtils.copyDirectory(cachedImagesFile, target);
		} catch (IOException e) {
			logAdapter.logError("Cannot copy images", e);
		}
		
	}

	protected String createAbsolutePath(Path path) {
		return path.toAbsolutePath().normalize().toString();
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
