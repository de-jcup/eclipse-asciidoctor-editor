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
package de.jcup.asciidoctoreditor.preview;

import static de.jcup.asciidoctoreditor.util.EclipseUtil.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;

import de.jcup.asciidoctoreditor.AbstractAsciiDoctorEditorSupport;
import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.ContentTransformerData;
import de.jcup.asciidoctoreditor.EclipseDevelopmentSettings;
import de.jcup.asciidoctoreditor.TemporaryFileType;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocFileUtils;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocStringUtils;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDoctorBackendType;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDoctorWrapper;
import de.jcup.asciidoctoreditor.asciidoc.InstalledAsciidoctorException;
import de.jcup.asciidoctoreditor.asciidoc.WrapperConvertData;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.script.AsciiDoctorMarker;
import de.jcup.asciidoctoreditor.script.AsciiDoctorErrorBuilder;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;
import de.jcup.asciidoctoreditor.util.EclipseUtil;

public class AsciiDoctorEditorBuildSupport extends AbstractAsciiDoctorEditorSupport {
    private Pattern tempFolderPattern;

    public AsciiDoctorEditorBuildSupport(AsciiDoctorEditor editor) {
        super(editor);
    }

    private boolean isFileNotAvailable(File file) {
        if (file == null) {
            return true;
        }
        return !file.exists();
    }

    private boolean isLoggingNecessary(Throwable e) {
        if (e == null || e instanceof InstalledAsciidoctorException) {
            /* InstalledAsciidoctorException is already logged */
            return false;
        }
        return true;
    }

    public void showRebuildingInPreviewAndTriggerFullHTMLRebuildAsJob(BuildAsciiDocMode mode) {
        showRebuildingInPreviewAndTriggerFullRebuildAsJob(mode, AsciiDoctorBackendType.HTML5, false,null);
    }

    public boolean isAutoBuildEnabledForExternalPreview() {
        return getEditor().getPreferences().isAutoBuildEnabledForExternalPreview();
    }

