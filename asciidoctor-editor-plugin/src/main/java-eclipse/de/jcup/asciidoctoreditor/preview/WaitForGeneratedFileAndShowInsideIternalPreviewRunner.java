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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;

public class WaitForGeneratedFileAndShowInsideIternalPreviewRunner implements EnsureFileRunnable {

    private static final FinalPreviewFileResolver finalPreviewFileResolver = new FinalPreviewFileResolver();
    private final AsciiDoctorEditor asciiDoctorEditor;
    private IProgressMonitor monitor;

    public WaitForGeneratedFileAndShowInsideIternalPreviewRunner(AsciiDoctorEditor asciiDoctorEditor, IProgressMonitor monitor) {
        this.asciiDoctorEditor = asciiDoctorEditor;
        this.monitor = monitor;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        boolean aquired = false;
        try {
            BrowserAccess browserAccess = asciiDoctorEditor.getBrowserAccess();
            while (asciiDoctorEditor.isNotCanceled(monitor) && (getHTML5PreviewFile() == null || !getHTML5PreviewFile().exists())) {
                if (System.currentTimeMillis() - start > 20000) {
                    // after 20 seconds there seems to be no chance to get
                    // the generated preview file back
                    browserAccess.safeBrowserSetText("<html><body><h3>Preview file generation timed out, so preview not available at:\n<pre>" + getHTML5PreviewFile() + "</pre></h3></body></html>");
                    return;
                }
                Thread.sleep(300);
            }
            aquired = asciiDoctorEditor.getOutputBuildSemaphore().tryAcquire(5, TimeUnit.SECONDS);

            safeAsyncExec(() -> {

                try {
                    File interhalHTML5previewFile = getHTML5PreviewFile();
                    File finalPreviewFile = finalPreviewFileResolver.resolvePreviewFileFromGeneratedHTMLFile(interhalHTML5previewFile, asciiDoctorEditor.getType());
                    
                    URL url = finalPreviewFile.toURI().toURL();
                    String foundURL = browserAccess.getUrl();
                    try {
                        URL formerURL = new URL(browserAccess.getUrl());
                        foundURL = formerURL.toExternalForm();
                    } catch (MalformedURLException e) {
                        /* ignore - about pages etc. */
                    }
                    String externalForm = url.toExternalForm();
                    if (!externalForm.equals(foundURL)) {
                        browserAccess.setUrl(externalForm);
                    } else {
                        browserAccess.refresh();
                    }

                } catch (MalformedURLException e) {
                    AsciiDoctorEditorUtil.logError("Was not able to use malformed URL", e);
                    browserAccess.safeBrowserSetText("<html><body><h3>URL malformed</h3></body></html>");
                }
            });

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (aquired == true) {
                asciiDoctorEditor.getOutputBuildSemaphore().release();
            }
        }

    }

    private File getHTML5PreviewFile() {
        return asciiDoctorEditor.getTemporaryInternalPreviewFile();
    }
}