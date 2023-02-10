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

import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferenceConstants.*;
import static de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil.*;

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
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
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocConfigFileSupport;
import de.jcup.asciidoctoreditor.presentation.AccessibleBooleanFieldEditor;

/**
 * Parts are inspired b4444y <a href=
 * "https://github.com/eclipse/eclipse.jdt.ui/blob/parent/org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/preferences/JavaEditorAppearanceConfigurationBlock.java">org.eclipse.jdt.internal.ui.preferences.JavaEditorAppearanceConfigurationBlock
 * </a>
 * 
 * @author Albert Tregnaghi
 *
 */
public class AsciiDoctorEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    protected static final int INDENT = 20;

    protected static void indent(Control control) {
        if (control.getLayoutData() instanceof GridData) {
            ((GridData) control.getLayoutData()).horizontalIndent += INDENT;
        }
    }

    private ArrayList<ParentButtonChildSelectionListener> parentChildListeners = new ArrayList<>();
    private Composite baseComposite;

    public AsciiDoctorEditorPreferencePage() {
        super(GRID);
        setPreferenceStore(getPreferences().getPreferenceStore());
    }

    @Override
    protected Control createContents(Composite parent) {
        Control control = super.createContents(parent);
        return control;
    }

    @Override
    public void init(IWorkbench workbench) {

    }

    @Override
    public void performDefaults() {
        super.performDefaults();
        parentChildListeners.forEach((a) -> a.updateChildComponent());
    }

    protected void createDependency(Button parent, Control child) {
        createDependency(parent, child, true, false);
    }

    protected void createDependency(Button parent, Control child, boolean indent) {
        createDependency(parent, child, indent, false);
    }

    protected void createDependency(Button parent, Control child, boolean indent, boolean negative) {
        Assert.isNotNull(child);
        if (indent) {
            indent(child);
        }
        ParentButtonChildSelectionListener listener = new ParentButtonChildSelectionListener(parent, child, negative);
        parent.addSelectionListener(listener);
        this.parentChildListeners.add(listener);
    }

    @Override
    protected void createFieldEditors() {
        baseComposite = createComposite();

        createUIGroup(baseComposite);
        createExternalPreviewParts(baseComposite);
    }

    public Composite getBaseComposite() {
        return baseComposite;
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

    protected void createUIGroup(Composite composite) {
        Composite uiComposite = new Composite(composite, SWT.NONE);
        GridLayout uiLayout = new GridLayout();
        uiLayout.marginWidth = 0;
        uiLayout.marginHeight = 0;
        uiComposite.setLayout(uiLayout);

        uiComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        Composite devNull = new Composite(uiComposite, SWT.NONE);
        String[][] entryNamesAndValues = new String[][] { new String[] { "Vertical", PreviewLayout.VERTICAL.getId() }, new String[] { "Horizontal", PreviewLayout.HORIZONTAL.getId() },
                new String[] { "Hide preview, use external browser", PreviewLayout.EXTERNAL_BROWSER.getId() } };
        /* @formatter:on */
        ComboFieldEditor previewDefaultTypeRadioButton = new ComboFieldEditor(P_EDITOR_NEWEDITOR_PREVIEW_LAYOUT.getId(), "Default preview layout", entryNamesAndValues, devNull);

        addField(previewDefaultTypeRadioButton);

        devNull = new Composite(uiComposite, SWT.NONE);
        IntegerFieldEditor tocLevels = new IntegerFieldEditor(P_EDITOR_TOC_LEVELS.getId(), "TOC levels shown in preview", devNull);
        tocLevels.setValidRange(0, 7);
        tocLevels.setTextLimit(1);
        tocLevels.getLabelControl(devNull).setToolTipText("0 keeps defaults from asciidoctor, other will set the wanted depth for TOC on preview only!");

        addField(tocLevels);
        
        devNull = new Composite(uiComposite, SWT.NONE);
        BooleanFieldEditor tocVisibleOnNewEditors = new BooleanFieldEditor(P_TOC_VISIBLE_ON_NEW_EDITORS_PER_DEFAULT .getId(), "TOC visible per default", devNull);
        tocVisibleOnNewEditors.getDescriptionControl(devNull).setToolTipText("When enabled the TOC (table of content) is automatically visible per default on new editor instances");
        addField(tocVisibleOnNewEditors);

        devNull = new Composite(uiComposite, SWT.NONE);
        BooleanFieldEditor autoConfigFileCreationEnabled = new BooleanFieldEditor(P_AUTOCREATE_INITIAL_CONFIGFILE.getId(), "Enable initial config file auto creation", devNull);
        autoConfigFileCreationEnabled.getDescriptionControl(devNull).setToolTipText("When enabled, a " + AsciiDocConfigFileSupport.FILENAME_ASCIIDOCTORCONFIG_ADOC + " with description inside \n"
                + "will be created in project root folder when no other config file exists.");
        addField(autoConfigFileCreationEnabled);
        devNull = new Composite(uiComposite, SWT.NONE);

        BooleanFieldEditor linkEditorWithPreviewEnabled = new BooleanFieldEditor(P_LINK_EDITOR_WITH_PREVIEW.getId(), "Link editor with internal preview", devNull);
        linkEditorWithPreviewEnabled.getDescriptionControl(devNull)
                .setToolTipText("When enabled editor caret movements are scrolled in internal preview.\n" + "This works only in some situations e.g. when cursor moves to a headline");
        addField(linkEditorWithPreviewEnabled);

        BooleanFieldEditor groupOutlineEnabledPerDefault = new BooleanFieldEditor(P_OUTLINE_GROUPING_ENABLED_PER_DEFAULT.getId(), "Show outline grouped per default", devNull);
        groupOutlineEnabledPerDefault.getDescriptionControl(devNull).setToolTipText(
                "This changes default behaviour of editor outline: When enabled outline items are grouped on new opened editor outlines per default.\n\nWhen grouping is turned off the items in outline are ordered by their offset inside document.");
        addField(groupOutlineEnabledPerDefault);
    }

    protected void createExternalPreviewParts(Composite composite) {
        Group externalPreviewGroup = new Group(composite, SWT.NONE);
        externalPreviewGroup.setText("External preview");
        externalPreviewGroup.setLayout(new GridLayout());
        externalPreviewGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        Composite devNull1 = new Composite(externalPreviewGroup, SWT.NONE);
        AccessibleBooleanFieldEditor autobuildForExternalPreviewEnabled = new AccessibleBooleanFieldEditor(P_EDITOR_AUTOBUILD_FOR_EXTERNAL_PREVIEW_ENABLED.getId(), "Auto build for external preview",
                devNull1);
        autobuildForExternalPreviewEnabled.getDescriptionControl(devNull1)
                .setToolTipText("When enabled the asciidoctor integration will be called on every change in document. As done in internal previews.\n\n"
                        + "If disabled only a click to 'refresh' or 'show in external browser' buttons inside the toolbar will rebuild the document.\n\n");
        addField(autobuildForExternalPreviewEnabled);

        Composite devNull2 = new Composite(externalPreviewGroup, SWT.NONE);
        IntegerFieldEditor autorefreshSeconds = new IntegerFieldEditor(P_EDITOR_AUTOBUILD_FOR_EXTERNAL_PREVIEW_REFRESH_IN_SECONDS.getId(), "Auto refresh in external preview (in seconds)", devNull2);
        autorefreshSeconds.setValidRange(0, 30);
        autorefreshSeconds.setTextLimit(2);
        autorefreshSeconds.getLabelControl(devNull2).setToolTipText("0 will turn off auto refresh for external previews.\n\nIf auto build has been disabled, this value will be ignored!");
        addField(autorefreshSeconds);

        createDependency(autobuildForExternalPreviewEnabled.getChangeControl(devNull1), autorefreshSeconds.getTextControl(devNull2));
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
        for (ParentButtonChildSelectionListener listener : parentChildListeners) {
            listener.updateChildComponent();
        }
    }

}
