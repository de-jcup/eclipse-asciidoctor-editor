package de.jcup.asciidoctoreditor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.texteditor.IDocumentProvider;

public class AsciiDoctorEditorCommentSupport extends AbstractAsciiDoctorEditorSupport{


    public AsciiDoctorEditorCommentSupport(AsciiDoctorEditor editor) {
        super(editor);
    }
    
    /**
     * Toggles comment of current selected lines
     */
    public void toggleComment() {
        ISelection selection = getEditor().getSelectionProvider().getSelection();
        if (!(selection instanceof TextSelection)) {
            return;
        }
        IDocumentProvider dp = getEditor().getDocumentProvider();
        IDocument doc = dp.getDocument(getEditor().getEditorInput());
        TextSelection ts = (TextSelection) selection;
        int startLine = ts.getStartLine();
        int endLine = ts.getEndLine();

        /* do comment /uncomment */
        String toggleCommentCodePart = getEditor().getToggleCommentCodePart();
        int toggleCommentCodePartLength = toggleCommentCodePart.length();

        for (int i = startLine; i <= endLine; i++) {
            IRegion info;
            try {
                info = doc.getLineInformation(i);
                int offset = info.getOffset();
                String line = doc.get(info.getOffset(), info.getLength());
                StringBuilder foundCode = new StringBuilder();
                StringBuilder whitespaces = new StringBuilder();
                for (int j = 0; j < line.length(); j++) {
                    char ch = line.charAt(j);
                    if (Character.isWhitespace(ch)) {
                        if (foundCode.length() == 0) {
                            whitespaces.append(ch);
                        }
                    } else {
                        foundCode.append(ch);
                    }
                    if (foundCode.length() > toggleCommentCodePartLength - 1) {
                        break;
                    }
                }
                int whitespaceOffsetAdd = whitespaces.length();
                if (toggleCommentCodePart.equals(foundCode.toString())) {
                    /* comment before */
                    doc.replace(offset + whitespaceOffsetAdd, toggleCommentCodePartLength, "");
                } else {
                    /* not commented */
                    doc.replace(offset, 0, toggleCommentCodePart);
                }

            } catch (BadLocationException e) {
                /* ignore and continue */
                continue;
            }

        }
        /* reselect */
        int selectionStartOffset;
        try {
            selectionStartOffset = doc.getLineOffset(startLine);
            int endlineOffset = doc.getLineOffset(endLine);
            int endlineLength = doc.getLineLength(endLine);
            int endlineLastPartOffset = endlineOffset + endlineLength;
            int length = endlineLastPartOffset - selectionStartOffset;

            ISelection newSelection = new TextSelection(selectionStartOffset, length);
            getEditor().getSelectionProvider().setSelection(newSelection);
        } catch (BadLocationException e) {
            /* ignore */
        }
    }
}
