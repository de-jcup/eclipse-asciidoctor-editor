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
package de.jcup.asciidoctoreditor.provider;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.jcup.asciidoctoreditor.EclipseDevelopmentSettings;
import de.jcup.asciidoctoreditor.LogAdapter;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocConfigFileSupport;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocFileUtils;
import de.jcup.asciidoctoreditor.asciidoc.AsciidoctorAdapter;
import de.jcup.asciidoctoreditor.asciidoc.AsciidoctorConfigFile;
import de.jcup.asp.api.asciidoc.AsciidocOption;

public class AsciiDoctorWrapperContext {

    private LogAdapter logAdapter;
    private File asciiDocFile;
    /**
     * Base dir used for asciidoctor rendering - is either the project base dir, or
     * a configured directory by a asciidoctorconfig file using "base_dir" option
     * attribute
     */
    private File baseDir;

    /**
     * The project base directory. it is calculated/estimated by the editor plugin
     */
    private File projectBaseDir;

    private Path outputFolder;
    private boolean tocVisible;
    private AsciiDoctorAdapterProvider provider;

    private AsciiDoctorBaseDirectoryProvider baseDirProvider;
    private AsciiDoctorImageProvider imageProvider;
    private AsciiDoctorDiagramProvider diagramProvider;
    private AsciiDoctorAttributesProvider attributesProvider;
    private AsciiDoctorOptionsProvider optionsProvider;

    int tocLevels;
    private boolean useInstalled;
    private File fileToRender;
    private ImageHandlingMode imageHandlingMode;

    private Set<AbstractAsciiDoctorProvider> providers = new LinkedHashSet<>();
    private File editorFileOrNull;
    private boolean noFooter;
    private boolean internalPreview;
    private boolean localResourcesEnabled = true;
    private AsciiDocConfigFileSupport configFileSupport;
    private List<AsciidoctorConfigFile> configFiles = new ArrayList<>();
    private File configRoot;

    public AsciiDoctorWrapperContext(AsciiDoctorAdapterProvider provider, LogAdapter logAdapter) {
        if (logAdapter == null) {
            throw new IllegalArgumentException("logAdapter may never be null!");
        }
        if (provider == null) {
            throw new IllegalArgumentException("provider may never be null!");
        }
        this.logAdapter = logAdapter;
        this.provider = provider;

        init();
    }

    public Path getOutputFolder() {
        return outputFolder;
    }

    public void setTocLevels(int tocLevels) {
        this.tocLevels = tocLevels;
    }

    public AsciiDoctorBaseDirectoryProvider getBaseDirProvider() {
        return baseDirProvider;
    }

    public AsciiDoctorImageProvider getImageProvider() {
        return imageProvider;
    }

    public AsciiDoctorDiagramProvider getDiagramProvider() {
        return diagramProvider;
    }

    public AsciiDoctorAttributesProvider getAttributesProvider() {
        return attributesProvider;
    }

    public AsciiDoctorOptionsProvider getOptionsProvider() {
        return optionsProvider;
    }

    public void setImageHandlingMode(ImageHandlingMode imageHandlingMode) {
        this.imageHandlingMode = imageHandlingMode;
    }

    public ImageHandlingMode getImageHandlingMode() {
        return imageHandlingMode;
    }

    protected void init() {
        boolean debugLoggingEnabled = EclipseDevelopmentSettings.DEBUG_LOGGING_ENABLED;
        
        if (debugLoggingEnabled) {
            logAdapter.resetTimeDiff();
        }
        attributesProvider = register(new AsciiDoctorAttributesProvider(this));
        if (debugLoggingEnabled) {
            logAdapter.logTimeDiff("time to create attributes provider");
        }
        imageProvider = register(new AsciiDoctorImageProvider(this));
        if (debugLoggingEnabled) {
            logAdapter.logTimeDiff("time to create images provider");
        }
        optionsProvider = register(new AsciiDoctorOptionsProvider(this));
        if (debugLoggingEnabled) {
            logAdapter.logTimeDiff("time to create options provider");
        }
        baseDirProvider = register(new AsciiDoctorBaseDirectoryProvider(this));
        if (debugLoggingEnabled) {
            logAdapter.logTimeDiff("time to create base dir provider");
        }
        diagramProvider = register(new AsciiDoctorDiagramProvider(this));
        if (debugLoggingEnabled) {
            logAdapter.logTimeDiff("time to create diagram provider");
        }
    }

    public void setOutputFolder(Path outputFolder) {
        this.outputFolder = outputFolder;
    }

