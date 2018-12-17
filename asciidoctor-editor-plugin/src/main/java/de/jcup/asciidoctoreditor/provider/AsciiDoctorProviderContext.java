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

import org.asciidoctor.Asciidoctor;

import de.jcup.asciidoctoreditor.LogAdapter;

public class AsciiDoctorProviderContext {

    private LogAdapter logAdapter;
    private File asciidocFile;
    private File baseDir;
    private Path outputFolder;
    private boolean tocVisible;
    private AsciiDoctorInstanceProvider provider;

    private AsciiDoctorBaseDirectoryProvider baseDirProvider;
    private AsciiDoctorImageProvider imageProvider;
    private AsciiDoctorDiagramProvider diagramProvider;
    private AsciiDoctorAttributesProvider attributesProvider;
    private AsciiDoctorOptionsProvider optionsProvider;
    File targetImagesDir;
    int tocLevels;
    private boolean useInstalled;

    public AsciiDoctorProviderContext(AsciiDoctorInstanceProvider provider, LogAdapter logAdapter) {
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

    public void setAsciidocFile(File asciidocFile) {
        this.asciidocFile = asciidocFile;
        this.baseDir = baseDirProvider.findBaseDir();
    }

    protected void init() {
        logAdapter.resetTimeDiff();
        attributesProvider = new AsciiDoctorAttributesProvider(this);
        logAdapter.logTimeDiff("time to create attributes provider");
        imageProvider = new AsciiDoctorImageProvider(this);
        logAdapter.logTimeDiff("time to create images provider");
        optionsProvider = new AsciiDoctorOptionsProvider(this);
        logAdapter.logTimeDiff("time to create options provider");
        baseDirProvider = new AsciiDoctorBaseDirectoryProvider(this);
        logAdapter.logTimeDiff("time to create base dir provider");
        diagramProvider = new AsciiDoctorDiagramProvider(this);
        logAdapter.logTimeDiff("time to create diagram provider");
    }

    public void setOutputFolder(Path outputFolder) {
        this.outputFolder = outputFolder;
    }

    public void reset() {
        this.baseDir = null;
        this.outputFolder = null;
        this.asciidocFile = null;

        this.attributesProvider.reset();
        this.optionsProvider.reset();
        this.imageProvider.reset();
    }

    public Asciidoctor getAsciiDoctor() {
        return getProvider().getAsciiDoctor(useInstalled);
    }

    protected AsciiDoctorInstanceProvider getProvider() {
        return provider;
    }

    public void setTOCVisible(boolean visible) {
        this.tocVisible = visible;
    }

    public boolean isTOCVisible() {
        return tocVisible;
    }

    public File getBaseDir() {
        if (baseDir == null) {
            baseDir = baseDirProvider.findBaseDir();
        }
        return baseDir;
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

}
