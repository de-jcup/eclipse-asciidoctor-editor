package de.jcup.asciidoctoreditor;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;

import de.jcup.asciidoctoreditor.toolbar.AddErrorDebugAction;
import de.jcup.asciidoctoreditor.toolbar.ChangeLayoutAction;
import de.jcup.asciidoctoreditor.toolbar.JumpToTopOfAsciiDocViewAction;
import de.jcup.asciidoctoreditor.toolbar.OpenInExternalBrowserAction;
import de.jcup.asciidoctoreditor.toolbar.RebuildAsciiDocViewAction;

public class AsciiDoctorDitaaEditor extends AsciiDoctorEditor {

	@Override
	protected ContentTransformer createCustomContentTransformer() {
		return new DitaaContentTransformer();
	}

	@Override
	protected String getTitleImageName(int severity) {
		return "ditaa-asciidoctor-editor.png";
	}

	protected void initToolbar() {

		IToolBarManager viewToolBarManager = new ToolBarManager(coolBarManager.getStyle());
		viewToolBarManager.add(new ChangeLayoutAction(this));
		viewToolBarManager.add(new RebuildAsciiDocViewAction(this));
		viewToolBarManager.add(new JumpToTopOfAsciiDocViewAction(this));

		IToolBarManager otherToolBarManager = new ToolBarManager(coolBarManager.getStyle());
		otherToolBarManager.add(new OpenInExternalBrowserAction(this));

		// Add to the cool bar manager
		coolBarManager.add(new ToolBarContributionItem(viewToolBarManager, "asciiDocDitaaEditor.toolbar.view"));
		coolBarManager.add(new ToolBarContributionItem(otherToolBarManager, "asciiDocDitaaEditor.toolbar.other"));

		if (EclipseDevelopmentSettings.DEBUG_TOOLBAR_ENABLED) {
			IToolBarManager debugToolBar = new ToolBarManager(coolBarManager.getStyle());
			debugToolBar.add(new AddErrorDebugAction(this));
			coolBarManager.add(new ToolBarContributionItem(debugToolBar, "asciiDocEditor.toolbar.debug"));
		}

		/*
		 * bugfix - coolbar manager does not use theme colors correctly so we
		 * try with transparent background color
		 */
		CoolBar coolbarControl = coolBarManager.getControl();
		Composite parent = coolbarControl.getParent();
		coolbarControl.setBackground(parent.getBackground());
		coolBarManager.update(true);

	}
	
	protected IDocumentProvider createDocumentProvider(IEditorInput input) {
		if (input instanceof FileStoreEditorInput) {
			return new TextFileDocumentProvider();
		} else {
			return new FileDocumentProvider();
		}
	}

}
