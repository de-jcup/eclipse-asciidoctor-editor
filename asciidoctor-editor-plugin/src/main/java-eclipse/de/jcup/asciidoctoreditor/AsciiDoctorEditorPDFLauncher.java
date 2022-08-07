/*
 * Copyright 2019 Albert Tregnaghi
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
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import de.jcup.asciidoctoreditor.asciidoc.ConversionData;
import de.jcup.asciidoctoreditor.asciidoc.PDFSupport;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;
import de.jcup.eclipse.commons.ui.EclipseUtil;

/**
 * Starts a progress dialog which is blocking on ui but can be canceld by user.
 * (reason for blocking: while pdf conversion is running (and this can take a
 * while)) working on the document can lead to problems. <br>
 * <br>
 * The conversion itself is done inside a Job - why? Because asciidoc call
 * cannot be canceled, when its running it runs until done or failed... <br>
 * <br>
 * But to give user possibility to cancel and keep on working we provide at
 * least to cancel blocking dialog. When underlying job is still running the
 * user is able to work
 * 
 * @author albert
 *
 */
public class AsciiDoctorEditorPDFLauncher {

    public static AsciiDoctorEditorPDFLauncher INSTANCE = new AsciiDoctorEditorPDFLauncher();

    private AsciiDoctorEditorPDFLauncher() {
    }

    public void createAndShowPDF(AsciiDoctorEditor editor) {

        ConversionData data = new ConversionData();
        data.setTargetType(editor.getType());
        data.setAsciiDocFile(editor.getEditorFileOrNull());
        data.setEditorId(editor.getEditorId());
        data.setUseHiddenFile(true);
        data.setEditorFileOrNull(editor.getEditorFileOrNull());

        ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(EclipseUtil.getActiveWorkbenchShell());
        try {
            progressDialog.run(true, true, new PDFRunnableWithProgress(editor.getPDFSupport(), data));
        } catch (Exception e) {
            AsciiDoctorEclipseLogAdapter.INSTANCE.logError("Was not able to create/show PDF", e);
        }
    }

    private class PDFRunnableWithProgress implements IRunnableWithProgress {

        private ConversionData data;
        private PDFSupport wrapper;

        private PDFRunnableWithProgress(PDFSupport wrapper, ConversionData data) {
            this.data = data;
            this.wrapper = wrapper;
        }

        @Override
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

            monitor.beginTask("Create and show PDF", IProgressMonitor.UNKNOWN);
            try {
                monitor.subTask("Initialize");
                createAndOpen(monitor, wrapper, data);

            } catch (Exception e) {
                AsciiDoctorEclipseLogAdapter.INSTANCE.logError("Was not able to create/show PDF", e);
            }
            monitor.done();

        }

        private void createAndOpen(IProgressMonitor monitor, PDFSupport wrapper, ConversionData data) throws Exception {
            if (monitor.isCanceled()) {
                return;
            }
            monitor.subTask("Converting adoc file to PDF");

            PDFConvertJob job = new PDFConvertJob();
            job.schedule();
            while (!job.done) {
                if (monitor.isCanceled()) {
                    job.cancel();
                    return;
                }
            }
            if (job.failed != null) {
                return;
            }
            if (monitor.isCanceled()) {
                return;
            }
            monitor.subTask("Open in external browser");

            File file = wrapper.getTargetPDFFileOrNull();

            if (file == null || !file.exists()) {
                monitor.setCanceled(true);
                AsciiDoctorEditorUtil.logError("Was not able to open pdf - file does not exist:" + file, null);
                return;
            }
            AsciiDoctorEditorUtil.openFileInExternalBrowser(file);

        }

        private class PDFConvertJob extends Job {
            private boolean done;
            private Exception failed;

            public PDFConvertJob() {
                super("PDF conversion running...");
            }

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    wrapper.convertPDF(data, monitor);
                    done = true;
                    return Status.OK_STATUS;
                } catch (Exception e) {
                    done = true;
                    failed = e;
                    return new Status(Status.ERROR, AsciiDoctorEditorActivator.PLUGIN_ID, "Was not able to create/show PDF", e);
                }
            }

        }
    }

}
