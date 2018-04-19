package de.jcup.asciidoctoreditor.toolbar;

import org.eclipse.jface.resource.ImageDescriptor;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;

public class ToggleTOCAction extends ToolbarAction {

	private static ImageDescriptor IMG_TOC_SHOW = createToolbarImageDescriptor("toc_show.png");
	private static ImageDescriptor IMG_TOC_HIDE = createToolbarImageDescriptor("toc_hide.png");

	public ToggleTOCAction(AsciiDoctorEditor asciiDoctorEditor) {
		super(asciiDoctorEditor);
		initUI();
	}

	@Override
	public void run() {
		asciiDoctorEditor.setTOCShown(!asciiDoctorEditor.isTOCShown());
		initUI();
	}

	private void initUI() {
		initImage();
		initText();
	}

	private void initImage() {
		setImageDescriptor(this.asciiDoctorEditor.isTOCShown() ? IMG_TOC_SHOW : IMG_TOC_HIDE);
	}

	private void initText() {
		setText(this.asciiDoctorEditor.isTOCShown() ? "TOC is shown" : "TOC is hidden");
	}

}