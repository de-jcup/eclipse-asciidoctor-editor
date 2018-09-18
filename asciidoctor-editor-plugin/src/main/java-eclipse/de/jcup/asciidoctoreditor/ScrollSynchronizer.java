package de.jcup.asciidoctoreditor;

import de.jcup.asciidoctoreditor.outline.Item;
import de.jcup.asciidoctoreditor.outline.ItemType;

/**
 * A synchronizer for scrolling between editor and internal preview
 * 
 * @author Albert Tregnaghi
 *
 */
public class ScrollSynchronizer {

	private AsciiDoctorEditor editor;

	public ScrollSynchronizer(AsciiDoctorEditor editor) {
		this.editor = editor;
	}

	public void onEditorCaretMoved(int caretOffset) {
		if (!editor.getPreferences().isLinkEditorWithPreviewEnabled()){
			return;
		}
		if (!editor.isInternalPreview()) {
			return;
		}
		Item item = editor.getItemAt(caretOffset);
		if (item == null) {
			return;
		}

		handleScrollToHeadlineIfPossible(item);
	}

	private void handleScrollToHeadlineIfPossible(Item item) {
		if (!ItemType.HEADLINE.equals(item.getItemType())) {
			return;
		}
		String headlineId = item.getId();
		if (headlineId == null) {
			/* means title */
			editor.browserAccess.navgigateToTopOfView();
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("document.getElementById('");
		sb.append(headlineId);
		sb.append("').scrollIntoView();");

		String javascript = sb.toString();

		editor.browserAccess.safeBrowserExecuteJavascript(javascript);
	}

}
