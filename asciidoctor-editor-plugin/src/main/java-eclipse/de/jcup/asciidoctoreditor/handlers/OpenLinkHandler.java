package de.jcup.asciidoctoreditor.handlers;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;

public class OpenLinkHandler extends AbstractAsciiDoctorEditorHandler {

	public static final String COMMAND_ID = "asciidoctoreditor.editor.commands.openlink";

	@Override
	protected void executeOnAsciiDoctorEditor(AsciiDoctorEditor asciidoctorEditor) {
		asciidoctorEditor.openHyperlinkAtCurrentCursorPosition();
	}

}
