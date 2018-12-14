package de.jcup.asciidoctoreditor;

import static de.jcup.asciidoctoreditor.EclipseUtil.*;

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

import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.script.AsciiDoctorError;
import de.jcup.asciidoctoreditor.script.AsciiDoctorErrorBuilder;

public class AsciiDoctorEditorBuildSupport {
    private Pattern tempFolderPattern;
    private AsciiDoctorEditor editor;

    public AsciiDoctorEditorBuildSupport(AsciiDoctorEditor editor) {
        this.editor = editor;
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

    protected void showRebuildingInPreviewAndTriggerFullHTMLRebuildAsJob(BuildAsciiDocMode mode) {
        showRebuildingInPreviewAndTriggerFullHTMLRebuildAsJob(mode, false);
    }

    protected boolean isAutoBuildEnabledForExternalPreview() {
        return editor.getPreferences().isAutoBuildEnabledForExternalPreview();
    }

    protected void showRebuildingInPreviewAndTriggerFullHTMLRebuildAsJob(BuildAsciiDocMode mode, boolean forceInitialize) {

        boolean rebuildEnabled = true;
        if (BuildAsciiDocMode.NOT_WHEN_EXTERNAL_PREVIEW_DISABLED == mode && !editor.isInternalPreview()) {
            rebuildEnabled = isAutoBuildEnabledForExternalPreview();
        }
        if (!rebuildEnabled) {
            return;
        }

        if (!forceInitialize && editor.outputBuildSemaphore.availablePermits() == 0) {
            /* already rebuilding -so ignore */
            return;
        }
        boolean initializing = forceInitialize || isFileNotAvailable(editor.temporaryInternalPreviewFile);

        try {
            editor.outputBuildSemaphore.acquire();
            if (initializing) {
                File previewInitializingFile = new File(editor.getWrapper().getAddonsFolder(), "html/initialize/preview_initializing.html");
                boolean previewInitializingFileFound = false;
                try {
                    if (previewInitializingFile.exists()) {
                        previewInitializingFileFound = true;
                    }
                    String previewFileURL = previewInitializingFile.toURI().toURL().toExternalForm();
                    editor.browserAccess.setUrl(previewFileURL);
                } catch (MalformedURLException e) {
                    logError("Preview initializer html file not valid url", e);
                }
                if (!previewInitializingFileFound) {
                    editor.browserAccess.safeBrowserSetText("<html><body><h3>Initializing document</h3></body></html>");
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

                    fullBuildTemporaryHTMLFilesAndShowAfter(monitor);

                    if (editor.isInternalPreview()){
                        monitor.subTask("show internal");
                        editor.ensureInternalBrowserShowsURL(monitor);
                    }
                    monitor.worked(7);

                    /*
                     * do a "refocus" on safe - sometimes necessary on windows.
                     * Seems browser grabs sometimes focus...
                     */
                    EclipseUtil.safeAsyncExec(() -> editor.refocus());

                    monitor.done();

                } finally {
                    editor.outputBuildSemaphore.release();
                }
            }

            protected String getSafeFileName() {
                if (editor.temporaryInternalPreviewFile == null) {
                    return "<unknown>";
                }
                return editor.temporaryInternalPreviewFile.getName();
            }
        });
        job.schedule();
    }

