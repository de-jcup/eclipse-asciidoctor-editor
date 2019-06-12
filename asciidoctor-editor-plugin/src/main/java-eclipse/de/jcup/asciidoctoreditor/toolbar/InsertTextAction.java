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
package de.jcup.asciidoctoreditor.toolbar;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;

public abstract class InsertTextAction extends ToolbarAction {
	// see
	// https://wiki.eclipse.org/FAQ_How_do_I_insert_text_in_the_active_text_editor%3F
	protected abstract String getInsertText(InsertTextContext context);

	protected InsertTextAction(AsciiDoctorEditor editor, String text, ImageDescriptor descriptor) {
		super(editor);
		setText(text);
		setImageDescriptor(descriptor);
	}

	@Override
	public void run() {
		InsertTextContext context = new InsertTextContext();
		ITextEditor editor = asciiDoctorEditor;

		ISelection selection = editor.getSelectionProvider().getSelection();
		if (selection instanceof ITextSelection) {
			ITextSelection ts = (ITextSelection) selection;
			context.selectedText = ts.getText();
			context.selectedLength = ts.getLength();
			context.selectedOffset = ts.getOffset();
		}
		beforeInsert(context);

		if (context.canceled) {
			return;
		}
		String toInsert = getInsertText(context);
		if (toInsert == null || toInsert.length() == 0) {
			return;
		}

		IDocumentProvider dp = editor.getDocumentProvider();
		IDocument doc = dp.getDocument(editor.getEditorInput());
		try {
			int offsetBefore = context.selectedOffset;//asciiDoctorEditor.getLastCaretPosition();
			if (offsetBefore==-1){
				offsetBefore = asciiDoctorEditor.getLastCaretPosition();
			}
			doc.replace(offsetBefore, context.selectedLength, toInsert);
			if (context.nextOffset != -1) {
				Control control = editor.getAdapter(Control.class);
				if (control instanceof StyledText) {
					StyledText text = (StyledText) control;
					text.setCaretOffset(context.nextOffset);
				}
			}
		} catch (BadLocationException e) {
			AsciiDoctorEditorUtil.logError("was not able to insert " + toInsert, e);
		}
		afterInsert(context);
	}

	protected void afterInsert(InsertTextContext context) {
	}

	protected void beforeInsert(InsertTextContext context) {
	}

	protected class InsertTextContext {
		/**
		 * Next offset after insert is done and not canceled. If -1 (default) the 
		 * cursor location will be still on cursor before inset - so before inserted text
		 */
		public int nextOffset = -1;
		public int selectedOffset;
		public int selectedLength;
		public String selectedText;
		protected Object data;
		protected boolean canceled;
	}

}
