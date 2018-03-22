package de.jcup.asciidoctoreditor.toolbar;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.AsciiDoctorEditorUtil;

public abstract class ToolbarAction extends Action {

	protected final AsciiDoctorEditor asciiDoctorEditor;

	public ToolbarAction(AsciiDoctorEditor asciiDoctorEditor) {
		this.asciiDoctorEditor = asciiDoctorEditor;
	}
	
	static ImageDescriptor createToolbarImageDescriptor(String name) {
		return AsciiDoctorEditorUtil.createImageDescriptor("icons/toolbar/" + name);
	}
}
