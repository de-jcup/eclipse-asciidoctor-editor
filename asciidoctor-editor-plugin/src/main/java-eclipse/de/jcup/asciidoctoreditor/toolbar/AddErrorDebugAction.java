package de.jcup.asciidoctoreditor.toolbar;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.AsciiDoctorEditorUtil;
import de.jcup.asciidoctoreditor.script.AsciiDoctorError;

public class AddErrorDebugAction extends ToolbarAction implements DebugAction {

	public AddErrorDebugAction(AsciiDoctorEditor editor) {
		super(editor);
		initUI();
	}

	@Override
	public void run() {
		AsciiDoctorError error = new AsciiDoctorError(-1, -1, "the message at "+System.currentTimeMillis()+" millis");
		AsciiDoctorEditorUtil.addScriptError(asciiDoctorEditor, -1, error, IMarker.SEVERITY_ERROR);
	}

	private void initUI() {
		initImage();
		initText();
	}

	private void initImage() {
		ImageDescriptor sharedImage = PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_ERROR_TSK);
		setImageDescriptor(sharedImage);
	}

	private void initText() {
		setText("Add an error");
	}

}