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
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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

import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;
import de.jcup.asciidoctoreditor.PreviewLayout;
import de.jcup.asciidoctoreditor.presentation.AccessibleBooleanFieldEditor;
import de.jcup.asciidoctoreditor.presentation.AccessibleDirectoryFieldEditor;

/**
 * Parts are inspired b4444y <a href=
 * "https://github.com/eclipse/eclipse.jdt.ui/blob/master/org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/preferences/JavaEditorAppearanceConfigurationBlock.java">org.eclipse.jdt.internal.ui.preferences.JavaEditorAppearanceConfigurationBlock
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

    private ArrayList<MasterButtonSlaveSelectionListener> masterSlaveListeners = new ArrayList<>();
    private AccessibleDirectoryFieldEditor pathToInstalledAsciidoctor;
    private IntegerFieldEditor aspServerPort;
    private AccessibleBooleanFieldEditor aspLogRecordsShownAsMarkerInEditor;
    private AccessibleDirectoryFieldEditor pathToJavaForASPlaunch;
    private AccessibleBooleanFieldEditor useInstalledAsciidoctor;
    private Composite baseComposite;
    private AccessibleBooleanFieldEditor aspServerOutputShownInConsole;

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
        pathToInstalledAsciidoctor.setStringValue("");
        masterSlaveListeners.forEach((a) -> a.updateSlaveComponent());

    }

    @Override
    public boolean performOk() {
        boolean ok = super.performOk();
        // we handle the directory field special, not added as field, so setting default
        // in this way
        AsciiDoctorEditorPreferences.getInstance().setStringPreference(AsciiDoctorEditorPreferenceConstants.P_PATH_TO_INSTALLED_ASCIICDOCTOR, pathToInstalledAsciidoctor.getStringValue());
        AsciiDoctorEditorPreferences.getInstance().setStringPreference(AsciiDoctorEditorPreferenceConstants.P_PATH_TO_JAVA_FOR_ASP_LAUNCH, pathToJavaForASPlaunch.getStringValue());
        AsciiDoctorEditorActivator.getDefault().updateASPServerStart();
        return ok;
    }

    protected void createDependency(Button master, Control slave) {
        createDependency(master, slave, true,false);
    }
    
    protected void createDependency(Button master, Control slave, boolean indent) {
        createDependency(master,slave,indent,false);
    }

    protected void createDependency(Button master, Control slave, boolean indent, boolean negative) {
        Assert.isNotNull(slave);
        if (indent) {
            indent(slave);
        }
        MasterButtonSlaveSelectionListener listener = new MasterButtonSlaveSelectionListener(master, slave,negative);
        master.addSelectionListener(listener);
        this.masterSlaveListeners.add(listener);
    }

    @Override
    protected void createFieldEditors() {
        baseComposite = createComposite();

        createUIGroup(baseComposite);
        createExternalPreviewParts(baseComposite);
        
        createSpacer(baseComposite);
        createAsciidoctorGroup(baseComposite);
        createSpacer(baseComposite);
//        
        createASPGroup(baseComposite);
//        createSpacer(baseComposite);
//        
        createInstalledAsciidoctorGroup(baseComposite);
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
        BooleanFieldEditor forceImageDirEnabled = new BooleanFieldEditor(P_USE_PREVIEW_IMAGEDIRECTORY.getId(), "Use image directory in preview folder", devNull);
        forceImageDirEnabled.getDescriptionControl(devNull)
                .setToolTipText("Enable this when you are using the ':imagesdir:' attribute. \n" + "This will ensure imagesdir content and also generated diagrams are available in temp folder.\n\n"
                        + "When you are using NOT attribute ':imagesdir:' but relative pathes you can turn off this option.\n" + "In this case the base dir will be set as image directory.");
        addField(forceImageDirEnabled);

        devNull = new Composite(uiComposite, SWT.NONE);
        BooleanFieldEditor linkEditorWithPreviewEnabled = new BooleanFieldEditor(P_LINK_EDITOR_WITH_PREVIEW.getId(), "Link editor with internal preview", devNull);
        linkEditorWithPreviewEnabled.getDescriptionControl(devNull)
                .setToolTipText("When enabled editor caret movements are scrolled in internal preview.\n" + "This works only in some situations e.g. when cursor moves to a headline");
        addField(linkEditorWithPreviewEnabled);
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
        IntegerFieldEditor autorefreshSeconds = new IntegerFieldEditor(P_EDITOR_AUTOREFRESH_EXTERNAL_BROWSER_IN_SECONDS.getId(), "Auto refresh in external preview (in seconds)", devNull2);
        autorefreshSeconds.setValidRange(0, 30);
        autorefreshSeconds.setTextLimit(2);
        autorefreshSeconds.getLabelControl(devNull2).setToolTipText("0 will turn off auto refresh for external previews.\n\nIf auto build has been disabled, this value will be ignored!");
        addField(autorefreshSeconds);

        createDependency(autobuildForExternalPreviewEnabled.getChangeControl(devNull1), autorefreshSeconds.getTextControl(devNull2));
    }

    @Override
    protected void checkState() {
        super.checkState();
        // we handle the directory field special, not added as field, so validating
        // value in this way
        if (pathToInstalledAsciidoctor !=null && !pathToInstalledAsciidoctor.checkState()) {
            setValid(false);
        }
        
        if (pathToJavaForASPlaunch !=null && !pathToJavaForASPlaunch.checkState()) {
            setValid(false);
        }
//        String path = pathToInstalledAsciidoctor.getStringValue();
//        if (path==null || path.isEmpty()) {
//            return;
//        }
//        File file = new File(path);
//        if (!file.exists()) {
//            setErrorMessage("Path to java invalid - executable does not exist");
//        }else {
//            String filename=file.getName();
//            if ("java".contentEquals(filename) || "java.exe".contentEquals(filename)) {
//                return;
//            }
//            setErrorMessage("Path to java invalid - not a java executable");
//        }
    }
    protected void createAsciidoctorGroup(Composite group) {

        useInstalledAsciidoctor = new AccessibleBooleanFieldEditor(P_USE_INSTALLED_ASCIIDOCTOR_ENABLED.getId(), "Use installed asciidoctor instead ASP", group);
        useInstalledAsciidoctor.getDescriptionControl(group).setToolTipText("When enabled the installed asciidoctor will be used instead of ASP variant.\n\n"
                + "Be aware about adding correct setup for your CLI arguments in preferences!");
        addField(useInstalledAsciidoctor);


    }
    
    protected void createASPGroup(Composite composite) {

        Group aspGroup = new Group(composite, SWT.NONE);
        aspGroup.setText("ASP - Asciidoctor server protocol");
        aspGroup.setLayout(new GridLayout(1, false));
        aspGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        Composite content = aspGroup;
        /* ------------------ */
        /* ASP server setting */
        /* ------------------ */
        Composite serverportComposite = new Composite(content, SWT.NONE);
        aspServerPort = new IntegerFieldEditor(P_ASP_SERVER_PORT.getId(), "ASP server port", serverportComposite);
        aspServerPort.getTextControl(serverportComposite).setToolTipText("Set port for ASP Server (Asciidoctor Server Protocol)");
        
        aspServerPort.setValidRange(4000, 65536);
        serverportComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        serverportComposite.setLayout(new GridLayout(2,false));
        addField(aspServerPort);
        
        aspLogRecordsShownAsMarkerInEditor = new AccessibleBooleanFieldEditor(P_ASP_SERVER_LOGS_SHOWN_AS_MARKER_IN_EDITOR.getId(), "ASP log records shown as marker in editor", content);
        addField(aspLogRecordsShownAsMarkerInEditor);
        
        aspServerOutputShownInConsole = new AccessibleBooleanFieldEditor(P_ASP_SERVER_OUTPUT_SHOWN_IN_CONSOLE.getId(), "Output of started ASP server is shown in console", content);
        addField(aspServerOutputShownInConsole);

        Composite pathComposite = new Composite(content, SWT.NONE);
        pathToJavaForASPlaunch = new AccessibleDirectoryFieldEditor(P_PATH_TO_JAVA_FOR_ASP_LAUNCH.getId(), "Path to Java", pathComposite);
        pathToJavaForASPlaunch.getTextControl(pathComposite).setMessage("Use installed java");
        pathToJavaForASPlaunch.getTextControl(pathComposite)
                .setToolTipText("Complete path to another java runtime. This is the execution which is called to launch ASP server. If empty, installed java will be used");
        pathToJavaForASPlaunch.setEmptyStringAllowed(true);
        pathToJavaForASPlaunch.setErrorMessage("Invalid path to java runtime");
        
        pathToJavaForASPlaunch.getTextControl(pathComposite).addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                /* when focus lost we must check */
                checkState();
            }

            @Override
            public void focusGained(FocusEvent e) {

            }
        });
        
        pathComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        pathComposite.setLayout(new GridLayout(3,false));