    /**
     * Shows rebuilding info in preview and triggers a full rebuild as a job in eclipse
     * @param mode builder mode
     * @param backend backend type provider
     * @param forceInitialize when <code>false</code> build is only done when not already building
     */
    protected void showRebuildingInPreviewAndTriggerFullRebuildAsJob(BuildAsciiDocMode mode, AsciiDoctorBackendType backend, boolean forceInitialize, BuildDoneListener buildListener) {

        boolean rebuildEnabled = true;
        if (BuildAsciiDocMode.NOT_WHEN_EXTERNAL_PREVIEW_DISABLED == mode && !getEditor().isInternalPreview()) {
            rebuildEnabled = isAutoBuildEnabledForExternalPreview();
        }
        if (!rebuildEnabled) {
            return;
        }

        if (!forceInitialize && getEditor().getOutputBuildSemaphore().availablePermits() == 0) {
            /* already rebuilding -so ignore */
            return;
        }
        boolean initializing = forceInitialize || isFileNotAvailable(getEditor().getTemporaryInternalPreviewFile());

        try {
            getEditor().getOutputBuildSemaphore().acquire();
            if (initializing) {
                File previewInitializingFile = new File(getEditor().getWrapper().getAddonsFolder(), "html/initialize/preview_initializing.html");
                boolean previewInitializingFileFound = false;
                try {
                    if (previewInitializingFile.exists()) {
                        previewInitializingFileFound = true;
                    }
                    String previewFileURL = previewInitializingFile.toURI().toURL().toExternalForm();
                    getEditor().getBrowserAccess().setUrl(previewFileURL);
                } catch (MalformedURLException e) {
                    logError("Preview initializer html file not valid url", e);
                }
                if (!previewInitializingFileFound) {
                    getEditor().getBrowserAccess().safeBrowserSetText("<html><body><h3>Initializing document</h3></body></html>");
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        String jobInfo = null;
        if (initializing) {
            jobInfo = "Asciidoctor editor preview initializing ";
        } else {
            jobInfo = "Asciidoctor editor full rebuild";
        }
        Job job = Job.create(jobInfo, new ICoreRunnable() {

            @Override
            public void run(IProgressMonitor monitor) throws CoreException {
                try {
                    monitor.beginTask("Building document " + getSafeFileName(), 7);

                    fullBuildTemporaryHTMLFilesAndShowAfter(monitor,backend);

                    if (getEditor().isInternalPreview()) {
                        monitor.subTask("show internal");
                        getEditor().ensureInternalBrowserShowsURL(monitor);
                    }
                    monitor.worked(7);

                    if (getEditor().isInternalPreview()) {
                    	/*
                    	 * do a "refocus" on safe - sometimes necessary on windows.
                    	 * Seems browser grabs sometimes focus...
                    	 */
                    	EclipseUtil.safeAsyncExec(() -> getEditor().refocus());
                    }
                    if (buildListener!=null) {
                        buildListener.buildDone();
                    }

                    monitor.done();

                } finally {
                    getEditor().getOutputBuildSemaphore().release();
                }
            }

            protected String getSafeFileName() {
                if (getEditor().getTemporaryInternalPreviewFile() == null) {
                    return "<unknown>";
                }
                return getEditor().getTemporaryInternalPreviewFile().getName();
            }
        });
        job.schedule();
    }

    private void fullBuildTemporaryHTMLFilesAndShowAfter(IProgressMonitor monitor, AsciiDoctorBackendType backend) {
        String htmlInternalPreview = null;
        String htmlExternalBrowser = null;
        if (getEditor().isCanceled(monitor)) {
            return;
        }
        AsciiDoctorWrapper wrapper = getEditor().getWrapper();
        int worked = 0;
        try {
            safeAsyncExec(() -> AsciiDoctorEditorUtil.removeScriptErrors(getEditor()));

            File editorFileOrNull = getEditor().getEditorFileOrNull();

            monitor.subTask("RESOLVE");
            String asciiDocHtml = null;
            File fileToConvertIntoHTML = null;
            if (editorFileOrNull == null) {
                String asciiDoc = getEditor().getDocumentText();
                fileToConvertIntoHTML = resolveFileToConvertToHTMLAndConvertBeforeWhenNecessary(asciiDoc);
            } else {
                fileToConvertIntoHTML = resolveFileToConvertToHTMLAndConvertBeforeWhenNecessary(editorFileOrNull);
            }
            if (fileToConvertIntoHTML == null) {
                return;
            }
            
            // editorId, asciidocFile, tempFolder)
            if (getEditor().isCanceled(monitor)) {
                return;
            }
            monitor.worked(++worked);

            /* content exists as simple file */
            monitor.subTask("GENERATE");

            long editorId = getEditorId();
            WrapperConvertData data = new WrapperConvertData();
            data.targetType=getEditor().getType();
            data.asciiDocFile=fileToConvertIntoHTML;
            data.editorId=editorId;
            data.useHiddenFile=isNeedingAHiddenEditorFile(editorFileOrNull, fileToConvertIntoHTML);
            data.editorFileOrNull=editorFileOrNull;
			wrapper.convert(data, backend);

            monitor.worked(++worked);

            if (getEditor().isCanceled(monitor)) {
                return;
            }
            monitor.subTask("READ AND TRANSFORM");
            asciiDocHtml = readFileCreatedByAsciiDoctor(wrapper.getContext().getFileToRender(),editorId);
            monitor.worked(++worked);
            /*
             * Calling Asciidoctor generates output with absolute pathes - we
             * keep the originally generated asciidoc file as is
             * (fileToConvertIntoHTML) but the preview files will be changed.
             */
            asciiDocHtml = transformAbsolutePathesToRelatives(asciiDocHtml);
            monitor.worked(++worked);

            int refreshAutomaticallyInSeconds = AsciiDoctorEditorPreferences.getInstance().getAutoRefreshInSecondsForExternalBrowser();
            htmlInternalPreview = wrapper.buildHTMLWithCSS(asciiDocHtml, 0);
            htmlExternalBrowser = wrapper.buildHTMLWithCSS(asciiDocHtml, refreshAutomaticallyInSeconds);
            if (getEditor().isCanceled(monitor)) {
                return;
            }
            try {
                monitor.subTask("WRITE INTERNAL PREVIEW");
                AsciiDocStringUtils.writeTextToUTF8File(htmlInternalPreview, getEditor().getTemporaryInternalPreviewFile());
                monitor.worked(++worked);

                monitor.subTask("WRITE EXTERNAL PREVIEW");
                AsciiDocStringUtils.writeTextToUTF8File(htmlExternalBrowser, getEditor().getTemporaryExternalPreviewFile());
                monitor.worked(++worked);

            } catch (IOException e1) {
                AsciiDoctorEditorUtil.logError("Was not able to save temporary files for preview!", e1);
            }

        } catch (Throwable e) {
            /*
             * Normally I would do a catch(Exception e), but we must use
             * catch(Throwable t) here. Reason (at least eclipse neon) we got
             * full eclipse editor tab freeze problem when a jruby class not
             * found error occurs!
             */
            /*
             * This means the ASCIIDOCTOR wrapper was not able to convert - so
             * we have to clean the former output and show up a marker for
             * complete file
             */
            StringBuilder htmlSb = new StringBuilder();
            htmlSb.append("<h4");
            if (getEditor().isAsciiDoctorError(e)) {
                htmlSb.append("Asciidoctor error");
            } else {
                htmlSb.append("Unknown error");
            }
            htmlSb.append("</h4");

            safeAsyncExec(() -> {

                String errorMessage = getEditor().fetchAsciidoctorErrorMessage(e);

                AsciiDoctorErrorBuilder builder = new AsciiDoctorErrorBuilder();
                AsciiDoctorMarker error = builder.build(errorMessage);
                getEditor().getBrowserAccess().safeBrowserSetText(htmlSb.toString());
                AsciiDoctorEditorUtil.addAsciiDoctorMarker(getEditor(), -1, error, IMarker.SEVERITY_ERROR);

                if (isLoggingNecessary(e)) {
                    AsciiDoctorEditorUtil.logError("AsciiDoctor error occured:" + e.getMessage(), e);
                }
            });

        }
        if (EclipseDevelopmentSettings.DEBUG_LOGGING_ENABLED) {
            System.out.println("worked:" + worked);
        }

    }

    protected long getEditorId() {
        return getEditor().getEditorId();
    }

    /**
     * Asciidoctor starts normally from a root document and resolves pathes etc.
     * on the fly by using the base directory. So far so good. but when
     * resolving base directory for e.g. images, diagrams etc. and setting it
     * but rendering a sub file this does always break the includes, because either images do not longer work or the include.<br><br>
     * To prevent this we do following trick. We always create a temporary hidden file which will include the corresponding real editor file
     * This temporary file is always settled at base folder
     */
    protected boolean isNeedingAHiddenEditorFile(File editorFileOrNull, File fileToConvertIntoHTML) {
        /*
         * Still same file so not converted, means still same .adoc file for
         * those files we do always create a temporary editor file which does
         * include the origin one - reason see description in javadoc above
         */
        return fileToConvertIntoHTML.equals(editorFileOrNull);
    }

    protected Pattern createRemoveAbsolutePathToTempFolderPattern() {
        Path tempFolder = getEditor().getWrapper().getTempFolder();
        String absolutePathToTempFolder = tempFolder.toFile().getAbsolutePath();
        String asciidocOutputAbsolutePath = absolutePathToTempFolder.replace('\\', '/');
        asciidocOutputAbsolutePath += "/"; // relative path is without leading /
        return Pattern.compile(asciidocOutputAbsolutePath);
    }

    protected String readFileCreatedByAsciiDoctor(File fileToConvertIntoHTML, long editorId) {
        File generatedFile = getEditor().getWrapper().getTempFileFor(fileToConvertIntoHTML, editorId, TemporaryFileType.ORIGIN);
        try {
            return AsciiDocStringUtils.readUTF8FileToString(generatedFile);
        } catch (IOException e) {
            AsciiDoctorEditorUtil.logError("Was not able to build new full html variant", e);
            return "";
        }
    }

    protected File resolveFileToConvertToHTMLAndConvertBeforeWhenNecessary(String asciiDoc) throws IOException {
        File fileToConvertIntoHTML;
        String text;
        if (getEditor().getContentTransformer().isTransforming(asciiDoc)) {
        	
            ContentTransformerData  data = new ContentTransformerData();
            data.origin=asciiDoc;
			text = getEditor().getContentTransformer().transform(data);
        } else {
            text = asciiDoc;
        }
        fileToConvertIntoHTML = resolveFileToConvertToHTML("no_origin_file_defined", text);
        return fileToConvertIntoHTML;
    }

    /**
     * Transforms absolute pathes to relative pathes of current temp folder in
     * given html.
     * 
     * @param html
     * @return
     */
    protected String transformAbsolutePathesToRelatives(String html) {
        if (tempFolderPattern == null) {
            /*
             * store the pattern for this editor and reuse it, temp folder will
             * not change
             */
            tempFolderPattern = createRemoveAbsolutePathToTempFolderPattern();
        }
        String newHTML = tempFolderPattern.matcher(html).replaceAll("");
        return newHTML;
    }

    protected File resolveFileToConvertToHTMLAndConvertBeforeWhenNecessary(File editorFile) throws IOException {
        if (editorFile == null || !editorFile.exists()) {
            return null;
        }

        String originText = AsciiDocStringUtils.readUTF8FileToString(editorFile);
        if (originText == null) {
            return null;
        }
        if (!getEditor().getContentTransformer().isTransforming(originText)) {
            return editorFile;
        }
        return resolveFileToConvertToHTML(editorFile.getName(), originText);
    }

    public File resolveFileToConvertToHTML(String filename, String text) throws IOException {
        Path tempFolder = getEditor().getWrapper().getTempFolder();
        File newTempFile = AsciiDocFileUtils.createTempFileForConvertedContent(tempFolder, getEditorId(), filename);

        ContentTransformerData data = new ContentTransformerData();
        data.origin=text;
        data.filename=filename;
        
		String transformed = getEditor().getContentTransformer().transform(data);
        try {
            return AsciiDocStringUtils.writeTextToUTF8File(transformed, newTempFile);
        } catch (IOException e) {
            logError("Was not able to write transformed file:" + filename, e);
            return null;
        }
    }

}
