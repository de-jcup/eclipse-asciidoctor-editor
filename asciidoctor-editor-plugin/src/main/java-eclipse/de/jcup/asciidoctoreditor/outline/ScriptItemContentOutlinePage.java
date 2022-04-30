package de.jcup.asciidoctoreditor.outline;

import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModel;

public interface ScriptItemContentOutlinePage extends IContentOutlinePage {

    public ScriptItemTreeContentProvider getScriptItemTreeContentProvider();

    public void rebuild(AsciiDoctorScriptModel model);

    public void onEditorCaretMoved(int caretOffset);
}
