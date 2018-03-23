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
