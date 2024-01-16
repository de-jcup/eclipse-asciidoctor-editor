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
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorInput;

import de.jcup.asciidoctoreditor.AsciiDoctorEclipseLogAdapter;
import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.EclipseResourceHelper;
import de.jcup.asciidoctoreditor.EditorType;
import de.jcup.asciidoctoreditor.LogAdapter;
import de.jcup.asciidoctoreditor.PluginContentInstaller;
import de.jcup.asciidoctoreditor.TemporaryFileType;
import de.jcup.asciidoctoreditor.UniqueIdProvider;
import de.jcup.asciidoctoreditor.asciidoc.debug.AsciidocFileDebugInfoCollector;
import de.jcup.asciidoctoreditor.asciidoc.debug.VarContentDumper;
import de.jcup.asciidoctoreditor.console.AsciiDoctorConsoleUtil;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferenceConstants;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.provider.AsciiDoctorAttributesProvider;
import de.jcup.asciidoctoreditor.provider.AsciiDoctorDiagramProvider;
import de.jcup.asciidoctoreditor.provider.AsciiDoctorImageProvider;
import de.jcup.asciidoctoreditor.provider.AsciiDoctorOptionsProvider;
import de.jcup.asciidoctoreditor.provider.AsciiDoctorWrapperContext;
import de.jcup.asciidoctoreditor.provider.ImageHandlingMode;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;
import de.jcup.asp.api.asciidoc.AsciidocAttributes;
import de.jcup.asp.api.asciidoc.AsciidocOptions;
import de.jcup.asp.client.AspClientProgressMonitor;

/**
 * A project specific wrapper to handle asciidoctor calls. The wrapper is
 * created per project and caches for the project paths etc. When the defined
 * project is null, some fallbacks are used.
 */
public class AsciiDoctorWrapper {

    private static final Path FALLBACK_TEMP_FOLDER_WHEN_NO_PROJECT_SET = AsciiDocFileUtils.createTempFolderForNoProject();
    private LogAdapter logAdapter;
    private AsciiDoctorWrapperHTMLBuilder htmlBuilder;

    private AsciiDoctorWrapperContext context;
    private Path tempFolder;
    private IProject project;
    private File tempGenFolder;
    private AsciiDoctorEditor editor;
    private static AsciiDoctorWrapper noProjectAsciidoctorWrapper;

    /**
     * A shared project wrapper, which has no project relation. Will use
     * {@value #AsciiDocFileUtils#PROJECT_NAME_FOR_NO_PROJECT} as fallback project
     * name for paths. Instance will be created lazily/on demand.
     * 
     * @return wrapper without project relation
     */
    public static AsciiDoctorWrapper getNoProjectAsciiDoctorWrapper() {
        if (noProjectAsciidoctorWrapper == null) {
            noProjectAsciidoctorWrapper = new AsciiDoctorWrapper(null, AsciiDoctorEclipseLogAdapter.INSTANCE);
        }
        return noProjectAsciidoctorWrapper;
    }

    public AsciiDoctorWrapper(AsciiDoctorEditor editor, LogAdapter logAdapter) {
        if (logAdapter == null) {
            throw new IllegalArgumentException("log adapter may not be null!");
        }
        this.editor = editor;
        this.logAdapter = logAdapter;

        this.project = null; // at this time, the editor does not know the project. Will be set afterwards in initialize method
        this.tempFolder = FALLBACK_TEMP_FOLDER_WHEN_NO_PROJECT_SET;

        this.context = new AsciiDoctorWrapperContext(EclipseAsciiDoctorAdapterProvider.INSTANCE, AsciiDoctorEclipseLogAdapter.INSTANCE);
        this.htmlBuilder = new AsciiDoctorWrapperHTMLBuilder(context);

    }

    public void initialize(LogAdapter logAdapter, IEditorInput editorInput) {
        try {
            File configRoot = null;
            try {
                IProject project = AsciiDoctorEditor.resolveProjectOrNull(editorInput);
                if (project != null) {
                    IPath projectLocation = project.getLocation();
                    this.tempFolder = createTempPath(project);
                    configRoot = EclipseResourceHelper.DEFAULT.toFile(projectLocation);
                }
            } catch (Exception e) {
                AsciiDoctorEclipseLogAdapter.INSTANCE.logError("Was not able to determine config root, fallback to base dir", e);
            }
            // We do not render here, but outline does also use the wrapper - so we need to
            // configure the context. Eg.g. to have configuration file support
            // initialized at the beginning to have calculations correct.
            ConversionData initialConversionData = createInitialConversionDataForCleanContext(editorInput);
            initContextAndResolveParameters(configRoot, initialConversionData, AsciiDoctorBackendType.HTML5);

        } catch (IOException e) {
            logAdapter.logError("Was not able to initialize provider context at startup", e);
        }
    }

