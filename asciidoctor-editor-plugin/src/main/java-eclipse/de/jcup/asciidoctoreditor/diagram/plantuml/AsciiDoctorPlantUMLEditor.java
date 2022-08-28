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
package de.jcup.asciidoctoreditor.diagram.plantuml;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.AsciidoctorEditorOutlineSupport;
import de.jcup.asciidoctoreditor.ContentTransformer;
import de.jcup.asciidoctoreditor.EclipseDevelopmentSettings;
import de.jcup.asciidoctoreditor.EditorType;
import de.jcup.asciidoctoreditor.asciidoc.ConversionData;
import de.jcup.asciidoctoreditor.document.AsciiDoctorPlantUMLFileDocumentProvider;
import de.jcup.asciidoctoreditor.document.AsciiDoctorPlantUMLTextFileDocumentProvider;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.preview.BrowserAccess;
import de.jcup.asciidoctoreditor.toolbar.AddErrorDebugAction;
import de.jcup.asciidoctoreditor.toolbar.ClearProjectCacheAsciiDocViewAction;
import de.jcup.asciidoctoreditor.toolbar.JumpToTopOfAsciiDocViewAction;
import de.jcup.asciidoctoreditor.toolbar.NewPlantUMLUseExampleBlockAction;
import de.jcup.asciidoctoreditor.toolbar.RebuildAsciiDocViewAction;
import de.jcup.asciidoctoreditor.toolbar.ShowPreviewHorizontalInsideEditorAction;
import de.jcup.asciidoctoreditor.toolbar.ShowPreviewInExternalBrowserAction;
import de.jcup.asciidoctoreditor.toolbar.ShowPreviewVerticalInsideEditorAction;
import de.jcup.asciidoctoreditor.toolbar.ZoomLevel;
import de.jcup.asciidoctoreditor.toolbar.ZoomLevelContributionItem;
import de.jcup.asciidoctoreditor.util.EclipseUtil;

public class AsciiDoctorPlantUMLEditor extends AsciiDoctorEditor implements PlantUMLDataProvider {

    private static final double MAXIMUM_SCALE_FACTOR = 4.0;
    private static final double MINIMUM_SCALE_FACTOR = 0.1;

    private static final AsciiDoctorPlantUMLFileDocumentProvider ASCII_DOCTOR_PLANT_UML_FILE_DOCUMENT_PROVIDER = new AsciiDoctorPlantUMLFileDocumentProvider();
    private static final AsciiDoctorPlantUMLTextFileDocumentProvider ASCII_DOCTOR_PLANT_UML_TEXT_FILE_DOCUMENT_PROVIDER = new AsciiDoctorPlantUMLTextFileDocumentProvider();
    private double pumlScaleFactor;
    private ZoomLevelContributionItem zoomLevelContributionItem;

    @Override
    protected AsciidoctorEditorOutlineSupport createOutlineSupport() {
        return new AsciidoctorPlantUMLEditorOutlineSupport(this);
    }

    @Override
    protected void createSashFormAndBrowserAccess() {
        super.createSashFormAndBrowserAccess();
    }

    @Override
    protected void initPreview(SashForm sashForm) {
        String defaultZoomLevel = AsciiDoctorEditorPreferences.getInstance().getPlantUMLDefaultZoomLevelAsText();
        Double defaultPercentageOrNull = ZoomLevel.calculatePercentagefromString(defaultZoomLevel);
        
        if (defaultPercentageOrNull==null) {
            pumlScaleFactor = ZoomLevel.LEVEL_100_PERCENT_VALUE;
        }else {
            pumlScaleFactor = defaultPercentageOrNull; 
        }
        super.initPreview(sashForm);

        PlantUMLPreviewMouseWheelAndKeyListener mouseWheelAndKeyListener = new PlantUMLPreviewMouseWheelAndKeyListener();
        BrowserAccess browserAccess = getBrowserAccess();
        
        browserAccess.installMouseWheelListener(mouseWheelAndKeyListener);
    }

    public void beforeAsciidocConvert(ConversionData data) {
        /* nothing special here */
    }

    @Override
    protected ContentTransformer createCustomContentTransformer() {
        PlantUMLContentTransformer transformer = new PlantUMLContentTransformer();
        transformer.setDataProvider(this);
        return transformer;
    }

    @Override
    protected String getTitleImageName(int severity) {
        return "plantuml-asciidoctor-editor.png";
    }

    public EditorType getType() {
        return EditorType.PLANTUML;
    }

