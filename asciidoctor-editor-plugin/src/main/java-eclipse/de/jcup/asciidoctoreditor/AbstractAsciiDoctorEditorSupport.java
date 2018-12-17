package de.jcup.asciidoctoreditor;

public abstract class AbstractAsciiDoctorEditorSupport {

    private AsciiDoctorEditor editor;

    public AbstractAsciiDoctorEditorSupport(AsciiDoctorEditor editor) {
        this.editor=editor;
    }
    
    protected AsciiDoctorEditor getEditor() {
        return editor;
    }
    
}
