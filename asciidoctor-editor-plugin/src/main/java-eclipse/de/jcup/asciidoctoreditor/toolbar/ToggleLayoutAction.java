package de.jcup.asciidoctoreditor.toolbar;

import org.eclipse.jface.resource.ImageDescriptor;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;

public class ToggleLayoutAction extends ToolbarAction {

	private static ImageDescriptor IMG_LAYOUT_VERTICAL = createToolbarImageDescriptor("layout_vertical.png");
	private static ImageDescriptor IMG_LAYOUT_HORIZONTAL = createToolbarImageDescriptor("layout_horizontal.png");


	public ToggleLayoutAction(AsciiDoctorEditor asciiDoctorEditor) {
		super(asciiDoctorEditor);
		initUI();
	}

	@Override
	public void run() {
		this.asciiDoctorEditor.setVerticalSplit(!this.asciiDoctorEditor.isVerticalSplit());
		initUI();
	}

	private void initUI() {
		initImage();
		initText();
	}

	private void initImage() {
		setImageDescriptor(this.asciiDoctorEditor.isVerticalSplit() ? IMG_LAYOUT_HORIZONTAL : IMG_LAYOUT_VERTICAL);
	}

	private void initText() {
		setText(this.asciiDoctorEditor.isVerticalSplit() ? "Switch to horizontal layout" : "Switch to vertical layout");
	}

}