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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import de.jcup.asciidoctoreditor.asciidoc.AsciiDocFileUtils;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDoctorBackendType;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDoctorWrapper;
import de.jcup.asciidoctoreditor.asciidoc.ConversionData;
import de.jcup.asciidoctoreditor.asciidoc.OverviewDataProvider;
import de.jcup.asciidoctoreditor.asp.AspCompatibleProgressMonitorAdapter;
import de.jcup.asciidoctoreditor.diagram.plantuml.PlantUMLContentTransformer;
import de.jcup.asciidoctoreditor.diagram.plantuml.PlantUMLOutputFormat;
import de.jcup.asciidoctoreditor.globalmodel.GlobalAsciidocModel;
import de.jcup.asciidoctoreditor.tools.DocumentPlantUMLMindMapGenerator;
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
public class AsciiDoctorEditorOverviewLauncher {

    public static AsciiDoctorEditorOverviewLauncher INSTANCE = new AsciiDoctorEditorOverviewLauncher();
    private static final PlantUMLContentTransformer transformer = new PlantUMLContentTransformer();
    
    private static AsciiDoctorWrapper wrapper = AsciiDoctorWrapper.getNoProjectAsciiDoctorWrapper(); 

    private AsciiDoctorEditorOverviewLauncher() {
    }

    public void createAndShowOverview(AsciiDoctorEditor editor) {

        ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(EclipseUtil.getActiveWorkbenchShell());
        try {
            File editorFileOrNull = editor.getEditorFileOrNull();
            if (editorFileOrNull==null) {
                return;
            }
            progressDialog.run(true, true, new OverviewRunnableWithProgress(editor.getOverviewer(), editor.getEditorId(), editorFileOrNull));
        } catch (Exception e) {
            AsciiDoctorEclipseLogAdapter.INSTANCE.logError("Was not able to create/show Overview", e);
        }
    }

    private class OverviewRunnableWithProgress implements IRunnableWithProgress {

        private OverviewDataProvider selectedAsciidocFileWrapper;
        private File editorFile;
        private File generatedOverviewFile;
        public OverviewRunnableWithProgress(OverviewDataProvider wrapper, UniqueEditorId id, File editorFile) {
            this.selectedAsciidocFileWrapper = wrapper;
            this.editorFile=editorFile;
            
            generatedOverviewFile = new File(selectedAsciidocFileWrapper.getTempGenFolder(), ".ov_" + id.getUniqueId()+"_"+editorFile.getName());
        }

        @Override
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

            monitor.beginTask("Create and show Overview", IProgressMonitor.UNKNOWN);
            try {
                monitor.subTask("Initialize");
                createAndOpen(monitor);

            } catch (Exception e) {
                AsciiDoctorEclipseLogAdapter.INSTANCE.logError("Was not able to create/show Overview", e);
            }
            monitor.done();

        }

        private void createAndOpen(IProgressMonitor monitor) throws Exception {
            if (monitor.isCanceled()) {
                return;
            }
            monitor.subTask("Creating overview for adoc file");

            CreateOverviewJob job = new CreateOverviewJob();
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

            String originFileName = generatedOverviewFile.getName();
            
            File fileToOpenInBrowser = new File(wrapper.getOutputFolder().toFile(),"img/"+originFileName+".svg");

            if (fileToOpenInBrowser == null || !fileToOpenInBrowser.exists()) {
                monitor.setCanceled(true);
                AsciiDoctorEditorUtil.logError("Was not able to open overview - file does not exist:" + fileToOpenInBrowser, null);
                return;
            }
            AsciiDoctorEditorUtil.openFileInExternalBrowser(fileToOpenInBrowser);

        }

        private class CreateOverviewJob extends Job {
            private boolean done;
            private Exception failed;

            public CreateOverviewJob() {
                super("Overview creation running...");
            }

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    monitor.subTask("Create global model");
                    File baseDir = selectedAsciidocFileWrapper.getBaseDir();
                    /* @formatter:off */
                    GlobalAsciidocModel model = 
                                GlobalAsciidocModel.builder().
                                    from(baseDir).
                                    withImages(true, new File(selectedAsciidocFileWrapper.getCachedSourceImagesPath())).
                                    withDiagrams(true).
                                    logWith(AsciiDoctorEclipseLogAdapter.INSTANCE).
                                    build();
                    /* @formatter:on */

                    monitor.subTask("Generate plantuml mindmap");
                    generateOverviewFile(model);

                    monitor.subTask("Start conversion to HTML");
                    convertToHTML(monitor);
                    
                    done = true;
                    return Status.OK_STATUS;
                } catch (Exception e) {
                    done = true;
                    failed = e;
                    return new Status(Status.ERROR, AsciiDoctorEditorActivator.PLUGIN_ID, "Was not able to create/show Overview", e);
                }
            }

            private void convertToHTML(IProgressMonitor monitor) throws Exception {
                ConversionData data = new ConversionData();
                data.setAsciiDocFile(generatedOverviewFile);
                data.setEditorFileOrNull(generatedOverviewFile);
                data.setTargetType(EditorType.PLANTUML);
                data.setUseHiddenFile(false);
                data.setInternalPreview(false);

                wrapper.convert(data, AsciiDoctorBackendType.HTML5, new AspCompatibleProgressMonitorAdapter(monitor));
            }

            private void generateOverviewFile(GlobalAsciidocModel model) throws IOException {
                DocumentPlantUMLMindMapGenerator generator = new DocumentPlantUMLMindMapGenerator();
                String output = generator.generate(editorFile, model);

                ContentTransformerData transformData = new ContentTransformerData();
                transformData.origin = output;
                transformData.filename = generatedOverviewFile.getName();
                transformer.setDataProvider(()->PlantUMLOutputFormat.SVG);
                
                String asAsciidoc = transformer.transform(transformData);

                AsciiDocFileUtils.writeAsciidocFile(generatedOverviewFile, asAsciidoc);
            }

        }
    }

}
