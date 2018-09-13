package de.jcup.asciidoctoreditor.handlers;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;

public class MakeTextMonospacedHandler extends AbstractAsciiDoctorEditorHandler {

	public static final String COMMAND_ID = "asciidoctoreditor.editor.commands.formattext.monospaced";

	@Override
	protected void executeOnAsciiDoctorEditor(AsciiDoctorEditor asciidoctorEditor) {
		asciidoctorEditor.makeSelectedTextMonoSpaced();
	}

}
