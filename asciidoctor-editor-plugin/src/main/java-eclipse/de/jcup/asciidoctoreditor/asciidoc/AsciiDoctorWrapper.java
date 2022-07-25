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
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.jcup.asciidoctoreditor.AsciiDoctorEclipseLogAdapter;
import de.jcup.asciidoctoreditor.EclipseResourceHelper;
import de.jcup.asciidoctoreditor.EditorType;
import de.jcup.asciidoctoreditor.LogAdapter;
import de.jcup.asciidoctoreditor.PluginContentInstaller;
import de.jcup.asciidoctoreditor.TemporaryFileType;
import de.jcup.asciidoctoreditor.UniqueIdProvider;
import de.jcup.asciidoctoreditor.console.AsciiDoctorConsoleUtil;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferenceConstants;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.provider.AsciiDoctorAttributesProvider;
import de.jcup.asciidoctoreditor.provider.AsciiDoctorOptionsProvider;
import de.jcup.asciidoctoreditor.provider.AsciiDoctorProviderContext;
import de.jcup.asciidoctoreditor.provider.ImageHandlingMode;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;
import de.jcup.asp.api.asciidoc.AsciidocAttributes;
import de.jcup.asp.api.asciidoc.AsciidocOptions;
import de.jcup.asp.client.AspClientProgressMonitor;

public class AsciiDoctorWrapper {

    private LogAdapter logAdapter;
    private AsciiDoctorWrapperHTMLBuilder htmlBuilder;

    private AsciiDoctorProviderContext context;
    private Path tempFolder;
    private IProject project;

    public AsciiDoctorWrapper(IProject project, LogAdapter logAdapter) {
        if (logAdapter == null) {
            throw new IllegalArgumentException("log adapter may not be null!");
        }
        this.project = project;
        this.logAdapter = logAdapter;
        this.tempFolder = createTempPath(project);

        this.context = new AsciiDoctorProviderContext(EclipseAsciiDoctorAdapterProvider.INSTANCE, AsciiDoctorEclipseLogAdapter.INSTANCE);
        this.htmlBuilder = new AsciiDoctorWrapperHTMLBuilder(context);

    }

    public AsciiDoctorProviderContext getContext() {
        return context;
    }