    /**
     * Reset context. After this method is called all cached operations will be
     * recalculated on next rendering time fo editor content
     */
    public void resetCaches() {
        this.asciiDocFile = null;
        this.baseDir = null;
        this.projectBaseDir = null;
        this.outputFolder = null;

        for (AbstractAsciiDoctorProvider provider : providers) {
            provider.reset();
        }
    }

    public AsciidoctorAdapter getAsciiDoctor() {
        return getProvider().getAsciiDoctor(useInstalled);
    }

    protected AsciiDoctorAdapterProvider getProvider() {
        return provider;
    }

    public void setTOCVisible(boolean visible) {
        this.tocVisible = visible;
    }

    public boolean isTOCVisible() {
        return tocVisible;
    }

    public File getProjectBaseDir() {
        if (projectBaseDir == null) {
            projectBaseDir = baseDirProvider.findProjectBaseDir();
        }
        return projectBaseDir;
    }

    public File getBaseDir() {
        if (baseDir == null) {

            Map<String, Object> attributes = getAttributesProvider().getCachedAttributesOverridenByCustomAttributesFromPreferences();
            Object baseDirFromAttributesObj = attributes.get(AsciidocOption.BASEDIR.getKey());
            if (baseDirFromAttributesObj instanceof String) {
                String baseDirFromAttributes = baseDirFromAttributesObj.toString();
                this.baseDir = new File(baseDirFromAttributes);
                if (EclipseDevelopmentSettings.DEBUG_LOGGING_ENABLED) {
                    System.out.println("Using base dir from attributes:" + baseDirFromAttributes);
                }
            } else {
                this.baseDir = getProjectBaseDir();
            }
        }
        return baseDir;
    }

    public void initConfigFileSupportAndSetConfigRoot(File configRoot) {
        if (configRoot == null) {
            configRoot = getBaseDir();
        }

        this.configRoot = configRoot;
        this.configFileSupport = new AsciiDocConfigFileSupport(configRoot.toPath());
    }

    public LogAdapter getLogAdapter() {
        return logAdapter;
    }

    public File getAsciiDocFile() {
        return asciiDocFile;
    }

    public void setUseInstalled(boolean usingInstalledAsciidoctor) {
        useInstalled = usingInstalledAsciidoctor;
    }

    public boolean isUsingInstalledAsciiDoctor() {
        return useInstalled;
    }

    public void setFileToRender(File fileToRender) {
        this.fileToRender = fileToRender;
    }

    /**
     * 
     * @return the file to render, means the asciidoc file (either editor file or a
     *         temporary one which is generated in background)
     */
    public File getFileToRender() {
        return fileToRender;
    }

    /**
     * 
     * @return target pdf file or <code>null</code>
     */
    public File getTargetPDFFileOrNull() {
        return getTargetForFileEndingOrNull(".pdf");
    }

    private File getTargetForFileEndingOrNull(String ending) {
        if (fileToRender == null) {
            return null;
        }
        String originName = fileToRender.getName(); /* xyz.adoc, xyz.asciidoc, xyz, xyz.txt */
        String targetName = AsciiDocFileUtils.createTargetName(originName, ending);
        File file = new File(fileToRender.getParentFile(), targetName);
        return file;
    }

    public <T extends AbstractAsciiDoctorProvider> T register(T provider) {
        providers.add(provider);
        return provider;
    }

    public void setEditorFileOrNull(File editorFileOrNull) {
        this.editorFileOrNull = editorFileOrNull;
    }

    public File getEditorFileOrNull() {
        return editorFileOrNull;
    }

    public void setNoFooter(boolean noFooter) {
        this.noFooter = noFooter;
    }

    public boolean isNoFooter() {
        return noFooter;
    }

    public void setInternalPreview(boolean internalPreview) {
        this.internalPreview = internalPreview;
    }

    public boolean isInternalPreview() {
        return internalPreview;
    }

    public boolean isUsingOnlyLocalResources() {
        /*
         * internal preview has problems on enterprise proxies and slows down rendering
         * of preview
         */
        return internalPreview && localResourcesEnabled;
    }

    public AsciiDocConfigFileSupport getConfigFileSupport() {
        return configFileSupport;
    }

    /**
     * Set last first inside list the most far one (near parent), last one the
     * nearest!
     * 
     * @param configFiles
     */
    public void setConfigFiles(List<AsciidoctorConfigFile> configFiles) {
        this.configFiles.clear();
        this.configFiles.addAll(configFiles);
    }

    /**
     * 
     * @return config files : first one inside list the most far one (near parent),
     *         last one the nearest!
     */
    public List<AsciidoctorConfigFile> getConfigFiles() {
        return configFiles;
    }

    public File getConfigRoot() {
        return configRoot;
    }

}
