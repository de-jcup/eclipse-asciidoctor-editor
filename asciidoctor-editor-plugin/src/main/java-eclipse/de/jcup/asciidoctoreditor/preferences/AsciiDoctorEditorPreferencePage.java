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

import static de.jcup.asciidoctoreditor.AsciiDoctorEditorUtil.*;
import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferenceConstants.*;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Parts are inspired by <a href=
 * "https://github.com/eclipse/eclipse.jdt.ui/blob/master/org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/preferences/JavaEditorAppearanceConfigurationBlock.java">org.eclipse.jdt.internal.ui.preferences.JavaEditorAppearanceConfigurationBlock
 * </a>
 * 
 * @author Albert Tregnaghi
 *
 */
public class AsciiDoctorEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	protected static final int INDENT = 20;

	protected static void indent(Control control) {
		((GridData) control.getLayoutData()).horizontalIndent += INDENT;
	}

	// private BooleanFieldEditor linkEditorWithOutline;

	private ArrayList<MasterButtonSlaveSelectionListener> masterSlaveListeners = new ArrayList<>();

	private BooleanFieldEditor codeAssistWithAsciiDoctorKeywords;
	private BooleanFieldEditor codeAssistWithSimpleWords;
	private BooleanFieldEditor toolTipsEnabled;

	public AsciiDoctorEditorPreferencePage() {
		super(GRID);
		setPreferenceStore(getPreferences().getPreferenceStore());
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	public void performDefaults() {
		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		boolean ok = super.performOk();
		return ok;
	}

	protected void createDependency(Button master, Control slave) {
		Assert.isNotNull(slave);
		indent(slave);
		MasterButtonSlaveSelectionListener listener = new MasterButtonSlaveSelectionListener(master, slave);
		master.addSelectionListener(listener);
		this.masterSlaveListeners.add(listener);
	}

	@Override
	protected void createFieldEditors() {
		Composite appearanceComposite = new Composite(getFieldEditorParent(), SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		appearanceComposite.setLayout(layout);

		/* OTHER */
		Composite otherComposite = new Composite(appearanceComposite, SWT.NONE);
		GridLayout otherLayout = new GridLayout();
		otherLayout.marginWidth = 0;
		otherLayout.marginHeight = 0;
		otherComposite.setLayout(otherLayout);

		// /* linking with outline */
		// linkEditorWithOutline = new
		// BooleanFieldEditor(P_LINK_OUTLINE_WITH_EDITOR.getId(),
		// "New opened editors are linked with outline", otherComposite);
		// linkEditorWithOutline.getDescriptionControl(otherComposite)
		// .setToolTipText("Via this setting the default behaviour for new
		// opened outlines is set");
		// addField(linkEditorWithOutline);

		Label spacer = new Label(appearanceComposite, SWT.LEFT);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		gd.heightHint = convertHeightInCharsToPixels(1) / 2;
		spacer.setLayoutData(gd);

		/* --------------------- */
		/* -- Code assistance -- */
		/* --------------------- */

		GridData codeAssistGroupLayoutData = new GridData();
		codeAssistGroupLayoutData.horizontalSpan = 2;
		codeAssistGroupLayoutData.widthHint = 400;

		Group codeAssistGroup = new Group(appearanceComposite, SWT.NONE);
		codeAssistGroup.setText("Code assistence");
		codeAssistGroup.setLayout(new GridLayout());
		codeAssistGroup.setLayoutData(codeAssistGroupLayoutData);

		codeAssistWithAsciiDoctorKeywords = new BooleanFieldEditor(P_CODE_ASSIST_ADD_KEYWORDS.getId(),
				"AsciiDoctor keywords", codeAssistGroup);
		codeAssistWithAsciiDoctorKeywords.getDescriptionControl(codeAssistGroup).setToolTipText(
				"When enabled the standard keywords supported by asciidoctor editor are always automatically available as code proposals");
		addField(codeAssistWithAsciiDoctorKeywords);

		codeAssistWithSimpleWords = new BooleanFieldEditor(P_CODE_ASSIST_ADD_SIMPLEWORDS.getId(), "Existing words",
				codeAssistGroup);
		codeAssistWithSimpleWords.getDescriptionControl(codeAssistGroup).setToolTipText(
				"When enabled the current source will be scanned for words. The existing words will be available as code proposals");
		addField(codeAssistWithSimpleWords);

		toolTipsEnabled = new BooleanFieldEditor(P_TOOLTIPS_ENABLED.getId(), "Tooltips for keywords", codeAssistGroup);
		toolTipsEnabled.getDescriptionControl(codeAssistGroup)
				.setToolTipText("When enabled tool tips will occure for keywords");
		addField(toolTipsEnabled);

	}

	@Override
	protected void initialize() {
		super.initialize();
		updateSlaveComponents();
	}

	protected Button addButton(Composite parent, int style, String label, int indentation, SelectionListener listener) {
		Button button = new Button(parent, style);
		button.setText(label);

		GridData gd = new GridData(32);
		gd.horizontalIndent = indentation;
		gd.horizontalSpan = 2;
		button.setLayoutData(gd);
		button.addSelectionListener(listener);

		return button;
	}

	private void setBoolean(AsciiDoctorEditorPreferenceConstants id, boolean value) {
		getPreferences().setBooleanPreference(id, value);
	}

	private boolean getBoolean(AsciiDoctorEditorPreferenceConstants id) {
		return getPreferences().getBooleanPreference(id);
	}

	private boolean getDefaultBoolean(AsciiDoctorEditorPreferenceConstants id) {
		return getPreferences().getDefaultBooleanPreference(id);
	}

	private void updateSlaveComponents() {
		for (MasterButtonSlaveSelectionListener listener : masterSlaveListeners) {
			listener.updateSlaveComponent();
		}
	}

	private class MasterButtonSlaveSelectionListener implements SelectionListener {
		private Button master;
		private Control slave;

		public MasterButtonSlaveSelectionListener(Button master, Control slave) {
			this.master = master;
			this.slave = slave;
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {

		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			updateSlaveComponent();
		}

		private void updateSlaveComponent() {
			boolean state = master.getSelection();
			slave.setEnabled(state);
		}

	}

}
