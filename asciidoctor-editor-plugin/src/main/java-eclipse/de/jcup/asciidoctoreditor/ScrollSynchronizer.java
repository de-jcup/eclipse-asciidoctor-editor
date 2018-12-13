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
		
		editor.browserAccess.safeBrowserExecuteJavascript("doScrollTo('"+anchorId+"')");
	}

}
