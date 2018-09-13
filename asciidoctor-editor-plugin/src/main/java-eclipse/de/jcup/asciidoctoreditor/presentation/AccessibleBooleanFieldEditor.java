package de.jcup.asciidoctoreditor.presentation;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class AccessibleBooleanFieldEditor extends BooleanFieldEditor{

	public AccessibleBooleanFieldEditor(String name, String label, Composite parent) {
		super(name, label, parent);
	}

	public AccessibleBooleanFieldEditor(String name, String labelText, int style, Composite parent) {
		super(name, labelText, style, parent);
	}

	@Override
	public Button getChangeControl(Composite parent) {
		return super.getChangeControl(parent);
	}

}
