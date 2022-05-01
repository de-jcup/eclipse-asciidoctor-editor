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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.AsciidoctorEditorOutlineSupport;
import de.jcup.asciidoctoreditor.ContentTransformer;
import de.jcup.asciidoctoreditor.EclipseDevelopmentSettings;
import de.jcup.asciidoctoreditor.EditorType;
import de.jcup.asciidoctoreditor.asciidoc.WrapperConvertData;
import de.jcup.asciidoctoreditor.document.AsciiDoctorPlantUMLFileDocumentProvider;
import de.jcup.asciidoctoreditor.document.AsciiDoctorPlantUMLTextFileDocumentProvider;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.toolbar.AddErrorDebugAction;
import de.jcup.asciidoctoreditor.toolbar.ClearProjectCacheAsciiDocViewAction;
import de.jcup.asciidoctoreditor.toolbar.JumpToTopOfAsciiDocViewAction;
import de.jcup.asciidoctoreditor.toolbar.RebuildAsciiDocViewAction;
import de.jcup.asciidoctoreditor.toolbar.ShowPreviewHorizontalInsideEditorAction;
import de.jcup.asciidoctoreditor.toolbar.ShowPreviewInExternalBrowserAction;
import de.jcup.asciidoctoreditor.toolbar.ShowPreviewVerticalInsideEditorAction;

public class AsciiDoctorPlantUMLEditor extends AsciiDoctorEditor implements PlantUMLDataProvider {

    private static final AsciiDoctorPlantUMLFileDocumentProvider ASCII_DOCTOR_PLANT_UML_FILE_DOCUMENT_PROVIDER = new AsciiDoctorPlantUMLFileDocumentProvider();
    private static final AsciiDoctorPlantUMLTextFileDocumentProvider ASCII_DOCTOR_PLANT_UML_TEXT_FILE_DOCUMENT_PROVIDER = new AsciiDoctorPlantUMLTextFileDocumentProvider();

    @Override
    protected AsciidoctorEditorOutlineSupport createOutlineSupport() {
        return new AsciidoctorPlantUMLEditorOutlineSupport(this);
    }

    public void beforeAsciidocConvert(WrapperConvertData data) {
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

        IToolBarManager otherToolBarManager = new ToolBarManager(coolBarManager.getStyle());
        otherToolBarManager.add(new JumpToTopOfAsciiDocViewAction(this));

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
        return AsciiDoctorEditorPreferences.getInstance().getPlantUMLOutputFormat();
    }

}
