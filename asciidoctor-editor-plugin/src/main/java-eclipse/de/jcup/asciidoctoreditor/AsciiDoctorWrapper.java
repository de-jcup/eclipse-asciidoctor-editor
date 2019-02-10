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
import java.nio.file.Path;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.asciidoctor.Asciidoctor;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferenceConstants;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.provider.AsciiDoctorOptionsProvider;
import de.jcup.asciidoctoreditor.provider.AsciiDoctorProviderContext;
import de.jcup.asciidoctoreditor.provider.EclipseAsciiDoctorProvider;
import de.jcup.asciidoctoreditor.provider.ImageHandlingMode;

public class AsciiDoctorWrapper {

	private LogAdapter logAdapter;
	private AsciiDoctorWrapperHTMLBuilder htmlBuilder;

	private AsciiDoctorProviderContext context;
	private Path tempFolder;

	public AsciiDoctorWrapper(IProject project, LogAdapter logAdapter) {
		if (logAdapter == null) {
			throw new IllegalArgumentException("log adapter may not be null!");
		}
		this.logAdapter = logAdapter;
		this.tempFolder = createTempPath(project);
		this.context = new AsciiDoctorProviderContext(EclipseAsciiDoctorProvider.INSTANCE, AsciiDoctorEclipseLogAdapter.INSTANCE);
		this.htmlBuilder = new AsciiDoctorWrapperHTMLBuilder(context);

	}

	public AsciiDoctorProviderContext getContext() {
		return context;
	}

	public static class WrapperConvertData {
		EditorType targetType = EditorType.ASCIIDOC;
		File asciiDocFile;
		long editorId;
		boolean useHiddenFile;
		File editorFileOrNull;
	}

	public void convertToHTML(WrapperConvertData data) throws Exception {

		init(context, data);

		context.setAsciidocFile(data.asciiDocFile);
		if (data.useHiddenFile) {
			context.setFileToRender(AsciiDocFileUtils.createHiddenEditorFile(logAdapter, data.asciiDocFile, data.editorId, context.getBaseDir(), getTempFolder()));
		} else {
			context.setFileToRender(data.asciiDocFile);
		}

		AsciiDoctorEditorPreferences preferences = AsciiDoctorEditorPreferences.getInstance();
		int tocLevels = preferences.getIntegerPreference(AsciiDoctorEditorPreferenceConstants.P_EDITOR_TOC_LEVELS);
		context.setTocLevels(tocLevels);
		try {
			AsciiDoctorOptionsProvider optionsProvider = context.getOptionsProvider();
			Map<String, Object> defaultOptions = optionsProvider.createDefaultOptions();

			Asciidoctor asciiDoctor = context.getAsciiDoctor();
			asciiDoctor.convertFile(context.getFileToRender(), defaultOptions);

			refreshParentFolderIfNecessary();

		} catch (Exception e) {
			logAdapter.logError("Cannot convert to html:" + data.asciiDocFile, e);
			throw e;
		}
	}

	private void refreshParentFolderIfNecessary() {
		if (context.getImageHandlingMode() != ImageHandlingMode.STORE_DIAGRAM_FILES_LOCAL) {
			return;
		}
		File editorFileOrNull = context.getEditorFileOrNull();
		if (editorFileOrNull == null) {
			return;
		}
		IFile asFile = EclipseResourceHelper.DEFAULT.toIFile(editorFileOrNull);
		if (asFile==null) {
			return;
		}
		IContainer parent = asFile.getParent();
		if (parent==null) {
			return;
		}
		try {
			parent.refreshLocal(IFile.DEPTH_ONE, null);
		} catch (CoreException e) {
			AsciiDoctorEditorUtil.logError("Refresh was not possible",e);
		}
	}

	private void init(AsciiDoctorProviderContext context, WrapperConvertData data) {
		context.setUseInstalled(AsciiDoctorEditorPreferences.getInstance().isUsingInstalledAsciidoctor());
		context.setEditorFileOrNull(data.editorFileOrNull);

		EditorType type = data.targetType;
		if (type == EditorType.ASCIIDOC) {
			if (AsciiDoctorEditorPreferences.getInstance().isUsingPreviewImageDirectory()) {
				context.setImageHandlingMode(ImageHandlingMode.IMAGESDIR_FROM_PREVIEW_DIRECTORY);
			} else {
				context.setImageHandlingMode(ImageHandlingMode.RELATIVE_PATHES);
			}
		} else {
			if (type == EditorType.PLANTUML) {
				if (AsciiDoctorEditorPreferences.getInstance().isStoringPlantUmlFiles()) {
					context.setImageHandlingMode(ImageHandlingMode.STORE_DIAGRAM_FILES_LOCAL);
				} else {
					context.setImageHandlingMode(ImageHandlingMode.IMAGESDIR_FROM_PREVIEW_DIRECTORY);
				}
			} else {
				/* currently all other editor types ( ditaa) will use images dir approach */
				context.setImageHandlingMode(ImageHandlingMode.IMAGESDIR_FROM_PREVIEW_DIRECTORY);
			}
		}
		context.setOutputFolder(getTempFolder());
	}

	/**
	 * Resets cached values: baseDir, imagesDir
	 */
	public void resetCaches() {
		context.reset();
	}

	public Path getTempFolder() {
		return tempFolder;
	}

	private Path createTempPath(IProject project) {
		String id = "fallback";
		if (project != null) {
			IProjectDescription description;
			try {
				description = project.getDescription();
				id = description.getName() + project.hashCode();
			} catch (CoreException e) {
				id = "" + project.hashCode();
			}
		}
		return AsciiDocFileUtils.createTempFolderForId(id);
	}

	public File getTempFileFor(File editorFile, long editorId, TemporaryFileType type) {
		File parent = getTempFolder().toFile();

		String baseName = FilenameUtils.getBaseName(editorFile.getName());
		StringBuilder sb = new StringBuilder();
		if (!(editorFile.getName().startsWith("" + editorId))) {
			sb.append(editorId);
			sb.append("_");
		}
		sb.append(type.getPrefix());
		sb.append(baseName);
		sb.append(".html");
		return new File(parent, sb.toString());
	}

	public void dispose() {
		// no longer special handling -e.g. delete temp folder, because
		// tempfolder for projects and not longer for only one single editor!
	}

	public void setTocVisible(boolean tocVisible) {
		this.context.setTOCVisible(tocVisible);
	}

	public boolean isTocVisible() {
		return context.isTOCVisible();
	}

	public File getAddonsFolder() {
		return AsciiDoctorOSGIWrapper.INSTANCE.getAddonsFolder();
	}

	public String buildHTMLWithCSS(String html, int refreshAutomaticallyInSeconds) {
		return htmlBuilder.buildHTMLWithCSS(html, refreshAutomaticallyInSeconds);
	}

}
