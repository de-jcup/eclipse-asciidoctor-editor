package de.jcup.asciidoctoreditor.toolbar;

import org.eclipse.jface.resource.ImageDescriptor;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;

public class NewTableInsertAction extends InsertTextAction {

	private static ImageDescriptor IMG_NEW_TABLE = createToolbarImageDescriptor("table.gif");

	public NewTableInsertAction(AsciiDoctorEditor editor) {
		super(editor, "Insert a table", IMG_NEW_TABLE);
	}

	@Override
	protected String getInsertText() {
		StringBuilder sb = new StringBuilder();
		sb.append("[options=\"header\",cols=\"1,2,2\"]\n");
		sb.append("|===");
		sb.append("\n");
		sb.append("|HeadA|HeadB|HeadC");
		sb.append("\n");
		sb.append("|Row1A|Row1B|Row1C");
		sb.append("\n");
		sb.append("|Row2A|Row2B|Row2C");
		sb.append("\n");
		sb.append("|===");
		sb.append("\n");
		return sb.toString();
	}

}
