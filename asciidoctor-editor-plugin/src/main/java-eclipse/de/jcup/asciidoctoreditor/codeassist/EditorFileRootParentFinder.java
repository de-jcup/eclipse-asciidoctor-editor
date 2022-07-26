package de.jcup.asciidoctoreditor.codeassist;

import java.io.File;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;

public class EditorFileRootParentFinder implements RootParentFinder {

    @Override
    public File findRootParent() {
        AsciiDoctorEditor editor = AsciiDoctorEditorUtil.findActiveAsciidoctorEditorOrNull();
        if (editor == null) {
            return null;
        }
        File file = editor.getEditorFileOrNull();
        if (file == null) {
            return null;
        }
        File parentFile = file.getParentFile();
        return parentFile;
    }

}
