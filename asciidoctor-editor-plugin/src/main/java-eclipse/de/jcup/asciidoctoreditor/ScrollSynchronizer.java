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
		handleScrollToAnchorIfPossible(item);
	}

	private void handleScrollToHeadlineIfPossible(Item item) {
		ItemType itemType = item.getItemType();
		if (!ItemType.HEADLINE.equals(itemType)){
			return;
		}
		jumpToElementWithItemId(item);
	}
	
	private void handleScrollToAnchorIfPossible(Item item) {
		ItemType itemType = item.getItemType();
		if (!ItemType.INLINE_ANCHOR.equals(itemType)){
			return;
		}
		jumpToElementWithItemId(item);
	}

	protected void jumpToElementWithItemId(Item item) {
		String anchorId = item.getId();
		if (anchorId == null) {
			/* means first title */
			editor.browserAccess.navgigateToTopOfView();
			return;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("document.getElementById('");
		sb.append(anchorId);
		sb.append("').scrollIntoView();");

		String javascript = sb.toString();

		editor.browserAccess.safeBrowserExecuteJavascript(javascript);
	}

}