//      not:addField(pathToInstalledAsciidoctor); >>>> when not adding field as field editor it looks good. so text must be set to preferences by special code * field editors s...cks!
        pseudoAddField(pathToJavaForASPlaunch);
        
        Button changeControl = useInstalledAsciidoctor.getChangeControl(getBaseComposite());
        createDependency(changeControl, aspLogRecordsShownAsMarkerInEditor.getChangeControl(content), false,true);
        createDependency(changeControl, aspServerPort.getLabelControl(serverportComposite), false,true);
        createDependency(changeControl, aspServerPort.getTextControl(serverportComposite), false,true);
        createDependency(changeControl, pathToJavaForASPlaunch.getTextControl(pathComposite), false,true);
        createDependency(changeControl, pathToJavaForASPlaunch.getLabelControl(pathComposite), false,true);
        createDependency(changeControl, content, false,true);
        


    }


    protected void createInstalledAsciidoctorGroup(Composite composite) {

        Group installedAsciidoctorGroup = new Group(composite, SWT.NONE);
        installedAsciidoctorGroup.setText("Installed Asciidoctor");
        installedAsciidoctorGroup.setLayout(new GridLayout(1, false));
        installedAsciidoctorGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        Composite content=installedAsciidoctorGroup;

        Composite pathToInstalledComposite = new Composite(content, SWT.NONE);
        pathToInstalledAsciidoctor = new AccessibleDirectoryFieldEditor(P_PATH_TO_INSTALLED_ASCIICDOCTOR.getId(), "Path to Asciidoctor", pathToInstalledComposite);
        pathToInstalledAsciidoctor.getTextControl(pathToInstalledComposite).setMessage("Not defined");
        pathToInstalledAsciidoctor.getTextControl(pathToInstalledComposite)
                .setToolTipText("If not defined, installed asciidoctor instance must\nbe available from PATH in environment - otherwise it must be a valid directory.");
        pathToInstalledAsciidoctor.setEmptyStringAllowed(true);
        pathToInstalledAsciidoctor.setErrorMessage("Invalid path to installed Asciidoctor");
        pathToInstalledComposite.setLayout(new GridLayout(3, false));
        pathToInstalledComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

//		not:addField(pathToInstalledAsciidoctor); >>>> when not adding field as field editor it looks good. so text must be set to preferences by special code * field editors s...cks!
        pseudoAddField(pathToInstalledAsciidoctor);
        pathToInstalledAsciidoctor.getTextControl(pathToInstalledComposite).addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                /* when focus lost we must check */
                checkState();
            }

            @Override
            public void focusGained(FocusEvent e) {

            }
        });


        Composite devNull2 = new Composite(content, SWT.NONE);

        MultiLineStringFieldEditor cliArguments = new MultiLineStringFieldEditor(P_INSTALLED_ASCIICDOCTOR_ARGUMENTS.getId(), "Custom arguments for Asciidoctor CLI call", devNull2);
        cliArguments.getTextControl().setToolTipText("Setup arguments which shall be added to CLI call of installed asciidoctor instance.\n\nYou can use multiple lines.");
        GridData cliTextLayoutData = new GridData(SWT.FILL, SWT.TOP, true, false);
        cliArguments.getTextControl().setLayoutData(cliTextLayoutData);
        devNull2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        devNull2.setLayout(new GridLayout());
        addField(cliArguments);

        Composite devNull3 = new Composite(content, SWT.NONE);
        AccessibleBooleanFieldEditor consoleEnabled = new AccessibleBooleanFieldEditor(P_SHOW_ASCIIDOC_CONSOLE_ON_ERROR_OUTPUT.getId(), "Show console when asciidoctor writes to standard error",
                devNull3);
        addField(consoleEnabled);

        Button changeControl = useInstalledAsciidoctor.getChangeControl(getBaseComposite());
        createDependency(changeControl, pathToInstalledAsciidoctor.getTextControl(pathToInstalledComposite), false);
        createDependency(changeControl, pathToInstalledAsciidoctor.getLabelControl(pathToInstalledComposite), false);
        createDependency(changeControl, pathToInstalledAsciidoctor.getChangeControl(pathToInstalledComposite), false);
        createDependency(changeControl, cliArguments.getTextControl(devNull2), false);
        createDependency(changeControl, cliArguments.getLabelControl(devNull2), false);
        createDependency(changeControl, consoleEnabled.getChangeControl(devNull3), false);
        createDependency(changeControl, content, false);

    }

    private void pseudoAddField(FieldEditor pe) {
        pe.setPage(this);
        pe.setPropertyChangeListener(this);
        pe.setPreferenceStore(getPreferenceStore());
        pe.load();

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
        private boolean negative;
        
        public MasterButtonSlaveSelectionListener(Button master, Control slave, boolean negative) {
            this.master = master;
            this.slave = slave;
            this.negative=negative;
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
            if (negative) {
                slave.setEnabled(!state);
            }else {
                slave.setEnabled(state);
            }
        }

    }

}
