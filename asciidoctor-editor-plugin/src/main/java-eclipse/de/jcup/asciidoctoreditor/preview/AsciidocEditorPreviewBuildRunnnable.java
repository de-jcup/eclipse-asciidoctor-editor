/*
 * Copyright 2021 Albert Tregnaghi
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
import java.net.URLDecoder;
import java.nio.file.Path;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IProgressMonitor;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.AsciidoctorHTMLOutputParser;
import de.jcup.asciidoctoreditor.ContentTransformerData;
import de.jcup.asciidoctoreditor.EclipseDevelopmentSettings;
import de.jcup.asciidoctoreditor.EditorType;
import de.jcup.asciidoctoreditor.TemporaryFileType;
import de.jcup.asciidoctoreditor.UniqueIdProvider;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocFileUtils;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocStringUtils;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDoctorBackendType;
import de.jcup.asciidoctoreditor.asciidoc.ConversionData;
import de.jcup.asciidoctoreditor.asciidoc.InstalledAsciidoctorException;
import de.jcup.asciidoctoreditor.asciidoc.PreviewSupport;
import de.jcup.asciidoctoreditor.asciidoc.Sha256StringEncoder;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.provider.AsciiDoctorAttributesProvider;
import de.jcup.asciidoctoreditor.script.AsciiDoctorErrorBuilder;
import de.jcup.asciidoctoreditor.script.AsciiDoctorMarker;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;
import de.jcup.asciidoctoreditor.util.EclipseUtil;

/**
 * This is just the core part of building the HTML output for internal an
 * external previews. Here we have the temp asciidoc file creation, absolute
 * path conversion etc.
 * 
 * Between the asciidoc wrapper is called with the temporary asciidoc file.
 * 
 * 
 * @author albert
 *
 */
class AsciidocEditorPreviewBuildRunnnable implements ICoreRunnable {

    public static final Sha256StringEncoder STRING_ENCODER = new Sha256StringEncoder();

    BuildDoneListener buildDoneListener;
    AsciiDoctorBackendType backend;
    boolean internalPreview;
    AsciiDoctorEditor editor;
    private Worked worked;

    AsciidocEditorPreviewBuildRunnnable() {
        this.worked = new Worked();
    }

    @Override
    public void run(IProgressMonitor monitor) throws CoreException {
        try {

            /* before */
            before(monitor);

            /* work */
            buildHTMLPreviewFile(monitor);

            /* after */
            after(monitor);

        } finally {
            editor.getOutputBuildSemaphore().release();
        }
    }

    private void buildHTMLPreviewFile(IProgressMonitor monitor) {
        if (isCanceled(monitor)) {
            return;
        }
        PreviewSupport previewSupport = editor.getPreviewSupport();
        File outputFile = null;

        try {
            safeAsyncExec(() -> AsciiDoctorEditorUtil.removeScriptErrors(editor));

            monitor.subTask("RESOLVE");

            File editorFileOrNull = editor.getEditorFileOrNull();

            /* -------------------------- */
            /* --- Transform Asciidoc --- */
            /* --- content if necessary - */
            /* --- (e.g. for rendering -- */
            /* --- PlantUML files) ------ */
            /* ----before HTML generation */
            /* -------------------------- */
            File tempAsciiDocFileToConvertIntoHTML = createTransformedTempFileOrNull(editorFileOrNull);
            if (tempAsciiDocFileToConvertIntoHTML == null) {
                return;
            }

            if (isCanceled(monitor)) {
                return;
            }
            increaseWorked(monitor);

            /* content exists as simple file */
            monitor.subTask("GENERATE");

            /* -------------------------- */
            /* -------------------------- */
            /* ----Call Asciidoctor ----- */
            /* -------------------------- */
            /* -------------------------- */
            convertTempAsciidocFileToHTML(monitor, previewSupport, editorFileOrNull, tempAsciiDocFileToConvertIntoHTML);

            increaseWorked(monitor);

            if (isCanceled(monitor)) {
                return;
            }

            monitor.subTask("READ AND TRANSFORM");

            /* -------------------------- */
            /* ----- Read origin------- */
            /* ----- Asciidoc output----- */
            /* -------------------------- */
            File fileToRender = previewSupport.getFileToRender();
            String originalAsciidocHTML = readFileCreatedByAsciiDoctor(fileToRender, editor.getEditorId());

            String asciidocHTML = originalAsciidocHTML;

            increaseWorked(monitor);

            /* -------------------------- */
            /* -- Read image pathes -- */
            /* ---from Asciidoc output -- */
            /* -------------------------- */
            asciidocHTML = fixImageLocationPathesInsideHTML(previewSupport, asciidocHTML);

            if (isCanceled(monitor)) {
                return;
            }
            increaseWorked(monitor);

            /* -------------------------- */
            /* -- Create final preview -- */
            /* ---HTML file ------------- */
            /* -------------------------- */
            outputFile = enrichPreviewHTMLAndWriteToDisk(monitor, previewSupport, asciidocHTML);

        } catch (Throwable e) {
            /*
             * Normally I would do a catch(Exception e), but we must use catch(Throwable t)
             * here. Reason (at least eclipse neon) we got full eclipse editor tab freeze
             * problem when a jruby class not found error occurs!
             */
            writeFallbackWhenPreviewFileWasNotWritten(e);

        }
        if (EclipseDevelopmentSettings.DEBUG_LOGGING_ENABLED) {
            System.out.println("worked:" + worked);
            System.out.println("outputFile:" + outputFile);
        }

    }

