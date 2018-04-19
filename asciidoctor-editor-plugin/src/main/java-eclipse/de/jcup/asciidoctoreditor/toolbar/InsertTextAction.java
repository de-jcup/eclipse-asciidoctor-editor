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
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.AsciiDoctorEditorUtil;

public abstract class InsertTextAction extends ToolbarAction {
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
		beforeInsert(context);
		if (context.canceled){
			return;
		}
		String toInsert = getInsertText(context);
		if (toInsert == null || toInsert.length() == 0) {
			return;
		}
		ITextEditor editor = asciiDoctorEditor;
		IDocumentProvider dp = editor.getDocumentProvider();
		IDocument doc = dp.getDocument(editor.getEditorInput());
		int offset;
		try {
			offset = asciiDoctorEditor.getLastCaretPosition();
			doc.replace(offset, 0, toInsert);
		} catch (BadLocationException e) {
			AsciiDoctorEditorUtil.logError("was not able to insert " + toInsert, e);
		}
		afterInsert(context);
	}
	protected void  afterInsert(InsertTextContext context) {
	}
	
	protected void beforeInsert(InsertTextContext context) {
	}
	
	protected class InsertTextContext{
		protected Object data;
		protected boolean canceled; 
	}

}
