package de.jcup.asciidoctoreditor.toolbar;

import org.eclipse.jface.resource.ImageDescriptor;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;

public class JumpToTopOfAsciiDocViewAction extends ToolbarAction {
	
	private static ImageDescriptor IMG_REFRESH = createToolbarImageDescriptor("jump_to_top.png");

		public JumpToTopOfAsciiDocViewAction(AsciiDoctorEditor editor) {
			super(editor);
			initUI();
		}

		@Override
		public void run() {
			asciiDoctorEditor.navgigateToTopOfView();
			
		}

		private void initUI() {
			initImage();
			initText();
		}

		private void initImage() {
			setImageDescriptor(IMG_REFRESH);
		}

		private void initText() {
			setText("Jump to top of ascii view");
		}

	}