    protected void initToolbar() {

        /* necessary for refresh */
        rebuildAction = new RebuildAsciiDocViewAction(this);
        clearProjectCacheAction = new ClearProjectCacheAsciiDocViewAction(this);

        IToolBarManager previewToolBarManager = new ToolBarManager(coolBarManager.getStyle());
        previewToolBarManager.add(new ShowPreviewVerticalInsideEditorAction(this));
        previewToolBarManager.add(new ShowPreviewHorizontalInsideEditorAction(this));
        previewToolBarManager.add(new ShowPreviewInExternalBrowserAction(this));
        zoomLevelContributionItem = new ZoomLevelContributionItem(this);
        previewToolBarManager.add(zoomLevelContributionItem);

        IToolBarManager otherToolBarManager = new ToolBarManager(coolBarManager.getStyle());
        otherToolBarManager.add(new JumpToTopOfAsciiDocViewAction(this));
        otherToolBarManager.add(new NewPlantUMLUseExampleBlockAction(this));

        IToolBarManager buildToolBarManager = new ToolBarManager(coolBarManager.getStyle());
        buildToolBarManager.add(rebuildAction);
        buildToolBarManager.add(clearProjectCacheAction);

        // Add to the cool bar manager
        coolBarManager.add(new ToolBarContributionItem(previewToolBarManager, "asciiDocPlantUMLEditor.toolbar.preview"));
        coolBarManager.add(new ToolBarContributionItem(otherToolBarManager, "asciiDocPlantUMLEditor.toolbar.other"));
        coolBarManager.add(new ToolBarContributionItem(buildToolBarManager, "asciiDocPlantUMLEditor.toolbar.build"));

        if (EclipseDevelopmentSettings.DEBUG_TOOLBAR_ENABLED) {
            IToolBarManager debugToolBar = new ToolBarManager(coolBarManager.getStyle());
            debugToolBar.add(new AddErrorDebugAction(this));
            coolBarManager.add(new ToolBarContributionItem(debugToolBar, "asciiDocEditor.toolbar.debug"));
        }

        coolBarManager.update(true);

    }

    @Override
    protected String getToggleCommentCodePart() {
        return "'";
    }

    protected SourceViewerConfiguration createSourceViewerConfig() {
        return new AsciiDoctorPlantUMLSourceViewerConfiguration(this);
    }

    protected IDocumentProvider resolveDocumentProvider(IEditorInput input) {
        if (input instanceof FileStoreEditorInput) {
            return ASCII_DOCTOR_PLANT_UML_TEXT_FILE_DOCUMENT_PROVIDER;
        } else {
            return ASCII_DOCTOR_PLANT_UML_FILE_DOCUMENT_PROVIDER;
        }
    }

    @Override
    public PlantUMLOutputFormat getOutputFormat() {
        return PlantUMLOutputFormat.SVG;
    }

    @Override
    public void updateScaleFactor(double percentage) {
        updateScaleFactor(percentage, false);
    }

    private void updateScaleFactor(double percentage, boolean alwaysUpdateUI) {
        double newPumlScaleFactor = ensureScaleFactorValid(percentage);
        if (alwaysUpdateUI || newPumlScaleFactor != percentage) {
            updateZoomLevelOnUI();
        }
        if (pumlScaleFactor == newPumlScaleFactor) {
            return;
        }
        pumlScaleFactor = newPumlScaleFactor;

        rebuild();
    }

    @Override
    public double getScaleFactor() {
        return pumlScaleFactor;
    }

    /**
     * Ensures given scale factor is valid. If valid, the factor will be returned.
     * If not, another(but valid) factor will be returned
     * 
     * @param scaleFactor
     * @return valid factor
     */
    private double ensureScaleFactorValid(double scaleFactor) {
        if (scaleFactor <= 0) {
            scaleFactor = ZoomLevel.LEVEL_100_PERCENT_VALUE;
        }
        if (scaleFactor < MINIMUM_SCALE_FACTOR) {
            scaleFactor = MINIMUM_SCALE_FACTOR;
        }
        if (scaleFactor > MAXIMUM_SCALE_FACTOR) {
            scaleFactor = MAXIMUM_SCALE_FACTOR;
        }
        return scaleFactor;
    }

    private void updateZoomLevelOnUI() {
        if (zoomLevelContributionItem == null) {
            return;
        }
        EclipseUtil.safeAsyncExec(() -> zoomLevelContributionItem.updateZoomLevel(pumlScaleFactor));
    }

    private class PlantUMLPreviewMouseWheelAndKeyListener implements MouseWheelListener, Runnable {

        private int lastEventTime;
        private Object monitor = new Object();
        private Thread updateThread;
        private long lastMouseWheelChange;
        
        public void mouseScrolled(MouseEvent e) {
            if ((e.stateMask & SWT.CTRL) == 0) {
                /* not CTRL pressed, so ignore */
                return;
            }
            boolean zoomIn = e.count > 0;
            
            handleZoomAction(e, zoomIn);
        }

        private void handleZoomAction(TypedEvent e, boolean zoomIn) {
            /* with the next time check we avoid multiple events at same time */
            if (e.time == lastEventTime) {
                return;
            }
            lastMouseWheelChange = System.currentTimeMillis();
            lastEventTime = e.time;
            double newPumlScaleFactor = pumlScaleFactor;
            if (zoomIn) {
                newPumlScaleFactor = newPumlScaleFactor += 0.1;
            } else {
                newPumlScaleFactor = newPumlScaleFactor -= 0.1;
            }
            double ensured = ensureScaleFactorValid(newPumlScaleFactor);
            
            if (ensured != newPumlScaleFactor) {
                /* not valid - so just do not change */
                return;
            }
            pumlScaleFactor = newPumlScaleFactor;
            updateZoomLevelOnUI();

            synchronized (monitor) {
                /* if no update thread currently running/existing, create a new one */
                if (updateThread == null || !updateThread.isAlive()) {
                    updateThread = new Thread(this, "puml-scale-update-delay");
                    updateThread.start();
                }
            }
        }

        @Override
        public void run() {
            while (System.currentTimeMillis() - lastMouseWheelChange < 300) {
                /* while we have changes - we wait until no more changes */
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            rebuild();
        }

      
    }
}
