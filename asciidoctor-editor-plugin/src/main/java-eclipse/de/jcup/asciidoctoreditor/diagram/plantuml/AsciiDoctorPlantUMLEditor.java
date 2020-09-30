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

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.ContentTransformer;
import de.jcup.asciidoctoreditor.CopySupport;
import de.jcup.asciidoctoreditor.EclipseDevelopmentSettings;
import de.jcup.asciidoctoreditor.EditorType;
import de.jcup.asciidoctoreditor.asciidoc.WrapperConvertData;
import de.jcup.asciidoctoreditor.document.AsciiDoctorPlantUMLFileDocumentProvider;
import de.jcup.asciidoctoreditor.document.AsciiDoctorPlantUMLTextFileDocumentProvider;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.script.AsciiDoctorMarker;
import de.jcup.asciidoctoreditor.toolbar.AddErrorDebugAction;
import de.jcup.asciidoctoreditor.toolbar.ChangeLayoutAction;
import de.jcup.asciidoctoreditor.toolbar.JumpToTopOfAsciiDocViewAction;
import de.jcup.asciidoctoreditor.toolbar.OpenInExternalBrowserAction;
import de.jcup.asciidoctoreditor.toolbar.RebuildAsciiDocViewAction;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;

public class AsciiDoctorPlantUMLEditor extends AsciiDoctorEditor implements PlantUMLDataProvider {

    private static final AsciiDoctorPlantUMLFileDocumentProvider ASCII_DOCTOR_PLANT_UML_FILE_DOCUMENT_PROVIDER = new AsciiDoctorPlantUMLFileDocumentProvider();
    private static final AsciiDoctorPlantUMLTextFileDocumentProvider ASCII_DOCTOR_PLANT_UML_TEXT_FILE_DOCUMENT_PROVIDER = new AsciiDoctorPlantUMLTextFileDocumentProvider();

    private PlantUMLLocalIncludeHierarchySearch search = new PlantUMLLocalIncludeHierarchySearch();

    public void beforeAsciidocConvert(WrapperConvertData data) {
        /* we check if there is a need to copy some local includes to temp folder */
        String text = getDocumentText();
        File file = data.editorFileOrNull;
        if (file==null) {
            return;
        }
        File parentFile = file.getParentFile();
        search.setBaseFolder(parentFile);

        List<File> files = null;
        try {
            files = search.searchLocalIncludes(text);
            
        } catch (IOException e) {
            warn("Include search problem: " + e.getMessage());
            return;
        }
        CopySupport copySupport = new CopySupport(parentFile,getTemporaryExternalPreviewFile().getParentFile());
        try {
            copySupport.copyFilesToNewBase(files);
        } catch (IOException e) {
            warn("Failed to copy local includes to temp folder: " + e.getMessage());
        }
        
        
        
    }

    private void warn(String message) {
        AsciiDoctorMarker error = new AsciiDoctorMarker(-1, -1, message);
        int severity = IMarker.SEVERITY_WARNING;
        AsciiDoctorEditorUtil.addAsciiDoctorMarker(this, 1, error, severity);
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

        IToolBarManager viewToolBarManager = new ToolBarManager(coolBarManager.getStyle());
        viewToolBarManager.add(new ChangeLayoutAction(this));
        viewToolBarManager.add(new RebuildAsciiDocViewAction(this));
        viewToolBarManager.add(new JumpToTopOfAsciiDocViewAction(this));

        IToolBarManager otherToolBarManager = new ToolBarManager(coolBarManager.getStyle());
        otherToolBarManager.add(new OpenInExternalBrowserAction(this));

        // Add to the cool bar manager
        coolBarManager.add(new ToolBarContributionItem(viewToolBarManager, "asciiDocPlantUMLEditor.toolbar.view"));
        coolBarManager.add(new ToolBarContributionItem(otherToolBarManager, "asciiDocPlantUMLEditor.toolbar.other"));

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
