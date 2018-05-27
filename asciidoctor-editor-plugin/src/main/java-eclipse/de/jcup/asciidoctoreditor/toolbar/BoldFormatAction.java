package de.jcup.asciidoctoreditor.toolbar;

import org.eclipse.jface.resource.ImageDescriptor;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;

public class BoldFormatAction extends FormatTextAction{
	
	private static ImageDescriptor IMG_DESCRIPTOR_BOLD = createToolbarImageDescriptor("format_bold.png");

	public BoldFormatAction(AsciiDoctorEditor editor) {
		super(editor, "Bold", IMG_DESCRIPTOR_BOLD);
	}

	@Override
	protected String formatPrefix() {
		return "*";
	}

	@Override
	protected String formatPostfix() {
		return "*";
	}

}
