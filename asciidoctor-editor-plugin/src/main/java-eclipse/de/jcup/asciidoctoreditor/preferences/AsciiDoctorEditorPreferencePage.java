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
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
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
import de.jcup.asciidoctoreditor.presentation.AccessibleDirectoryFieldEditor;

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
		if (control.getLayoutData() instanceof GridData){
			((GridData) control.getLayoutData()).horizontalIndent += INDENT;
		}
	}

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
		masterSlaveListeners.forEach((a) -> a.updateSlaveComponent());

	}

	@Override
	public boolean performOk() {
		boolean ok = super.performOk();
		return ok;
	}
	protected void createDependency(Button master, Control slave) {
		createDependency(master, slave,true);
	}
	protected void createDependency(Button master, Control slave, boolean indent) {
		Assert.isNotNull(slave);
		if (indent){
			indent(slave);
		}
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
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
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
		Group codeAssistGroup = new Group(composite, SWT.NONE);
		codeAssistGroup.setText("Code assistence");
		codeAssistGroup.setLayout(new GridLayout());
		codeAssistGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		// why devNull ? because of layout problems on field editors + groups.
		// See
		// https://stackoverflow.com/questions/490015/layout-problems-in-fieldeditorpreferencepage
		Composite devNull = new Composite(codeAssistGroup, SWT.NONE);

		BooleanFieldEditor codeAssistWithAsciiDoctorKeywords = new BooleanFieldEditor(
				P_CODE_ASSIST_ADD_KEYWORDS.getId(), "AsciiDoctor keywords", devNull);
		codeAssistWithAsciiDoctorKeywords.getDescriptionControl(devNull).setToolTipText(
				"When enabled the standard keywords supported by asciidoctor editor are always automatically available as code proposals");
		addField(codeAssistWithAsciiDoctorKeywords);

		devNull = new Composite(codeAssistGroup, SWT.NONE);
		BooleanFieldEditor codeAssistWithSimpleWords = new BooleanFieldEditor(P_CODE_ASSIST_ADD_SIMPLEWORDS.getId(),
				"Existing words", devNull);
		codeAssistWithSimpleWords.getDescriptionControl(devNull).setToolTipText(
				"When enabled the current source will be scanned for words. The existing words will be available as code proposals");
		addField(codeAssistWithSimpleWords);

		devNull = new Composite(codeAssistGroup, SWT.NONE);
		BooleanFieldEditor toolTipsEnabled = new BooleanFieldEditor(P_TOOLTIPS_ENABLED.getId(), "Tooltips for keywords",
				devNull);
		toolTipsEnabled.getDescriptionControl(devNull)
				.setToolTipText("When enabled tool tips will occure for keywords");
		addField(toolTipsEnabled);
	}

	protected void createUIGroup(Composite composite) {
		Composite uiComposite = new Composite(composite, SWT.NONE);
		GridLayout uiLayout = new GridLayout();
		uiLayout.marginWidth = 0;
		uiLayout.marginHeight = 0;
		uiComposite.setLayout(uiLayout);

		uiComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		Composite devNull = new Composite(uiComposite, SWT.NONE);
		String[][] entryNamesAndValues = new String[][] { new String[] { "Vertical", PreviewLayout.VERTICAL.getId() },
				new String[] { "Horizontal", PreviewLayout.HORIZONTAL.getId() },
				new String[] { "Hide preview, use external browser", PreviewLayout.EXTERNAL_BROWSER.getId() } };
		/* @formatter:on */
		ComboFieldEditor previewDefaultTypeRadioButton = new ComboFieldEditor(P_EDITOR_NEWEDITOR_PREVIEW_LAYOUT.getId(),
				"Default preview layout", entryNamesAndValues, devNull);

		addField(previewDefaultTypeRadioButton);

		devNull = new Composite(uiComposite, SWT.NONE);
		IntegerFieldEditor tocLevels = new IntegerFieldEditor(P_EDITOR_TOC_LEVELS.getId(),
				"TOC levels shown in preview", devNull);
		tocLevels.setValidRange(0, 7);
		tocLevels.setTextLimit(1);
		tocLevels.getLabelControl(devNull).setToolTipText(
				"0 keeps defaults from asciidoctor, other will set the wanted depth for TOC on preview only!");

		addField(tocLevels);

		devNull = new Composite(uiComposite, SWT.NONE);
		BooleanFieldEditor linkEditorWithPreviewEnabled = new BooleanFieldEditor(P_LINK_EDITOR_WITH_PREVIEW.getId(),
				"Link editor with internal preview", devNull);
		linkEditorWithPreviewEnabled.getDescriptionControl(devNull)
				.setToolTipText("When enabled editor caret movements are scrolled in internal preview.\n"
						+ "This works only in some situations e.g. when cursor moves to a headline");
		addField(linkEditorWithPreviewEnabled);
	}

	protected void createExternalPreviewParts(Composite composite) {
		Group externalPreviewGroup = new Group(composite, SWT.NONE);
		externalPreviewGroup.setText("External preview");
		externalPreviewGroup.setLayout(new GridLayout());
		externalPreviewGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		Composite devNull1 = new Composite(externalPreviewGroup, SWT.NONE);
		AccessibleBooleanFieldEditor autobuildForExternalPreviewEnabled = new AccessibleBooleanFieldEditor(
				P_EDITOR_AUTOBUILD_FOR_EXTERNAL_PREVIEW_ENABLED.getId(), "Auto build for external preview", devNull1);
		autobuildForExternalPreviewEnabled.getDescriptionControl(devNull1).setToolTipText(
				"When enabled the asciidoctor integration will be called on every change in document. As done in internal previews.\n\n"
						+ "If disabled only a click to 'refresh' or 'show in external browser' buttons inside the toolbar will rebuild the document.\n\n");
		addField(autobuildForExternalPreviewEnabled);

		Composite devNull2 = new Composite(externalPreviewGroup, SWT.NONE);
		IntegerFieldEditor autorefreshSeconds = new IntegerFieldEditor(
				P_EDITOR_AUTOREFRESH_EXTERNAL_BROWSER_IN_SECONDS.getId(),
				"Auto refresh in external preview (in seconds)", devNull2);
		autorefreshSeconds.setValidRange(0, 30);
		autorefreshSeconds.setTextLimit(2);
		autorefreshSeconds.getLabelControl(devNull2).setToolTipText(
				"0 will turn off auto refresh for external previews.\n\nIf auto build has been disabled, this value will be ignored!");
		addField(autorefreshSeconds);

		createDependency(autobuildForExternalPreviewEnabled.getChangeControl(devNull1),
				autorefreshSeconds.getTextControl(devNull2));
	}

	protected void createAsciidoctorGroup(Composite composite) {

		Group group = new Group(composite, SWT.NONE);
		group.setText("Asciidoctor");
		group.setLayout(new GridLayout(1,false));
		group.setLayoutData(new GridData(SWT.FILL,SWT.TOP, true,false));

		Composite devNull1 = new Composite(group,SWT.NONE);
		AccessibleBooleanFieldEditor useInstalledAsciidoctor = new AccessibleBooleanFieldEditor(
				P_USE_INSTALLED_ASCIIDOCTOR_ENABLED.getId(), "Use installed asciidoctor",
				devNull1);
		useInstalledAsciidoctor.getDescriptionControl(devNull1).setToolTipText(
				"When enabled the installed asciidoctor will be used instead of embedded variant.\n\n"
				+ "Using the installed version enables you to use templates,  newer asciidoctor features etc. Just setup your behaviour in CLI arguments.\n\n");
		addField(useInstalledAsciidoctor);
		
		Composite group2 = new Composite(group,SWT.NONE);
		GridData group2Data = new GridData(SWT.FILL,SWT.TOP, true,false);
		group2.setLayoutData(group2Data);
		group2.setLayout(new GridLayout());

//		Composite devNull2a = new Composite(group2,SWT.NONE);
//		AccessibleDirectoryFieldEditor pathToAsciidocFieldEditor = new AccessibleDirectoryFieldEditor(P_PATH_TO_INSTALLED_ASCIICDOCTOR.getId(),
//				"Path to Asciidoctor", devNull2a);
//		devNull2a.setLayout(new GridLayout());
//		devNull2a.setLayoutData(new GridData(SWT.FILL,SWT.TOP, true,false));  
//		
//		addField(pathToAsciidocFieldEditor);
//		
//		createDependency(useInstalledAsciidoctor.getChangeControl(devNull1),
//				pathToAsciidocFieldEditor.getTextControl(devNull2a));
//		createDependency(useInstalledAsciidoctor.getChangeControl(devNull1),
//				pathToAsciidocFieldEditor.getLabelControl(devNull2a));
//		createDependency(useInstalledAsciidoctor.getChangeControl(devNull1),
//				pathToAsciidocFieldEditor.getChangeControl(devNull2a));
		
		Composite devNull2 = new Composite(group2,SWT.NONE);
		MultiLineStringFieldEditor cliArguments = new MultiLineStringFieldEditor(P_INSTALLED_ASCIICDOCTOR_ARGUMENTS.getId(),
				"Custom arguments for Asciidoctor CLI call", devNull2);
		cliArguments.getTextControl().setToolTipText("Setup arguments which shall be added to CLI call of installed asciidoctor instance.\n\nYou can use multiple lines.");
		GridData cliTextLayoutData = new GridData(SWT.FILL,SWT.TOP, true,false);
		cliArguments.getTextControl().setLayoutData(cliTextLayoutData);
		devNull2.setLayoutData(new GridData(SWT.FILL,SWT.TOP, true,false));
		devNull2.setLayout(new GridLayout());
		addField(cliArguments);

		createDependency(useInstalledAsciidoctor.getChangeControl(devNull1),
				cliArguments.getTextControl(devNull2),false);
		createDependency(useInstalledAsciidoctor.getChangeControl(devNull1),
				cliArguments.getLabelControl(devNull2),false);
		createDependency(useInstalledAsciidoctor.getChangeControl(devNull1),
				group2,false);
		
		Composite devNull3 = new Composite(group2,SWT.NONE);
		AccessibleBooleanFieldEditor consoleEnabled = new AccessibleBooleanFieldEditor(P_SHOW_ASCIIDOC_CONSOLE_ON_ERROR_OUTPUT.getId(), "Show console when asciidoctor writes to standard error", devNull3);
		addField(consoleEnabled);	
		createDependency(useInstalledAsciidoctor.getChangeControl(devNull1),
				consoleEnabled.getChangeControl(devNull3),false);
		
	
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
