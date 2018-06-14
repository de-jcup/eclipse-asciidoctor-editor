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
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.asciidoctor.Asciidoctor;

import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferenceConstants;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.provider.AsciiDoctorOptionsProvider;
import de.jcup.asciidoctoreditor.provider.AsciiDoctorProviderContext;

public class AsciiDoctorWrapper {
	

	private LogAdapter logAdapter;

	private Path tempFolder;

	private AsciiDoctorProviderContext context;

	public AsciiDoctorWrapper(long tempIdentifier, LogAdapter logAdapter) {
		if (logAdapter==null){
			throw new IllegalArgumentException("log adapter may not be null!");
		}
		this.logAdapter=logAdapter;
		initTempFolderOrFail(tempIdentifier);
		this.context = new AsciiDoctorProviderContext(AsciiDoctorOSGIWrapper.INSTANCE.getAsciidoctor(), AsciiDoctorEclipseLogAdapter.INSTANCE);
		context.setOutputFolder(tempFolder);
		
	}
	
	public AsciiDoctorProviderContext getContext() {
		return context;
	}

	public void convertToHTML(File asciiDocFile) throws Exception{
		context.setAsciidocFile(asciiDocFile);
		AsciiDoctorEditorPreferences preferences = AsciiDoctorEditorPreferences.getInstance();
		int tocLevels = preferences.getIntegerPreference(AsciiDoctorEditorPreferenceConstants.P_EDITOR_TOC_LEVELS);
		context.setTocLevels(tocLevels);
		try{
			AsciiDoctorOptionsProvider optionsProvider = context.getOptionsProvider();
			Map<String, Object> defaultOptions = optionsProvider.createDefaultOptions();
			
			Asciidoctor asciiDoctor = context.getAsciiDoctor();
			asciiDoctor.convertFile(asciiDocFile, defaultOptions);
		}catch(Exception e){
			logAdapter.logError("Cannot convert to html:"+asciiDocFile, e);
			throw e;
		}
	}

	
	/**
	 * Resets cached values: baseDir, imagesDir
	 */
	public void resetCaches() {
		context.reset();
		context.setOutputFolder(tempFolder);
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
		if (context.isTOCVisible()) {
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

	protected void initTempFolderOrFail(long tempIdentifier) {
		tempFolder = AsciiDocFileUtils.createTempFolderForEditor(tempIdentifier);
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
		this.context.setTOCVisible(tocVisible);
	}

	public boolean isTocVisible() {
		return context.isTOCVisible();
	}
}