    private void fullBuildTemporaryHTMLFilesAndShowAfter(IProgressMonitor monitor) {
        String htmlInternalPreview = null;
        String htmlExternalBrowser = null;
        if (editor.isCanceled(monitor)) {
            return;
        }
        int worked = 0;
        try {
            safeAsyncExec(() -> AsciiDoctorEditorUtil.removeScriptErrors(editor));

            File editorFileOrNull = editor.getEditorFileOrNull();

            monitor.subTask("RESOLVE");
            String asciiDocHtml = null;
            File fileToConvertIntoHTML = null;
            if (editorFileOrNull == null) {
                String asciiDoc = editor.getDocumentText();
                fileToConvertIntoHTML = resolveFileToConvertToHTMLAndConvertBeforeWhenNecessary(asciiDoc);
            } else {
                fileToConvertIntoHTML = resolveFileToConvertToHTMLAndConvertBeforeWhenNecessary(editorFileOrNull);
            }
            if (fileToConvertIntoHTML == null) {
                return;
            }
            if (editor.isCanceled(monitor)) {
                return;
            }
            monitor.worked(++worked);

            /* content exists as simple file */
            monitor.subTask("GENERATE");
            editor.getWrapper().convertToHTML(fileToConvertIntoHTML);

            monitor.worked(++worked);

            if (editor.isCanceled(monitor)) {
                return;
            }
            monitor.subTask("READ AND TRANSFORM");
            asciiDocHtml = readFileCreatedByAsciiDoctor(fileToConvertIntoHTML);
            monitor.worked(++worked);
            /*
             * Calling Asciidoctor generates output with absolute pathes - we
             * keep the originally generated asciidoc file as is
             * (fileToConvertIntoHTML) but the preview files will be changed.
             */
            asciiDocHtml = transformAbsolutePathesToRelatives(asciiDocHtml);
            monitor.worked(++worked);

            int refreshAutomaticallyInSeconds = AsciiDoctorEditorPreferences.getInstance().getAutoRefreshInSecondsForExternalBrowser();
            AsciiDoctorWrapper wrapper = editor.getWrapper();
            htmlInternalPreview = wrapper.buildHTMLWithCSS(asciiDocHtml, 0);
            htmlExternalBrowser = wrapper.buildHTMLWithCSS(asciiDocHtml, refreshAutomaticallyInSeconds);
            if (editor.isCanceled(monitor)) {
                return;
            }
            try {
                monitor.subTask("WRITE INTERNAL PREVIEW");
                AsciiDocStringUtils.writeTextToUTF8File(htmlInternalPreview, editor.temporaryInternalPreviewFile);
                monitor.worked(++worked);

                monitor.subTask("WRITE EXTERNAL PREVIEW");
                AsciiDocStringUtils.writeTextToUTF8File(htmlExternalBrowser, editor.temporaryExternalPreviewFile);
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
            if (editor.isAsciiDoctorError(e)) {
                htmlSb.append("Asciidoctor error");
            } else {
                htmlSb.append("Unknown error");
            }
            htmlSb.append("</h4");

            safeAsyncExec(() -> {

                String errorMessage = editor.fetchAsciidoctorErrorMessage(e);

                AsciiDoctorErrorBuilder builder = new AsciiDoctorErrorBuilder();
                AsciiDoctorError error = builder.build(errorMessage);
                editor.browserAccess.safeBrowserSetText(htmlSb.toString());
                AsciiDoctorEditorUtil.addScriptError(editor, -1, error, IMarker.SEVERITY_ERROR);

                if (isLoggingNecessary(e)) {
                    AsciiDoctorEditorUtil.logError("AsciiDoctor error occured:" + e.getMessage(), e);
                }
            });

        }
        if (EclipseDevelopmentSettings.DEBUG_LOGGING_ENABLED){
            System.out.println("worked:"+worked);
        }

    }

    protected Pattern createRemoveAbsolutePathToTempFolderPattern() {
        Path tempFolder = editor.getWrapper().getTempFolder();
        String absolutePathToTempFolder = tempFolder.toFile().getAbsolutePath();
        String asciidocOutputAbsolutePath = absolutePathToTempFolder.replace('\\', '/');
        asciidocOutputAbsolutePath += "/"; // relative path is without leading /
        return Pattern.compile(asciidocOutputAbsolutePath);
    }

    protected String readFileCreatedByAsciiDoctor(File fileToConvertIntoHTML) {
        File generatedFile = editor.getWrapper().getTempFileFor(fileToConvertIntoHTML, TemporaryFileType.ORIGIN);
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
        if (editor.contentTransformer.isTransforming(asciiDoc)) {
            text = editor.contentTransformer.transform(asciiDoc);
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
        if (!editor.contentTransformer.isTransforming(originText)) {
            return editorFile;
        }
        return resolveFileToConvertToHTML(editorFile.getName(), originText);
    }

    public File resolveFileToConvertToHTML(String filename, String text) throws IOException {
        File newTempFile = AsciiDocFileUtils.createTempFileForConvertedContent(editor.editorTempIdentifier, filename);

        String transformed = editor.contentTransformer.transform(text);
        try {
            return AsciiDocStringUtils.writeTextToUTF8File(transformed, newTempFile);
        } catch (IOException e) {
            logError("Was not able to write transformed file:" + filename, e);
            return null;
        }
    }
}
