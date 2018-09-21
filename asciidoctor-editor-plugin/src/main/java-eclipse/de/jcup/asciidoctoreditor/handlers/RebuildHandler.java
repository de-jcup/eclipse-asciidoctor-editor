package de.jcup.asciidoctoreditor.handlers;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;

public class RebuildHandler extends AbstractAsciiDoctorEditorHandler {

	public static final String COMMAND_ID = "asciidoctoreditor.editor.commands.rebuild";

	@Override
	protected void executeOnAsciiDoctorEditor(AsciiDoctorEditor asciidoctorEditor) {
		asciidoctorEditor.rebuild();
	}

}
