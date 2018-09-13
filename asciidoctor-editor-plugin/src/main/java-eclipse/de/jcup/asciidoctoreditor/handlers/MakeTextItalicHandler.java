package de.jcup.asciidoctoreditor.handlers;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;

public class MakeTextItalicHandler extends AbstractAsciiDoctorEditorHandler {

	public static final String COMMAND_ID = "asciidoctoreditor.editor.commands.formattext.italic";

	@Override
	protected void executeOnAsciiDoctorEditor(AsciiDoctorEditor asciidoctorEditor) {
		asciidoctorEditor.makeSelectedTextItalic();;
	}

}
