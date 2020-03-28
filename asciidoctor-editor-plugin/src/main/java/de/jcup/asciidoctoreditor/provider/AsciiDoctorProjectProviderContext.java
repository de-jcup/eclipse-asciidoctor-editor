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
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;

import de.jcup.asciidoctoreditor.LogAdapter;
import de.jcup.asciidoctoreditor.asciidoc.AsciidoctorAdapter;

/**
 * This context is available for all editors inside a project! So its necessary to set editor file before doing 
 * editor specific operations...
 * @author albert
 *
 */
public class AsciiDoctorProjectProviderContext {

    private LogAdapter logAdapter;
    private File asciidocFile;
    private Path outputFolder;
    private boolean tocVisible;
    private AsciiDoctorAdapterProvider provider;

    private AsciiDoctorRootDirectoryProvider rootDirectoryProvider;
    private AsciiDoctorImageCopyProvider imageCopyProvider;
    private AsciiDoctorDiagramProvider diagramProvider;
    private AsciiDoctorAttributesProvider attributesProvider;
    private AsciiDoctorOptionsProvider optionsProvider;
    private AsciiDoctorTempFileProvider tempFileProvider;
    private AsciiDoctorImageDirProvider imageDirProvider;

    File targetImagesDir;
    int tocLevels;
    private boolean useInstalled;
    private File fileToRender;
    private ImageHandlingMode imageHandlingMode;

    private Set<AbstractAsciiDoctorProvider> providers = new LinkedHashSet<>();
    private File editorFileOrNull;
    private boolean noFooter;
    private boolean internalPreview;
    private boolean localResourcesEnabled = true;
    private Path tempFolder;
    private File projectLocation;
    private String projectName;

    
    /**
     * Asciidoctor provider context
     * @param projectName the name of the project
     * @param tempFolder
     * @param provider
     * @param logAdapter
     */
    public AsciiDoctorProjectProviderContext(File projectLocation, String projectName, Path tempFolder, AsciiDoctorAdapterProvider provider, LogAdapter logAdapter) {
        if (projectLocation == null) {
            throw new IllegalArgumentException("projectLocation may never be null!");
        }
        if (logAdapter == null) {
            throw new IllegalArgumentException("logAdapter may never be null!");
        }
        if (provider == null) {
            throw new IllegalArgumentException("provider may never be null!");
        }
        if (tempFolder==null) {
            throw new IllegalArgumentException("tempFolder may never be null!");
        }
        this.logAdapter = logAdapter;
        this.provider = provider;
        this.tempFolder=tempFolder;
        this.projectLocation = projectLocation;
        this.projectName=projectName;

        init();
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public File getProjectLocation() {
        return projectLocation;
    }
    
    public Path getTempFolder() {
        return tempFolder;
    }

    public Path getOutputFolder() {
        return outputFolder;
    }

    public void setTocLevels(int tocLevels) {
        this.tocLevels = tocLevels;
    }

    public AsciiDoctorTempFileProvider getTempFileProvider() {
        return tempFileProvider;
    }
    
    public AsciiDoctorRootDirectoryProvider getRootDirectoryProvider() {
        return rootDirectoryProvider;
    }

    public AsciiDoctorImageDirProvider getImageDirProvider() {
        return imageDirProvider;
    }
    
    public AsciiDoctorImageCopyProvider getImageCopyProvider() {
        return imageCopyProvider;
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

    public void setAsciidocFile(File asciidocFile) {
        this.asciidocFile = asciidocFile;
    }

    public void setImageHandlingMode(ImageHandlingMode imageHandlingMode) {
        this.imageHandlingMode = imageHandlingMode;
    }

    public ImageHandlingMode getImageHandlingMode() {
        return imageHandlingMode;
    }

    protected void init() {
        logAdapter.resetTimeDiff();
        
        imageDirProvider = register(new AsciiDoctorImageDirProvider(this));
        logAdapter.logTimeDiff("time to create imageDirProvider provider");
        
        attributesProvider = register(new AsciiDoctorAttributesProvider(this));
        logAdapter.logTimeDiff("time to create attributes provider");
        
        imageCopyProvider = register(new AsciiDoctorImageCopyProvider(this));
        logAdapter.logTimeDiff("time to create imageCopyProvider provider");
        
        optionsProvider = register(new AsciiDoctorOptionsProvider(this));
        logAdapter.logTimeDiff("time to create options provider");
        
        rootDirectoryProvider = register(new AsciiDoctorRootDirectoryProvider(this));
        logAdapter.logTimeDiff("time to create base dir provider");
        
        diagramProvider = register(new AsciiDoctorDiagramProvider(this));
        logAdapter.logTimeDiff("time to create diagram provider");
        
        tempFileProvider = register(new AsciiDoctorTempFileProvider(this));
        logAdapter.logTimeDiff("time to create targeet file provider");
        
        
    }

    public void setOutputFolder(Path outputFolder) {
        this.outputFolder = outputFolder;
    }

    /**
     * Reset context. After this method is called all cached operations will be
     * recalculated on next rendering time fo editor content
     */
    public void reset() {
        this.outputFolder = null;
        this.asciidocFile = null;

        for (AbstractAsciiDoctorProvider provider : providers) {
            provider.reset();
        }
    }
    
    /**
     * The base directory represents the directory where the currently rendered document
     * resides
     * @return base directory or <code>null</code>
     */
    public File getBaseDirectoryOrNull() {
        if (asciidocFile==null) {
            return null;
        }
        return asciidocFile.getParentFile();
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

    public File getRootDirectory() {
        return rootDirectoryProvider.getRootDirectory();
    }

    public LogAdapter getLogAdapter() {
        return logAdapter;
    }

    public File getAsciiDocFile() {
        return asciidocFile;
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

    public File getFileToRender() {
        return fileToRender;
    }

    /**
     * 
     * @return target pdf file or <code>null</code>
     */
    public File getTargetPDFFileOrNull() {
        if (fileToRender == null) {
            return null;
        }
        String originName = fileToRender.getName(); /* xyz.adoc, xyz.asciidoc, xyz, xyz.txt */
        String fileName = FilenameUtils.getBaseName(originName) + ".pdf";
        File file = new File(fileToRender.getParentFile(), fileName);
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

}