    private AsciiDoctorWrapperContext getContext() {
        return context;
    }

    private ConversionData createInitialConversionDataForCleanContext(IEditorInput editorInput) {
        ConversionData data = new ConversionData();
        File editorInputFileOrNull = AsciiDoctorEditor.resolveFileOrNull(editorInput);
        if (editor == null) {
            // happens only for ".no-project" - we just ignore this case.
            // in this case we do not have editor files or any project information
        } else {
            data.setTargetType(editor.getType());
            data.setEditorId(editor.getEditorId());

        }
        data.setAsciiDocFile(editorInputFileOrNull);
        data.setEditorFileOrNull(editorInputFileOrNull);

        data.setUseHiddenFile(false);
        data.setInternalPreview(false);
        return data;
    }

    private class AttributesAndOptionsParameter {

        public AsciidocAttributes attributes;
        public AsciidocOptions options;

    }

    /**
     * Converts given data by internal {@link AsciidoctorAdapter}
     * 
     * @param data
     * @param asciiDoctorBackendType
     * @param monitor
     * @throws Exception
     */
    public void convert(ConversionData data, AsciiDoctorBackendType asciiDoctorBackendType, AspClientProgressMonitor monitor) throws Exception {
        try {
            AttributesAndOptionsParameter param = initContextAndResolveParameters(null, data, asciiDoctorBackendType);

            /* start conversion by asciidoctor */
            AsciidoctorAdapter asciiDoctorAdapter = context.getAsciiDoctor();
            File fileToRender = context.getFileToRender();
            asciiDoctorAdapter.convertFile(data.getEditorFileOrNull(), fileToRender, param.options, param.attributes, monitor);

        } catch (Exception e) {
            logAdapter.logError("Cannot convert to html:" + data.getAsciiDocFile(), e);
            throw e;
        }
    }

    private AttributesAndOptionsParameter initContextAndResolveParameters(File configRoot, ConversionData data, AsciiDoctorBackendType asciiDoctorBackendType) throws IOException {

        initProviderContext(context, data, configRoot);

        AttributesAndOptionsParameter param = createConvertParameter(asciiDoctorBackendType);
        return param;
    }

    private AttributesAndOptionsParameter createConvertParameter(AsciiDoctorBackendType asciiDoctorBackendType) {
        AttributesAndOptionsParameter param = new AttributesAndOptionsParameter();

        /* build attributes */
        AsciiDoctorAttributesProvider attributesProvider = context.getAttributesProvider();
        AsciidocAttributes attributes = attributesProvider.createAttributes();

        /* build options - containing attribute parameters */
        AsciiDoctorOptionsProvider optionsProvider = context.getOptionsProvider();
        AsciidocOptions options = optionsProvider.createOptions(asciiDoctorBackendType);

        param.attributes = attributes;
        param.options = options;
        return param;
    }

