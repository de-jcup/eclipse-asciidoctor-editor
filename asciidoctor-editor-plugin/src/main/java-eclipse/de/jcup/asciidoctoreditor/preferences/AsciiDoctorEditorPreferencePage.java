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
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
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

import de.jcup.asciidoctoreditor.PreviewLayout;
import de.jcup.asciidoctoreditor.presentation.AccessibleBooleanFieldEditor;

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
		masterSlaveListeners.forEach( (a)->a.updateSlaveComponent());
		
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
		Composite composite = createComposite();

		createUIGroup(composite);
		createExternalPreviewParts(composite);
		createAsciidoctorGroup(composite);
		createSpacer(composite);
		createCodeAssistencGroup(composite);
		
	}

	protected Composite createComposite() {
		Composite composite = new Composite(getFieldEditorParent(), SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		return composite;
	}

	protected void createSpacer(Composite composite) {
		Label spacer = new Label(composite, SWT.LEFT);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = 2;
		gd.heightHint = convertHeightInCharsToPixels(1) / 2;
		spacer.setLayoutData(gd);
	}

	protected void createCodeAssistencGroup(Composite composite) {

		GridData codeAssistGroupLayoutData = new GridData();
		codeAssistGroupLayoutData.horizontalSpan = 2;
		codeAssistGroupLayoutData.widthHint = 400;

		Group codeAssistGroup = new Group(composite, SWT.NONE);
		codeAssistGroup.setText("Code assistence");
		codeAssistGroup.setLayout(new GridLayout());
		codeAssistGroup.setLayoutData(codeAssistGroupLayoutData);

		BooleanFieldEditor codeAssistWithAsciiDoctorKeywords = new BooleanFieldEditor(P_CODE_ASSIST_ADD_KEYWORDS.getId(),
				"AsciiDoctor keywords", codeAssistGroup);
		codeAssistWithAsciiDoctorKeywords.getDescriptionControl(codeAssistGroup).setToolTipText(
				"When enabled the standard keywords supported by asciidoctor editor are always automatically available as code proposals");
		addField(codeAssistWithAsciiDoctorKeywords);

		BooleanFieldEditor codeAssistWithSimpleWords = new BooleanFieldEditor(P_CODE_ASSIST_ADD_SIMPLEWORDS.getId(), "Existing words",
				codeAssistGroup);
		codeAssistWithSimpleWords.getDescriptionControl(codeAssistGroup).setToolTipText(
				"When enabled the current source will be scanned for words. The existing words will be available as code proposals");
		addField(codeAssistWithSimpleWords);

		BooleanFieldEditor toolTipsEnabled = new BooleanFieldEditor(P_TOOLTIPS_ENABLED.getId(), "Tooltips for keywords", codeAssistGroup);
		toolTipsEnabled.getDescriptionControl(codeAssistGroup)
				.setToolTipText("When enabled tool tips will occure for keywords");
		addField(toolTipsEnabled);
	}

	protected void createUIGroup(Composite composite) {
		Composite uiComposite = new Composite(composite, SWT.NONE);
		GridLayout uiLayout = new GridLayout();
		uiLayout.marginWidth = 0;
		uiLayout.marginHeight = 0;
		uiComposite.setLayout(uiLayout);

		String[][] entryNamesAndValues = new String[][] { new String[] { "Vertical", PreviewLayout.VERTICAL.getId() },
				new String[] { "Horizontal", PreviewLayout.HORIZONTAL.getId() },
				new String[] { "Hide preview, use external browser", PreviewLayout.EXTERNAL_BROWSER.getId() } };
		/* @formatter:on */
		ComboFieldEditor previewDefaultTypeRadioButton = new ComboFieldEditor(P_EDITOR_NEWEDITOR_PREVIEW_LAYOUT.getId(),
				"Default preview layout", entryNamesAndValues, uiComposite);

		addField(previewDefaultTypeRadioButton);

		IntegerFieldEditor tocLevels = new IntegerFieldEditor(P_EDITOR_TOC_LEVELS.getId(), "TOC levels shown in preview", uiComposite);
		tocLevels.setValidRange(0, 7);
		tocLevels.setTextLimit(1);
		tocLevels.getLabelControl(uiComposite).setToolTipText(
				"0 keeps defaults from asciidoctor, other will set the wanted depth for TOC on preview only!");

		addField(tocLevels);
		
		BooleanFieldEditor linkEditorWithPreviewEnabled = new BooleanFieldEditor(P_LINK_EDITOR_WITH_PREVIEW.getId(), "Link editor with internal preview", uiComposite);
		linkEditorWithPreviewEnabled.getDescriptionControl(uiComposite)
		.setToolTipText("When enabled editor caret movements are scrolled in internal preview.\n"
				+ "This works only in some situations e.g. when cursor moves to a headline");
		addField(linkEditorWithPreviewEnabled);
	}

	protected void createExternalPreviewParts(Composite composite) {
		GridData externalPreviewGroupLayoutData = new GridData();
		externalPreviewGroupLayoutData.horizontalSpan = 2;
		externalPreviewGroupLayoutData.widthHint = 400;

		Group externalPreviewGroup = new Group(composite, SWT.NONE);
		externalPreviewGroup.setText("External preview");
		externalPreviewGroup.setLayout(new GridLayout());
		externalPreviewGroup.setLayoutData(externalPreviewGroupLayoutData);

		AccessibleBooleanFieldEditor autobuildForExternalPreviewEnabled = new AccessibleBooleanFieldEditor(
				P_EDITOR_AUTOBUILD_FOR_EXTERNAL_PREVIEW_ENABLED.getId(), "Auto build for external preview",
				externalPreviewGroup);
		autobuildForExternalPreviewEnabled.getDescriptionControl(externalPreviewGroup).setToolTipText(
				"When enabled the asciidoctor integration will be called on every change in document. As done in internal previews.\n\n"
				+ "If disabled only a click to 'refresh' or 'show in external browser' buttons inside the toolbar will rebuild the document.\n\n");
		addField(autobuildForExternalPreviewEnabled);

		IntegerFieldEditor autorefreshSeconds = new IntegerFieldEditor(P_EDITOR_AUTOREFRESH_EXTERNAL_BROWSER_IN_SECONDS.getId(),
				"Auto refresh in external preview (in seconds)", externalPreviewGroup);
		autorefreshSeconds.setValidRange(0, 30);
		autorefreshSeconds.setTextLimit(2);
		autorefreshSeconds.getLabelControl(externalPreviewGroup)
				.setToolTipText("0 will turn off auto refresh for external previews.\n\nIf auto build has been disabled, this value will be ignored!");
		addField(autorefreshSeconds);

		createDependency(autobuildForExternalPreviewEnabled.getChangeControl(externalPreviewGroup),
				autorefreshSeconds.getTextControl(externalPreviewGroup));
	}

	protected void createAsciidoctorGroup(Composite composite) {
		GridData groupLayoutData = new GridData();
		groupLayoutData.horizontalSpan = 2;
		groupLayoutData.widthHint = 400;

		Group group = new Group(composite, SWT.NONE);
		group.setText("Asciidoctor");
		group.setLayout(new GridLayout());
		group.setLayoutData(groupLayoutData);

		AccessibleBooleanFieldEditor useInstalledAsciidoctor = new AccessibleBooleanFieldEditor(
				P_USE_INSTALLED_ASCIIDOCTOR_ENABLED.getId(), "Use installed asciidoctor",
				group);
		useInstalledAsciidoctor.getDescriptionControl(group).setToolTipText(
				"When enabled the asciidoctor will be used instead of embedded variant.\n\n"
				+ "Using the installed version enables you to use templates,  newer asciidoctor features etc.\n\n");
		addField(useInstalledAsciidoctor);

		MultiLineStringFieldEditor cliArguments = new MultiLineStringFieldEditor(P_INSTALLED_ASCIICDOCTOR_ARGUMENTS.getId(),
				"Additional\nCLI-Arguments", group);
		cliArguments.getTextControl().setToolTipText("Setup arguments which shall be added to CLI call of installed asciidoctor instance.\n\nYou can use multiple lines.");
		GridData data = new GridData();
		data.verticalAlignment = SWT.CENTER;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.heightHint=75;
		data.widthHint=250;
		
		cliArguments.getTextControl().setLayoutData(data);
		addField(cliArguments);

		createDependency(useInstalledAsciidoctor.getChangeControl(group),
				cliArguments.getTextControl(group));
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

	void setBoolean(AsciiDoctorEditorPreferenceConstants id, boolean value) {
		getPreferences().setBooleanPreference(id, value);
	}

	boolean getBoolean(AsciiDoctorEditorPreferenceConstants id) {
		return getPreferences().getBooleanPreference(id);
	}

	boolean getDefaultBoolean(AsciiDoctorEditorPreferenceConstants id) {
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
