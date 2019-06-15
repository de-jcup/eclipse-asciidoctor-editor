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
import de.jcup.asciidoctoreditor.ContentTransformer;
import de.jcup.asciidoctoreditor.EclipseDevelopmentSettings;
import de.jcup.asciidoctoreditor.EditorType;
import de.jcup.asciidoctoreditor.diagram.plantuml.PlantUMLContentTransformer;
import de.jcup.asciidoctoreditor.document.AsciiDoctorPlantUMLFileDocumentProvider;
import de.jcup.asciidoctoreditor.document.AsciiDoctorPlantUMLTextFileDocumentProvider;
import de.jcup.asciidoctoreditor.toolbar.AddErrorDebugAction;
import de.jcup.asciidoctoreditor.toolbar.ChangeLayoutAction;
import de.jcup.asciidoctoreditor.toolbar.JumpToTopOfAsciiDocViewAction;
import de.jcup.asciidoctoreditor.toolbar.OpenInExternalBrowserAction;
import de.jcup.asciidoctoreditor.toolbar.RebuildAsciiDocViewAction;

public class AsciiDoctorPlantUMLEditor extends AsciiDoctorEditor {

	@Override
	protected ContentTransformer createCustomContentTransformer() {
		return new PlantUMLContentTransformer();
	}

	@Override
	protected String getTitleImageName(int severity) {
		return "plantuml-asciidoctor-editor.png";
	}

	public EditorType getType() {
		return EditorType.PLANTUML;
	}

	protected void initToolbar() {

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

	protected IDocumentProvider createDocumentProvider(IEditorInput input) {
		if (input instanceof FileStoreEditorInput) {
			return new AsciiDoctorPlantUMLTextFileDocumentProvider();
		} else {
			return new AsciiDoctorPlantUMLFileDocumentProvider();
		}
	}

}
