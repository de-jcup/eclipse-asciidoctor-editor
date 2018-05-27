package de.jcup.asciidoctoreditor.toolbar;

import org.eclipse.jface.resource.ImageDescriptor;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;

public class MonospacedFormatAction extends FormatTextAction{
	
	private static ImageDescriptor IMG_DESCRIPTOR_MONOSPACED = createToolbarImageDescriptor("format_monospaced.png");

	public MonospacedFormatAction(AsciiDoctorEditor editor) {
		super(editor, "Monospaced", IMG_DESCRIPTOR_MONOSPACED);
	}

	@Override
	protected String formatPrefix() {
		return "`";
	}

	@Override
	protected String formatPostfix() {
		return "`";
	}

}
