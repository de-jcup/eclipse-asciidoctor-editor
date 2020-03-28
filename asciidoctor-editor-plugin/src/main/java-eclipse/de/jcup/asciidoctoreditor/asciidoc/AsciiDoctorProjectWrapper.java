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
package de.jcup.asciidoctoreditor.asciidoc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

import de.jcup.asciidoctoreditor.AsciiDoctorEclipseLogAdapter;
import de.jcup.asciidoctoreditor.EclipseResourceHelper;
import de.jcup.asciidoctoreditor.EditorType;
import de.jcup.asciidoctoreditor.LogAdapter;
import de.jcup.asciidoctoreditor.PluginContentInstaller;
import de.jcup.asciidoctoreditor.console.AsciiDoctorConsoleUtil;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferenceConstants;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.provider.AsciiDoctorOptionsProvider;
import de.jcup.asciidoctoreditor.provider.AsciiDoctorProjectProviderContext;
import de.jcup.asciidoctoreditor.provider.ImageHandlingMode;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;
import de.jcup.asp.client.AspClientProgressMonitor;

public class AsciiDoctorProjectWrapper {

    private LogAdapter logAdapter;
    private AsciiDoctorWrapperHTMLBuilder htmlBuilder;

    private AsciiDoctorProjectProviderContext context;

    public AsciiDoctorProjectWrapper(IProject project, LogAdapter logAdapter) {
        if (logAdapter == null) {
            throw new IllegalArgumentException("log adapter may not be null!");
        }
        if (project == null) {
            throw new IllegalArgumentException("project may not be null!");
        }
        this.logAdapter = logAdapter;
        File projectLocation = project.getLocation().toFile();
        this.context = new AsciiDoctorProjectProviderContext(projectLocation, project.getName(), createTempPath(project), EclipseAsciiDoctorAdapterProvider.INSTANCE,
                AsciiDoctorEclipseLogAdapter.INSTANCE);
        this.htmlBuilder = new AsciiDoctorWrapperHTMLBuilder(context);

    }

    public AsciiDoctorProjectProviderContext getContext() {
        return context;
    }

    public void convert(WrapperConvertData data, AspClientProgressMonitor monitor) throws Exception {
        try {
            updateContext(context, data);

            AsciiDoctorOptionsProvider optionsProvider = context.getOptionsProvider();
            Map<String, Object> defaultOptions = optionsProvider.createDefaultOptions(data.backendType);

            AsciidoctorAdapter asciiDoctor = context.getAsciiDoctor();
            asciiDoctor.convertFile(data.editorFileOrNull, context.getFileToRender(), defaultOptions, monitor);

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
        if (asFile == null) {
            return;
        }
        IContainer parent = asFile.getParent();
        if (parent == null) {
            return;
        }
        try {
            parent.refreshLocal(IFile.DEPTH_ONE, null);
        } catch (CoreException e) {
            AsciiDoctorEditorUtil.logError("Refresh was not possible", e);
        }
    }

    private void updateContext(AsciiDoctorProjectProviderContext context, WrapperConvertData data) throws IOException {
        AsciiDoctorEditorPreferences preferences = AsciiDoctorEditorPreferences.getInstance();

        context.setAsciidocFile(data.asciiDocFile);

        context.setInternalPreview(data.internalPreview);
        context.setUseInstalled(preferences.isUsingInstalledAsciidoctor());
        context.setEditorFileOrNull(data.editorFileOrNull);
        int tocLevels = preferences.getIntegerPreference(AsciiDoctorEditorPreferenceConstants.P_EDITOR_TOC_LEVELS);
        context.setTocLevels(tocLevels);
        context.setImageHandlingMode(ImageHandlingMode.DEFAULT);

        EditorType type = data.targetType;
        if (type == EditorType.PLANTUML) {
            if (AsciiDoctorEditorPreferences.getInstance().isStoringPlantUmlFiles()) {
                context.setImageHandlingMode(ImageHandlingMode.STORE_DIAGRAM_FILES_LOCAL);
            }
        }
        context.setNoFooter(true);

        File hiddenEditorTempFile = context.getTempFileProvider().createHiddenEditorTempFile(data.asciiDocFile, data.editorId);

        context.setOutputFolder(hiddenEditorTempFile.getParentFile().toPath());

        if (data.useHiddenFile) {
            context.setFileToRender(hiddenEditorTempFile);
        } else {
            context.setFileToRender(data.asciiDocFile);
        }

    }

    /**
     * Resets cached values: baseDir, imagesDir
     */
    public void resetCaches() {
        if (context == null) {
            return;
        }
        context.reset();
    }

    public void reinitContext() {
        resetCaches();
        AsciiDoctorConsoleUtil.output("- cleaned caches");
        this.context = new AsciiDoctorProjectProviderContext(context.getProjectLocation(), context.getProjectName(), context.getTempFolder(), EclipseAsciiDoctorAdapterProvider.INSTANCE,
                AsciiDoctorEclipseLogAdapter.INSTANCE);
        AsciiDoctorConsoleUtil.output("- context recreated");
    }

    public void deleteTempFolder() {
        Path tempFolder = getTempFolder();
        String pathAsString = tempFolder.toAbsolutePath().toString();
        deleteFolder(tempFolder, "- deleted temp folder:" + pathAsString, "Wasn't able to delete temp folder:" + pathAsString);
    }

    private void deleteFolder(Path outputFolder, String successMessage, String errorMessage) {
        if (outputFolder == null) {
            return;
        }

        try {
            File file = outputFolder.toFile();
            FileUtils.deleteDirectory(file);

            AsciiDoctorConsoleUtil.output(successMessage);
        } catch (IOException e) {
            AsciiDoctorEditorUtil.logError(errorMessage, e);
            AsciiDoctorConsoleUtil.error(errorMessage);
        }
    }

    public Path getTempFolder() {
        return getContext().getTempFolder();
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

    public void dispose() {
        // no longer special handling -e.g. delete temp folder, because
        // temp folder for projects and not longer for only one single editor!
    }

    public void setTocVisible(boolean tocVisible) {
        this.context.setTOCVisible(tocVisible);
    }

    public boolean isTocVisible() {
        return context.isTOCVisible();
    }

    public File getAddonsFolder() {
        return PluginContentInstaller.INSTANCE.getAddonsFolder();
    }

    public String buildHTMLWithCSS(String html, int refreshAutomaticallyInSeconds) {
        return htmlBuilder.buildHTMLWithCSS(html, refreshAutomaticallyInSeconds);
    }

}
