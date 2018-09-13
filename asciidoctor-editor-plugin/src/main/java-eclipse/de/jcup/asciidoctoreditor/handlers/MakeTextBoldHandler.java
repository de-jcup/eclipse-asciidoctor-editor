package de.jcup.asciidoctoreditor.handlers;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;

public class MakeTextBoldHandler extends AbstractAsciiDoctorEditorHandler {

	public static final String COMMAND_ID = "asciidoctoreditor.editor.commands.formattext.bold";

	@Override
	protected void executeOnAsciiDoctorEditor(AsciiDoctorEditor asciidoctorEditor) {
		asciidoctorEditor.makeSelectedTextBold();
	}

}
