package de.jcup.asciidoctoreditor.preferences;
/*
 * Copyright 2017 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */

import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorValidationPreferenceConstants.*;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.asciidoctoreditor.AsciiDoctorEditorUtil;
import de.jcup.asciidoctoreditor.script.parser.validator.AsciiDoctorEditorValidationErrorLevel;

public class AsciiDoctorEditorValidationPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public AsciiDoctorEditorValidationPreferencePage() {
		setPreferenceStore(AsciiDoctorEditorUtil.getPreferences().getPreferenceStore());
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();

		createEditor(VALIDATE_IF_STATEMENTS, parent);
		createEditor(VALIDATE_DO_STATEMENTS, parent);
		createEditor(VALIDATE_FUNCTION_STATEMENTS, parent);
		createEditor(VALIDATE_BLOCK_STATEMENTS, parent);

		createComboBox(VALIDATE_ERROR_LEVEL, parent);
	}

	private void createComboBox(AsciiDoctorEditorValidationPreferenceConstants constant, Composite parent) {
		String name = constant.getId();
		String labelText = constant.getLabelText();

		/* @formatter:off */
		String[][] entryNamesAndValues = 
				new String[][] { 
					getLabelAndValue(AsciiDoctorEditorValidationErrorLevel.ERROR),
					getLabelAndValue(AsciiDoctorEditorValidationErrorLevel.WARNING),
					getLabelAndValue(AsciiDoctorEditorValidationErrorLevel.INFO)
		};
		/* @formatter:on */
		
		Composite composite = new Composite(parent, SWT.NONE);
		
		ComboFieldEditor comboFieldEditor = new ComboFieldEditor(name, labelText, entryNamesAndValues, composite);
		addField(comboFieldEditor);
	}

	private String[] getLabelAndValue(AsciiDoctorEditorValidationErrorLevel errorLevel) {
		return new String[] { errorLevel.name(), errorLevel.getId() };
	}

	private BooleanFieldEditor createEditor(AsciiDoctorEditorValidationPreferenceConstants constant, Composite parent) {
		BooleanFieldEditor editor = new BooleanFieldEditor(constant.getId(), constant.getLabelText(), parent);
		addField(editor);
		return editor;
	}

}