    public void convert(WrapperConvertData data, AsciiDoctorBackendType asciiDoctorBackendType, AspClientProgressMonitor monitor) throws Exception {
        try {
            /* @formatter:on */
            initContext(context, data);

            /* build attributes */
            AsciiDoctorAttributesProvider attributesProvider = context.getAttributesProvider();
            AsciidocAttributes attributes = attributesProvider.createAttributes();

            /* build options - containing attribute parameters */
            AsciiDoctorOptionsProvider optionsProvider = context.getOptionsProvider();
            AsciidocOptions options = optionsProvider.createOptions(asciiDoctorBackendType);

            /* start conversion by asciidoctor */
            AsciidoctorAdapter asciiDoctorAdapter = context.getAsciiDoctor();
            File fileToRender = context.getFileToRender();
            asciiDoctorAdapter.convertFile(data.editorFileOrNull, fileToRender, options, attributes, monitor);
            
            refreshParentFolderIfNecessary();
            /* @formatter:off */

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

    private void initContext(AsciiDoctorProviderContext context, WrapperConvertData data) throws IOException {
        AsciiDoctorEditorPreferences preferences = AsciiDoctorEditorPreferences.getInstance();

        context.setInternalPreview(data.internalPreview);
        context.setUseInstalled(preferences.isUsingInstalledAsciidoctor());
        context.setEditorFileOrNull(data.editorFileOrNull);
        int tocLevels = preferences.getIntegerPreference(AsciiDoctorEditorPreferenceConstants.P_EDITOR_TOC_LEVELS);
        context.setTocLevels(tocLevels);

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
            context.setNoFooter(true);
        }
        context.setOutputFolder(getTempFolder());

        context.setAsciidocFile(data.asciiDocFile);
        
        /* setup auto config creation - as configured */
        boolean autoCreateConfigEnabled = AsciiDoctorEditorPreferences.getInstance().isAutoCreateConfigEnabled();

        AsciiDocConfigFileSupport configFileSupport = context.getConfigFileSupport();
        configFileSupport.setAutoCreateConfig(autoCreateConfigEnabled);
        if (project!=null) {
            configFileSupport.setAutoCreateConfigCallback(()->{
                createRefreshAutoConfigFolderJob(project).schedule();
            });
        }else {
            configFileSupport.setAutoCreateConfigCallback(null);
        }
        
        List<AsciidoctorConfigFile> configFiles = configFileSupport.collectConfigFiles(context.getAsciiDocFile().toPath());
        context.setConfigFiles(configFiles);
        if (data.useHiddenFile) {
            /* asciidoc files ... we create the hidden file which references the origin one*/
            File createdHiddenEditorFile = AsciiDocFileUtils.createHiddenEditorFile(logAdapter, data.asciiDocFile, data.editorId, context.getBaseDir(), getTempFolder(),configFiles, context.getConfigRoot().getAbsolutePath());
            context.setFileToRender(createdHiddenEditorFile);
        } else {
            /* PlantUML, ditaa files ...*/
            context.setFileToRender(data.asciiDocFile);
        }

    }
    
    private Job createRefreshAutoConfigFolderJob(final IProject project) {
       return new RefreshAFterAutoCreateConfigurationFileJob(project);
    }
    
    private class RefreshAFterAutoCreateConfigurationFileJob extends Job{

        private IProject project;

        public RefreshAFterAutoCreateConfigurationFileJob(IProject project) {
            super("Refresh because auto configuration added");
            this.project=project;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            try {
                monitor.beginTask("refresh folder because asciidoc config automatically added", 1);
                /* refresh */
                project.refreshLocal(IResource.DEPTH_INFINITE, null);
                monitor.worked(1);

                return Status.OK_STATUS;
            } catch (CoreException e) {
                return e.getStatus();
            }
        }
        
    }
    
    /**
     * Resets cached values: baseDir, imagesDir
     */
    public void resetCaches() {
        if (context == null) {
            return;
        }
        context.resetCaches();
    }
    
    public void deleteTempFolder() {
        Path tempFolder = getTempFolder();
        String pathAsString = tempFolder.toAbsolutePath().toString();
        deleteFolder(tempFolder, "- deleted temp folder:"+pathAsString,"Wasn't able to delete temp folder:"+pathAsString);
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
        return tempFolder;
    }

    private Path createTempPath(IProject project) {
        String projectName = "fallback-projectname";
        if (project != null) {
            IProjectDescription description;
            try {
                description = project.getDescription();
                projectName = description.getName();
            } catch (CoreException e) {
                projectName = "" + project.getName();
            }
        }
        return AsciiDocFileUtils.createTempFolderForId(projectName);
    }

    public File getTempFileFor(File editorFile, UniqueIdProvider uniqueIdProvider, TemporaryFileType type) {
        File parent = getTempFolder().toFile();

        String baseName = FilenameUtils.getBaseName(editorFile.getName());
        StringBuilder sb = new StringBuilder();
        if (!(editorFile.getName().startsWith(uniqueIdProvider.getUniqueId()))) {
            sb.append(uniqueIdProvider.getUniqueId());
            sb.append("_");
        }
        sb.append(type.getPrefix());
        sb.append(baseName);
        sb.append(".html");
        
        return  new File(parent, sb.toString());
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

    /**
     * Enrich given HTML with CSS and additional javascript
     * @param html
     * @param refreshAutomaticallyInSeconds
     * @return
     */
    public String enrichHTML(String html, int refreshAutomaticallyInSeconds) {
        return htmlBuilder.buildHTMLWithCSS(html, refreshAutomaticallyInSeconds);
    }

}
