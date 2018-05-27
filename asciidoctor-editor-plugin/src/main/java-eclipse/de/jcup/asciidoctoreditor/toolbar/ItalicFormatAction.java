package de.jcup.asciidoctoreditor.toolbar;

import org.eclipse.jface.resource.ImageDescriptor;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;

public class ItalicFormatAction extends FormatTextAction{
	
	private static ImageDescriptor IMG_DESCRIPTOR_ITALIC = createToolbarImageDescriptor("format_italic.png");

	public ItalicFormatAction(AsciiDoctorEditor editor) {
		super(editor, "Italic", IMG_DESCRIPTOR_ITALIC);
	}

	@Override
	protected String formatPrefix() {
		return "_";
	}

	@Override
	protected String formatPostfix() {
		return "_";
	}

}
