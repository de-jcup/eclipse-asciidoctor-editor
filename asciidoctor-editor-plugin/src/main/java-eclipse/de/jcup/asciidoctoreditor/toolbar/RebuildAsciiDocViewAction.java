package de.jcup.asciidoctoreditor.toolbar;

import org.eclipse.jface.resource.ImageDescriptor;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;

public class RebuildAsciiDocViewAction extends ToolbarAction {
	
	private static ImageDescriptor IMG_REFRESH = createToolbarImageDescriptor("refresh.png");

	private static ImageDescriptor IMG_LAYOUT_VERTICAL = createToolbarImageDescriptor("layout_vertical.png");
	private static ImageDescriptor IMG_LAYOUT_HORIZONTAL = createToolbarImageDescriptor("layout_horizontal.png");

	
		public RebuildAsciiDocViewAction(AsciiDoctorEditor editor) {
			super(editor);
			initUI();
		}

		@Override
		public void run() {
			asciiDoctorEditor.resetCache();
			asciiDoctorEditor.updateAsciiDocView();
			initUI();
		}

		private void initUI() {
			initImage();
			initText();
		}

		private void initImage() {
			setImageDescriptor(IMG_REFRESH);
		}

		private void initText() {
			setText("Rebuild ascii doc view (e.g. when includes or imageDir have been changed)");
		}

	}