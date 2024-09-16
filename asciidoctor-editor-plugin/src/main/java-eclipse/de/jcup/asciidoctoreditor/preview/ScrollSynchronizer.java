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

import static de.jcup.asciidoctoreditor.AsciiDoctorEclipseLogAdapter.*;
import static de.jcup.asciidoctoreditor.EclipseDevelopmentSettings.*;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.outline.Item;
import de.jcup.asciidoctoreditor.outline.ItemType;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.script.AsciidoctorTextSelectable;

/**
 * A synchronizer for scrolling between editor and internal preview
 * 
 * @author Albert Tregnaghi
 *
 */
public class ScrollSynchronizer {

    private AsciiDoctorEditor editor;
    private ScrollSyncMouseListener scrollSyncListener;
    private boolean ignoreNextTreeSelectionChangeEvent;

    public ScrollSynchronizer(AsciiDoctorEditor editor) {
        this.editor = editor;
        this.scrollSyncListener = new ScrollSyncMouseListener();
    }

    public void installInBrowser() {
        editor.getBrowserAccess().installMouseListener(scrollSyncListener);
    }

    class ScrollSyncMouseListener extends MouseAdapter {
        @Override
        public void mouseUp(MouseEvent e) {
            String javascript = "var element=document.elementFromPoint(" + e.x + "," + e.y + ");" + "if (element!=null){ return element.getAttribute('id')} else {return null};";
            String elementId = editor.getBrowserAccess().safeBrowserEvaluateJavascript(javascript);
            if (elementId != null) {
                onMouseClickInBrowser(elementId);
                return;
            }
            if (!AsciiDoctorEditorPreferences.getInstance().isLinkEditorWithPreviewUsingTextSelectionAsFallback()) {
                return;
            }
            // not a click to a defined element (e.g. headline) inside the browser
            // so we try to find the text
            javascript = "var element=document.elementFromPoint(" + e.x + "," + e.y + ");" + "if (element!=null){ return element.textContent} else {return null};";
            String browserText = editor.getBrowserAccess().safeBrowserEvaluateJavascript(javascript);
            if (browserText == null) {
                return;
            }
            String textToSearch = browserText;
            String editorText = editor.getDocumentText();
            int loopcount = 0;
            int maxTextLoopCount = 1000;

            int pos = -1;
            do {
                if (textToSearch.length() < 2) {
                    break;
                }
                pos = editorText.indexOf(textToSearch);
                if (pos == -1) {
                    textToSearch = textToSearch.substring(0, textToSearch.length() - 2);
                }
            } while (loopcount < maxTextLoopCount && pos == -1);

            if (pos == -1) {
                return;
            }
            editor.selectAndReveal(pos, textToSearch.length());

        }
    }

    private void onMouseClickInBrowser(String elementId) {
        if (!editor.getPreferences().isLinkEditorWithPreviewEnabled()) {
            return;
        }
        if (elementId == null) {
            return;
        }
        AsciidoctorTextSelectable selectable = editor.findAsciiDoctorPositionByElementId(elementId);
        if (selectable == null) {
            return;
        }
        int offset = selectable.getSelectionStart();
        if (offset < 0) {
            return;
        }
        editor.setFocus();
        editor.selectAndReveal(offset, selectable.getSelectionLength());
        ignoreNextTreeSelectionChangeEvent = true; // avoid reselect by tree editor
        editor.getOutlineSupport().selectItemPointingTo(selectable.getPosition());// will fire tree selection change

    }

    public void onEditorCaretMoved(int caretOffset) {
        if (!editor.getPreferences().isLinkEditorWithPreviewEnabled()) {
            return;
        }
        if (!editor.isInternalPreview()) {
            return;
        }
        Item item = editor.getItemAt(caretOffset);
        if (item != null) {
            if (DEBUG_LOGGING_ENABLED) {
                INSTANCE.logInfo("Editor caret moved to item:" + item);
            }
            handleScrollToHeadlineIfPossible(item);
            handleScrollToAnchorIfPossible(item);
        }
        // we do not synch here automatically with editor - reason: Only when the 
        // file is saved, the preview is rendered with new values and could be searched
    }

    private void handleScrollToHeadlineIfPossible(Item item) {
        ItemType itemType = item.getItemType();
        if (!ItemType.HEADLINE.equals(itemType)) {
            return;
        }
        jumpToElementWithItemId(item);
    }

    private void handleScrollToAnchorIfPossible(Item item) {
        ItemType itemType = item.getItemType();
        if (!ItemType.INLINE_ANCHOR.equals(itemType)) {
            return;
        }
        jumpToElementWithItemId(item);
    }

    protected void jumpToElementWithItemId(Item item) {
        if (ignoreNextTreeSelectionChangeEvent) {
            ignoreNextTreeSelectionChangeEvent = false;
            return;
        }
        String anchorId = item.getId();
        if (DEBUG_LOGGING_ENABLED) {
            INSTANCE.logInfo("Item has anchor id:" + anchorId);
        }
        if (anchorId == null) {
            /* means first title */
            editor.getBrowserAccess().navgigateToTopOfView();
            return;
        }

        String javascript = "doScrollTo('" + anchorId + "')";
        if (DEBUG_LOGGING_ENABLED) {
            INSTANCE.logInfo("Call browser access with javascript:" + javascript);
        }
        editor.getBrowserAccess().safeBrowserExecuteJavascript(javascript);
    }

}
