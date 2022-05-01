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
package de.jcup.asciidoctoreditor.diagram.ditaa;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.ContentTransformer;
import de.jcup.asciidoctoreditor.EclipseDevelopmentSettings;
import de.jcup.asciidoctoreditor.EditorType;
import de.jcup.asciidoctoreditor.toolbar.AddErrorDebugAction;
import de.jcup.asciidoctoreditor.toolbar.ClearProjectCacheAsciiDocViewAction;
import de.jcup.asciidoctoreditor.toolbar.JumpToTopOfAsciiDocViewAction;
import de.jcup.asciidoctoreditor.toolbar.RebuildAsciiDocViewAction;
import de.jcup.asciidoctoreditor.toolbar.ShowPreviewHorizontalInsideEditorAction;
import de.jcup.asciidoctoreditor.toolbar.ShowPreviewInExternalBrowserAction;
import de.jcup.asciidoctoreditor.toolbar.ShowPreviewVerticalInsideEditorAction;

public class AsciiDoctorDitaaEditor extends AsciiDoctorEditor {

    private static final FileDocumentProvider DITAA_FILE_DOCUMENT_PROVIDER = new FileDocumentProvider();
    private static final TextFileDocumentProvider DITAA_TEXT_FILE_DOCUMENT_PROVIDER = new TextFileDocumentProvider();

    @Override
    protected ContentTransformer createCustomContentTransformer() {
        return new DitaaContentTransformer();
    }

    @Override
    protected String getTitleImageName(int severity) {
        return "ditaa-asciidoctor-editor.png";
    }

    public EditorType getType() {
        return EditorType.DITAA;
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
        coolBarManager.add(new ToolBarContributionItem(previewToolBarManager, "asciiDocDitaaEditor.toolbar.preview"));
        coolBarManager.add(new ToolBarContributionItem(otherToolBarManager, "asciiDocDitaaEditor.toolbar.other"));
        coolBarManager.add(new ToolBarContributionItem(buildToolBarManager, "asciiDocDitaaEditor.toolbar.build"));

        if (EclipseDevelopmentSettings.DEBUG_TOOLBAR_ENABLED) {
            IToolBarManager debugToolBar = new ToolBarManager(coolBarManager.getStyle());
            debugToolBar.add(new AddErrorDebugAction(this));
            coolBarManager.add(new ToolBarContributionItem(debugToolBar, "asciiDocEditor.toolbar.debug"));
        }

        coolBarManager.update(true);

    }

    protected IDocumentProvider resolveDocumentProvider(IEditorInput input) {
        if (input instanceof FileStoreEditorInput) {
            return DITAA_TEXT_FILE_DOCUMENT_PROVIDER;
        } else {
            return DITAA_FILE_DOCUMENT_PROVIDER;
        }
    }

}
