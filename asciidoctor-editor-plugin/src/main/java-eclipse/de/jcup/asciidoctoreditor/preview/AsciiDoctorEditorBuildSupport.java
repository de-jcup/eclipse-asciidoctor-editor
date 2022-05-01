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

import org.eclipse.core.runtime.jobs.Job;

import de.jcup.asciidoctoreditor.AbstractAsciiDoctorEditorSupport;
import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDoctorBackendType;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;

/**
 * Every editor has got its own editor support object!
 * 
 * @author albert
 *
 */
public class AsciiDoctorEditorBuildSupport extends AbstractAsciiDoctorEditorSupport {

    private AsciidocBuildAndPreviewJobFactory factory;

    public AsciiDoctorEditorBuildSupport(AsciiDoctorEditor editor) {
        super(editor);
        this.factory = new AsciidocBuildAndPreviewJobFactory(getEditor());
    }

    /**
     * Builds/rebuilds HTML by an eclipse background job and shows up rebuilding
     * information inside preview while job has not finished
     * 
     * @param mode
     * @param internalPreview
     */
    public void build(BuildAsciiDocMode mode, boolean internalPreview) {
        buildFullHTMLRebuildAsJobAndShowRebuildingInPreview(mode, AsciiDoctorBackendType.HTML5, false, internalPreview, new RevalidateAfterBuildListener());
    }

    /**
     * Shows rebuilding info in preview and triggers a full rebuild as a job in
     * eclipse
     * 
     * @param mode            builder mode
     * @param backend         backend blockType provider
     * @param forceInitialize when <code>false</code> build is only done when not
     *                        already building
     */
    private void buildFullHTMLRebuildAsJobAndShowRebuildingInPreview(BuildAsciiDocMode mode, AsciiDoctorBackendType backend, boolean forceInitialize, boolean internalPreview,
            BuildDoneListener buildDoneListener) {
        getEditor().validate();

        boolean rebuildEnabled = true;
        boolean internalPreview2 = getEditor().isInternalPreview();
        if (BuildAsciiDocMode.NOT_WHEN_EXTERNAL_PREVIEW_DISABLED == mode && !internalPreview2) {
            rebuildEnabled = AsciiDoctorEditorPreferences.getInstance().isAutoBuildEnabledForExternalPreview();
        }
        if (!rebuildEnabled) {
            /* at least validate + rebuild outline */
            return;
        }
        if (!forceInitialize && getEditor().getOutputBuildSemaphore().availablePermits() == 0) {
            /* already rebuilding -so ignore */
            return;
        }
        boolean initializing = forceInitialize || isFileNotAvailable(getEditor().getTemporaryInternalPreviewFile());

        showInitializingInfoWhenNecessary(initializing);

        triggerBuildAndRefreshJob(backend, internalPreview, buildDoneListener, initializing);
    }

    private void showInitializingInfoWhenNecessary(boolean initializing) {
        try {
            getEditor().getOutputBuildSemaphore().acquire();
            if (initializing) {
                showInitializingInfo(getEditor());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static String previewFileURL = null;
    private static final String INITIALIZE_FALLBACK_HTML = "<html><body><h3>Initializing document</h3></body></html>";

    public static void showInitializingInfo(AsciiDoctorEditor editor) {
        BrowserAccess browserAccess = editor.getBrowserAccess();

        if (previewFileURL == null) {
            /* we only need this one time */
            previewFileURL = "";

            File previewInitializingFile = new File(editor.getWrapper().getAddonsFolder(), "html/initialize/preview_initializing.html");
            if (previewInitializingFile.exists()) {
                try {
                    previewFileURL = previewInitializingFile.toURI().toURL().toExternalForm();
                } catch (MalformedURLException e) {
                    logError("Preview initializer html file not valid url", e);
                }
            }
        }

        if (previewFileURL.equals("")) {
            browserAccess.safeBrowserSetText(INITIALIZE_FALLBACK_HTML);
        } else {
            browserAccess.setUrl(previewFileURL);
        }
    }

    private void triggerBuildAndRefreshJob(AsciiDoctorBackendType backend, boolean internalPreview, BuildDoneListener buildDoneListener, boolean initializing) {
        String jobName = null;
        if (initializing) {
            jobName = "Asciidoctor editor preview initializing ";
        } else {
            jobName = "Asciidoctor editor full rebuild";
        }

        /* create job and trigger it */
        Job job = factory.createBuildAndPreviewJob(backend, internalPreview, buildDoneListener, jobName);
        job.schedule();
    }

    private class RevalidateAfterBuildListener implements BuildDoneListener {

        @Override
        public void buildDone() {
            getEditor().rebuildOutlineAndValidate();
        }

    }

}
