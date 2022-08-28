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

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;
import de.jcup.asciidoctoreditor.util.EclipseUtil;

public class WaitForGeneratedFileAndShowInsideExternalPreviewPreviewRunner implements EnsureFileRunnable {

    private static final FinalPreviewFileResolver finalPreviewFileResolver = new FinalPreviewFileResolver();
    private final AsciiDoctorEditor asciiDoctorEditor;
    private IProgressMonitor monitor;

    public WaitForGeneratedFileAndShowInsideExternalPreviewPreviewRunner(AsciiDoctorEditor asciiDoctorEditor, IProgressMonitor monitor) {
        this.asciiDoctorEditor = asciiDoctorEditor;
        this.monitor = monitor;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        try {
            File temporaryExternalPreviewFile = asciiDoctorEditor.getTemporaryExternalPreviewFile();
            
            
            while (asciiDoctorEditor.isNotCanceled(monitor) && (temporaryExternalPreviewFile == null || !temporaryExternalPreviewFile.exists())) {
                if (System.currentTimeMillis() - start > 20000) {
                    // after 20 seconds there seems to be no chance to get
                    // the generated preview file back
                    EclipseUtil.safeAsyncExec(() -> MessageDialog.openWarning(EclipseUtil.getActiveWorkbenchShell(), "Asciidoctor Editor",
                            "Generated HTML output not found - maybe it's still in generation.\n\nPlease wait and try again."));
                    return;
                }
                Thread.sleep(300);
            }
            File finalPreviewFile = finalPreviewFileResolver.resolvePreviewFileFromGeneratedHTMLFile(temporaryExternalPreviewFile, asciiDoctorEditor.getType());
            AsciiDoctorEditorUtil.openFileInExternalBrowser(finalPreviewFile);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

}