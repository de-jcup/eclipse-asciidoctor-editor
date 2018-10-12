package de.jcup.asciidoctoreditor.presentation;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class AccessibleDirectoryFieldEditor extends DirectoryFieldEditor {
	
	public AccessibleDirectoryFieldEditor(String name, String labelText, Composite parent) {
		super(name,labelText,parent);
	}
	
	@Override
	public Button getChangeControl(Composite parent) {
		return super.getChangeControl(parent);
	}
}
