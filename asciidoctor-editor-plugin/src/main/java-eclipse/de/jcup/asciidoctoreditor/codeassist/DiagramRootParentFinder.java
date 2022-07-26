package de.jcup.asciidoctoreditor.codeassist;

import java.io.File;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;

public class DiagramRootParentFinder implements RootParentFinder {

    private DiagramBaseParentResolver resolver = new DiagramBaseParentResolver();
    
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
        return resolver.getBaseParentDir(file);
    }

}
