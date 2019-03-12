package de.jcup.asciidoctoreditor.handlers;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;

public class AddLineBreakHandler extends AbstractAsciiDoctorEditorHandler {

    public static final String COMMAND_ID = "asciidoctoreditor.editor.commands.source.addlinebreak";

    @Override
    protected void executeOnAsciiDoctorEditor(AsciiDoctorEditor asciidoctorEditor) {
        asciidoctorEditor.addLineBreak();
    }

}
