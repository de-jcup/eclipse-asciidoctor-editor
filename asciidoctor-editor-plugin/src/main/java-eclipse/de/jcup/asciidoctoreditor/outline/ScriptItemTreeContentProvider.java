package de.jcup.asciidoctoreditor.outline;

import org.eclipse.jface.viewers.ITreeContentProvider;

import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModel;

public interface ScriptItemTreeContentProvider extends ITreeContentProvider {

    void rebuildTree(AsciiDoctorScriptModel model);

    Item tryToFindByOffset(int offset);

}