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
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.asciidoctor.Asciidoctor;

public class AsciiDoctorWrapper {
	

	private boolean tocVisible;
	private LogAdapter logAdapter;

	private Path tempFolder;

	private AsciiDoctorSupportContext context;


	public AsciiDoctorWrapper(LogAdapter logAdapter) {
		if (logAdapter==null){
			throw new IllegalArgumentException("log adapter may not be null!");
		}
		this.logAdapter=logAdapter;
		initTempFolderOrFail();
		this.context = new AsciiDoctorSupportContext();
		context.outputFolder=tempFolder;
		context.attributesSupport=new AsciiDoctorAttributesSupport(context);
		context.imageSupport=new AsciiDoctorOutputImageSupport(context);
		context.optionsSupport = new AsciiDoctorOptionsSupport(context);
		
	}

	public void convertToHTML(File asciiDocFile) throws Exception{
		context.asciidocFile=asciiDocFile;
		try{
			getAsciiDoctor().convertFile(asciiDocFile, context.optionsSupport.createDefaultOptions());
		}catch(Exception e){
			logAdapter.logError("Cannot convert to html:"+asciiDocFile, e);
			throw e;
		}
	}

	private Asciidoctor getAsciiDoctor() {
		return AsciiDoctorOSGIWrapper.INSTANCE.getAsciidoctor();
	}

	
	/**
	 * Resets cached values: baseDir, imagesDir
	 */
	public void resetCaches() {
		context.reset();
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
		context.setAsciidocFile(null);
		try{
			return getAsciiDoctor().convert(asciiDoc, context.optionsSupport.createDefaultOptions());
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

	

	

	public File getTempFileFor(File editorFile, TemporaryFileType type) {
		File parent = null;
		if (tempFolder == null) {
			parent = new File(".");
		} else {
			parent = tempFolder.toFile();
		}
		String baseName = FilenameUtils.getBaseName(editorFile.getName());
		StringBuilder sb = new StringBuilder();
		sb.append(type.getPrefix());
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