    /**
     * First of all relative pathes will be changed to absolute pathes here. <br>
     * <br>
     * After this it replaces wrong absolute pathes targeting the
     * asciidoctor-diagram outputs with (asciidoctor relies on imagdir attribute,
     * but asciidoctor-diagramm does use imageoutdir and asciidoctor generator for
     * HTML generates wrong pathes targeting imagedir and not imageoutdir...
     * 
     * @param previewSupport
     * @param asciidocHTML
     * @return
     * @throws IOException
     */
    private String fixImageLocationPathesInsideHTML(PreviewSupport previewSupport, String asciidocHTML) throws IOException {
        AsciidoctorHTMLOutputParser parser = new AsciidoctorHTMLOutputParser();
        File tempFolder = previewSupport.getProjectTempFolder().toFile();
        File imageOutDir = new File(tempFolder, AsciiDoctorAttributesProvider.IMAGE_OUTPUT_DIR_NAME);
        Set<String> pathes = parser.findImageSourcePathes(asciidocHTML);
        for (String path : pathes) {
            if (path == null) {
                continue;
            }
            if (path.indexOf("://") != -1) {
                /* we do not replace URIs - e.g. https://example.com/... */
                continue;
            }

            // file path might contains spaces or special characters that are URL encoded
            File file = new File(URLDecoder.decode(path, "UTF-8"));
            if (file.exists()) {
                /* replace with a valid URI format */
                String uri = file.toURI().toString();
                asciidocHTML = asciidocHTML.replace(path, uri);
            } else {
                String p2 = file.getPath();
                String replacePath = null;
                if (p2.startsWith(".")) {
                    // relative path
                    file = new File(previewSupport.getBaseDir(), p2);
                    if (file.exists()) {
                        replacePath = file.getCanonicalPath();
                    }
                }
                if (replacePath == null) {
                    /* lets assume this generated into imageoutdir... */
                    replacePath = new File(imageOutDir, file.getName()).getCanonicalPath();
                }

                asciidocHTML = asciidocHTML.replace(path, replacePath);

            }
        }

        asciidocHTML = asciidocHTML.replace("<!--[if IE]><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><![endif]-->", "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">");

        return asciidocHTML;
    }

    private void increaseWorked(IProgressMonitor monitor) {
        monitor.worked(++worked.amount);
    }

    private void convertTempAsciidocFileToHTML(IProgressMonitor monitor, PreviewSupport previewSupport, File editorFileOrNull, File tempFileToConvertIntoHTML) throws Exception {
        ConversionData conversionData = createWrapperData(editorFileOrNull, tempFileToConvertIntoHTML);
        editor.beforeAsciidocConvert(conversionData);

        /* convert */
        previewSupport.convert(conversionData, backend, monitor);
    }

    private ConversionData createWrapperData(File editorFileOrNull, File fileToConvertIntoHTML) {
        ConversionData data = new ConversionData();
        data.setTargetType(editor.getType());
        data.setAsciiDocFile(fileToConvertIntoHTML);
        data.setEditorId(editor.getEditorId());
        data.setEditorFileOrNull(editorFileOrNull);
        data.setUseHiddenFile(isNeedingAHiddenEditorFile(data.targetType, editorFileOrNull, fileToConvertIntoHTML));
        data.setInternalPreview(internalPreview);
        return data;
    }

    /*
     * transform if necessary - e.g. plantuml files must be converted before to
     * adoc...
     */
    private File createTransformedTempFileOrNull(File editorFileOrNull) throws IOException {
        File fileToConvertIntoHTML = null;

        if (editorFileOrNull == null) {
            String asciiDoc = editor.getDocumentText();
            fileToConvertIntoHTML = createTransformedTempFileFromTextContent(asciiDoc);
        } else {
            fileToConvertIntoHTML = createTransformedTempFileFromEditorFile(editorFileOrNull);
        }
        return fileToConvertIntoHTML;
    }

    private void writeFallbackWhenPreviewFileWasNotWritten(Throwable e) {
        /*
         * This means the ASCIIDOCTOR wrapper was not able to convert - so we have to
         * clean the former output and show up a marker for complete file
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
            AsciiDoctorMarker error = builder.build(errorMessage);

            editor.getBrowserAccess().safeBrowserSetText(htmlSb.toString());
            AsciiDoctorEditorUtil.addAsciiDoctorMarker(editor, -1, error, IMarker.SEVERITY_ERROR);

            if (isLoggingNecessary(e)) {
                AsciiDoctorEditorUtil.logError("AsciiDoctor error occured:" + e.getMessage(), e);
            }
        });
    }

    private File enrichPreviewHTMLAndWriteToDisk(IProgressMonitor monitor, PreviewSupport previewSupport, String asciiDocHtml) {
        String previewHTML;
        if (internalPreview) {
            previewHTML = previewSupport.enrichHTML(asciiDocHtml, 0);
        } else {
            int refreshAutomaticallyInSeconds = AsciiDoctorEditorPreferences.getInstance().getAutoRefreshInSecondsForExternalBrowser();
            previewHTML = previewSupport.enrichHTML(asciiDocHtml, refreshAutomaticallyInSeconds);
        }

        return writePreviewHTMLFile(monitor, previewHTML);
    }

    private File writePreviewHTMLFile(IProgressMonitor monitor, String previewHTML) {
        File outputFile = null;
        try {
            if (internalPreview) {
                monitor.subTask("WRITE INTERNAL PREVIEW");
                outputFile = editor.getTemporaryInternalPreviewFile();
            } else {
                monitor.subTask("WRITE EXTERNAL PREVIEW");
                outputFile = editor.getTemporaryExternalPreviewFile();
            }
            AsciiDocStringUtils.writeTextToUTF8File(previewHTML, outputFile);
            increaseWorked(monitor);

        } catch (IOException e1) {
            AsciiDoctorEditorUtil.logError("Was not able to save temporary files for preview!", e1);
        }
        return outputFile;
    }

    private File createTransformedTempFileFromTextContent(String asciiDoc) throws IOException {
        File fileToConvertIntoHTML;
        String text;
        if (editor.getContentTransformer().isTransforming(asciiDoc)) {

            ContentTransformerData data = new ContentTransformerData();
            data.origin = asciiDoc;
            text = editor.getContentTransformer().transform(data);
        } else {
            text = asciiDoc;
        }
        fileToConvertIntoHTML = createTransformedTempfile("no_origin_file_defined", text);
        return fileToConvertIntoHTML;
    }

    private File createTransformedTempFileFromEditorFile(File editorFile) throws IOException {
        if (editorFile == null || !editorFile.exists()) {
            return null;
        }

        String originText = AsciiDocStringUtils.readUTF8FileToString(editorFile);
        if (originText == null) {
            return null;
        }
        if (!editor.getContentTransformer().isTransforming(originText)) {
            return editorFile;
        }
        return createTransformedTempfile(editorFile.getName(), originText);
    }

    private File createTransformedTempfile(String filename, String text) throws IOException {
        Path tempFolder = editor.getPreviewSupport().getProjectTempFolder();
        File newTempFile = AsciiDocFileUtils.createTempFileForConvertedContent(tempFolder, editor.getEditorId(), filename);

        ContentTransformerData data = new ContentTransformerData();
        data.origin = text;
        data.filename = filename;

        String transformed = editor.getContentTransformer().transform(data);
        try {
            return AsciiDocStringUtils.writeTextToUTF8File(transformed, newTempFile);
        } catch (IOException e) {
            logError("Was not able to write transformed file:" + filename, e);
            return null;
        }
    }

    private String readFileCreatedByAsciiDoctor(File fileToConvertIntoHTML, UniqueIdProvider editorId) {
        File generatedFile = editor.getPreviewSupport().getTempFileFor(fileToConvertIntoHTML, editorId, TemporaryFileType.ORIGIN);
        try {
            return AsciiDocStringUtils.readUTF8FileToString(generatedFile);
        } catch (IOException e) {
            AsciiDoctorEditorUtil.logError("Was not able to build new full html variant", e);
            return "";
        }
    }

    /* -------------------------------------------- */
    /* ----------------Helpers--------------------- */
    /* -------------------------------------------- */
    private String getSafeFileName() {
        if (editor.getTemporaryInternalPreviewFile() == null) {
            return "<unknown>";
        }
        return editor.getTemporaryInternalPreviewFile().getName();
    }

    /**
     * Asciidoctor starts normally from a root document and resolves pathes etc. on
     * the fly by using the base directory. <br>
     * So far so good. but when rendering a sub file resolving base directory for
     * e.g. images, diagrams etc. this does always break the includes, because
     * either images do not longer work or the include.<br>
     * <br>
     * To prevent this we do following trick: We always create a temporary hidden
     * file which will include the corresponding real editor file. But we do this
     * NOT for plant uml or ditaa!
     */
    private boolean isNeedingAHiddenEditorFile(EditorType targetType, File editorFileOrNull, File fileToConvertIntoHTML) {
        if (targetType == EditorType.PLANTUML) {
            return false;
        }
        if (targetType == EditorType.DITAA) {
            return false;
        }
        /*
         * Still same file so not converted, means still same .adoc file for those files
         * we do always create a temporary editor file which does include the origin one
         * - reason see description in javadoc above
         */
        return fileToConvertIntoHTML.equals(editorFileOrNull);
    }

    private boolean isLoggingNecessary(Throwable e) {
        if (e == null || e instanceof InstalledAsciidoctorException) {
            /* InstalledAsciidoctorException is already logged */
            return false;
        }
        return true;
    }

    private void before(IProgressMonitor monitor) {
        monitor.beginTask("Building document " + getSafeFileName(), 7);
    }

    private void after(IProgressMonitor monitor) {
        if (editor.isInternalPreview()) {
            monitor.subTask("show internal");
            editor.ensureInternalBrowserShowsURL(monitor);
        }
        monitor.worked(7);

        handleBuildDone(buildDoneListener);

        monitor.done();
    }

    private void ensureFocused() {
        if (editor.isInternalPreview()) {
            /*
             * do a "refocus" on safe - sometimes necessary on windows. Seems browser grabs
             * sometimes focus...
             */
            EclipseUtil.safeAsyncExec(() -> editor.refocus());
        }
    }

    private void handleBuildDone(BuildDoneListener buildDoneListener) {
        ensureFocused();

        if (buildDoneListener != null) {
            buildDoneListener.buildDone();
        }
    }

    public boolean isCanceled(IProgressMonitor monitor) {
        if (monitor == null) {
            return false; // no chance to cancel...
        }
        return monitor.isCanceled();
    }

}