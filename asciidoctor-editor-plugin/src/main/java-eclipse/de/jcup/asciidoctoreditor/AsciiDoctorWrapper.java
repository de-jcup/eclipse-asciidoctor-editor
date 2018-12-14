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
import org.eclipse.core.resources.IProject;

import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferenceConstants;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.provider.AsciiDoctorOptionsProvider;
import de.jcup.asciidoctoreditor.provider.AsciiDoctorProviderContext;

public class AsciiDoctorWrapper {

    private LogAdapter logAdapter;
    private AsciiDoctorWrapperHTMLBuilder htmlBuilder;

    private AsciiDoctorProviderContext context;
    private AsciiDoctorEditor editor;

    private static AsciiDoctorWrapperProjectTempFolderMapper mapper = new AsciiDoctorWrapperProjectTempFolderMapper();

    public AsciiDoctorWrapper(AsciiDoctorEditor editor, LogAdapter logAdapter) {
        if (logAdapter == null) {
            throw new IllegalArgumentException("log adapter may not be null!");
        }
        this.logAdapter = logAdapter;
        this.editor=editor;
        this.context = new AsciiDoctorProviderContext(AsciiDoctorEclipseLogAdapter.INSTANCE);
        htmlBuilder = new AsciiDoctorWrapperHTMLBuilder(context);

    }

    public AsciiDoctorProviderContext getContext() {
        return context;
    }

    public void convertToHTML(File asciiDocFile) throws Exception {
        init(context);
        
        context.setAsciidocFile(asciiDocFile);
        AsciiDoctorEditorPreferences preferences = AsciiDoctorEditorPreferences.getInstance();
        int tocLevels = preferences.getIntegerPreference(AsciiDoctorEditorPreferenceConstants.P_EDITOR_TOC_LEVELS);
        context.setTocLevels(tocLevels);
        try {
            AsciiDoctorOptionsProvider optionsProvider = context.getOptionsProvider();
            Map<String, Object> defaultOptions = optionsProvider.createDefaultOptions();

            Asciidoctor asciiDoctor = context.getAsciiDoctor();
            asciiDoctor.convertFile(asciiDocFile, defaultOptions);

        } catch (Exception e) {
            logAdapter.logError("Cannot convert to html:" + asciiDocFile, e);
            throw e;
        }
    }

    private void init(AsciiDoctorProviderContext context) {
        context.setUseInstalled(editor.getPreferences().isUsingInstalledAsciidoctor());
        context.setOutputFolder(getTempFolder());
    }

    /**
     * Resets cached values: baseDir, imagesDir
     */
    public void resetCaches() {
        context.reset();
    }

    public Path getTempFolder() {
        IProject project = editor.getProject();
        Path tempFolder = mapper.getTempFolder(project);
        return tempFolder;
    }

    public File getTempFileFor(File editorFile, TemporaryFileType type) {
        File parent = getTempFolder().toFile();
        
        String baseName = FilenameUtils.getBaseName(editorFile.getName());
        StringBuilder sb = new StringBuilder();
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