    private void initProviderContext(AsciiDoctorWrapperContext context, ConversionData data, File configRoot) throws IOException {
        File asciiDocFile = data.getAsciiDocFile();
        context.setConfigRoot(configRoot);

        List<AsciidoctorConfigFile> configFiles = initConfigFileSupport(context, asciiDocFile);

        AsciiDoctorEditorPreferences preferences = AsciiDoctorEditorPreferences.getInstance();

        context.setInternalPreview(data.isInternalPreview());
        context.setUseInstalled(preferences.isUsingInstalledAsciidoctor());
        context.setEditorFileOrNull(data.getEditorFileOrNull());
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
                    context.setImageHandlingMode(ImageHandlingMode.IMAGESDIR_FROM_PREVIEW_DIRECTORY);
            } else {
                /* currently all other editor types ( ditaa) will use images dir approach */
                context.setImageHandlingMode(ImageHandlingMode.IMAGESDIR_FROM_PREVIEW_DIRECTORY);
            }
            context.setNoFooter(true);
        }
        context.setOutputFolder(getTempFolder());

        if (data.isUseHiddenFile()) {
            /*
             * asciidoc files ... we create the hidden file which references the origin one
             */
            File createdHiddenEditorFile = AsciiDocFileUtils.createHiddenEditorFile(logAdapter, asciiDocFile, data.getEditorId(), context.getBaseDir(), getTempFolder(), configFiles,
                    context.resolveRootLocation().getAbsolutePath());
            context.setFileToRender(createdHiddenEditorFile);
        } else {
            /* PlantUML, ditaa files ... */
            context.setFileToRender(asciiDocFile);
        }

    }

    private List<AsciidoctorConfigFile> initConfigFileSupport(AsciiDoctorWrapperContext context, File asciiDocFile) {
        if (asciiDocFile == null) {
            return Collections.emptyList();
        }
        /* setup auto config creation - as configured */
        boolean autoCreateConfigEnabled = AsciiDoctorEditorPreferences.getInstance().isAutoCreateConfigEnabled();

        AsciiDocConfigFileSupport configFileSupport = context.getConfigFileSupport();
        List<AsciidoctorConfigFile> configFiles;
        if (configFileSupport != null) {
            configFileSupport.setAutoCreateConfig(autoCreateConfigEnabled);
            if (project != null) {
                configFileSupport.setAutoCreateConfigCallback(() -> {
                    createRefreshAutoConfigFolderJob(project).schedule();
                });
            } else {
                configFileSupport.setAutoCreateConfigCallback(null);
            }

            configFiles = configFileSupport.collectConfigFiles(asciiDocFile.toPath());
            context.setConfigFiles(configFiles);
        } else {
            configFiles = Collections.emptyList();
        }
        return configFiles;
    }

    private Job createRefreshAutoConfigFolderJob(final IProject project) {
        return new RefreshAFterAutoCreateConfigurationFileJob(project);
    }

    private class RefreshAFterAutoCreateConfigurationFileJob extends Job {

        private IProject project;

        public RefreshAFterAutoCreateConfigurationFileJob(IProject project) {
            super("Refresh because auto configuration added");
            this.project = project;
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

    public File getTempGenFolder() {
        if (tempGenFolder == null) {
            tempGenFolder = new File(getTempFolder().toFile(), "gen");
            tempGenFolder.mkdirs();
        }
        return tempGenFolder;
    }

    public Path getTempFolder() {
        return tempFolder;
    }

    public Path getBaseDir() {
        return getContext().getBaseDir().toPath();
    }

    private Path createTempPath(IProject project) {
        String projectName;
        if (project == null) {
            return AsciiDocFileUtils.createTempFolderForNoProject();
        }

        IProjectDescription description;
        try {
            description = project.getDescription();
            projectName = description.getName();
        } catch (CoreException e) {
            projectName = "" + project.getName();
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

        return new File(parent, sb.toString());
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
     * 
     * @param html
     * @param autoRefreshEnabled 
     * @param refreshAutomaticallyInSeconds
     * @return
     */
    public String enrichHTML(String html, boolean autoRefreshEnabled, int refreshAutomaticallyInSeconds) {
        return htmlBuilder.buildHTMLWithCSS(html, autoRefreshEnabled, refreshAutomaticallyInSeconds);
    }

    public Path getOutputFolder() {
        return getContext().getOutputFolder();
    }

    public AsciiDoctorDiagramProvider getDiagramProvider() {
        return getContext().getDiagramProvider();
    }

    public AsciiDoctorImageProvider getImageProvider() {
        return getContext().getImageProvider();
    }

    public File getBaseDirAsFile() {
        return getBaseDir().toFile();
    }

    public File getFileToRender() {
        return getContext().getFileToRender();
    }

    public File getTargetPDFFileOrNull() {
        return getContext().getTargetPDFFileOrNull();
    }

    public String createDump() {
        StringBuilder sb = new StringBuilder();

        VarContentDumper dump = new VarContentDumper();
        dump.add("OutputFolder", getOutputFolder());
        dump.add("BaseDir", getBaseDir());
        dump.add("DiagramRootDir", getDiagramProvider().getDiagramRootDirectory());
        dump.add("CachedSourceImagesPath", getImageProvider().getCachedSourceImagesPath());
        dump.add("Attributes", getContext().getAttributesProvider().createAttributes().toMap());
        dump.addNewLine();

        dump.add("FileToRender", getFileToRender());
        sb.append("Wrapper variables:\n");
        sb.append("------------------\n");
        sb.append(dump.toString());

        sb.append("\n");
        sb.append("Simple file walkthrough:\n");
        sb.append("------------------------\n");

        AsciidocFileDebugInfoCollector infoCollector = new AsciidocFileDebugInfoCollector();
        infoCollector.setBaseDir(getBaseDir());
        infoCollector.collect(getFileToRender());
        sb.append(infoCollector.createDump());
        
        return sb.toString();
    }
}
