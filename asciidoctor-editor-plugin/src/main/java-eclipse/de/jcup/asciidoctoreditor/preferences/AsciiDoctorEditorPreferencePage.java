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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
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
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocConfigFileSupport;
import de.jcup.asciidoctoreditor.presentation.AccessibleBooleanFieldEditor;
import de.jcup.asciidoctoreditor.presentation.AccessibleDirectoryFieldEditor;
import de.jcup.asciidoctoreditor.presentation.AccessibleFileFieldEditor;

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
    private IntegerFieldEditor aspServerMinPort;
    private IntegerFieldEditor aspServerMaxPort;
    private AccessibleBooleanFieldEditor aspLogRecordsShownAsMarkerInEditor;
    private AccessibleFileFieldEditor pathToJavaForASPlaunch;
    private AccessibleBooleanFieldEditor useInstalledAsciidoctor;
    private Composite baseComposite;
    private AccessibleBooleanFieldEditor aspServerOutputShownInConsole;
    private AccessibleBooleanFieldEditor aspCommunicationShownInConsole;

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
        int min = 0;
        int max = 0;
        setErrorMessage(null);
        setValid(true);
        try {
            min = aspServerMinPort.getIntValue();
            max = aspServerMaxPort.getIntValue();
            if (max <= min) {
                setErrorMessage("ASP min port must be smaller than max !");
                setValid(false);
                return false;
            } else {
                if (max - min > 50) {
                    setErrorMessage("ASP max-min diff must be between 1 and 50!");
                    setValid(false);
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            /* ignore done by field editors */
        }
        
        boolean ok = super.performOk();
        // we handle the directory field special, not added as field, so setting default
        // in this way
        AsciiDoctorEditorPreferences.getInstance().setStringPreference(AsciiDoctorEditorPreferenceConstants.P_PATH_TO_INSTALLED_ASCIICDOCTOR, pathToInstalledAsciidoctor.getStringValue());
        AsciiDoctorEditorPreferences.getInstance().setStringPreference(AsciiDoctorEditorPreferenceConstants.P_PATH_TO_JAVA_BINARY_FOR_ASP_LAUNCH, pathToJavaForASPlaunch.getStringValue());
        AsciiDoctorEditorActivator.getDefault().getAspSupport().configurationChanged();
        return ok;
    }

    protected void createDependency(Button master, Control slave) {
        createDependency(master, slave, true, false);
    }

    protected void createDependency(Button master, Control slave, boolean indent) {
        createDependency(master, slave, indent, false);
    }

    protected void createDependency(Button master, Control slave, boolean indent, boolean negative) {
        Assert.isNotNull(slave);
        if (indent) {
            indent(slave);
        }
        MasterButtonSlaveSelectionListener listener = new MasterButtonSlaveSelectionListener(master, slave, negative);
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
        createASPGroup(baseComposite);
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
        BooleanFieldEditor autoConfigFileCreationEnabled = new BooleanFieldEditor(P_AUTOCREATE_INITIAL_CONFIGFILE.getId(), "Enable initial config file auto creation", devNull);
        autoConfigFileCreationEnabled.getDescriptionControl(devNull)
                .setToolTipText("When enabled, a "+AsciiDocConfigFileSupport.FILENAME_ASCIIDOCTORCONFIG_ADOC+" with description inside \n"
                        + "will be created in project root folder when no other config file exists.");
        addField(autoConfigFileCreationEnabled);
        devNull = new Composite(uiComposite, SWT.NONE);
        
        BooleanFieldEditor linkEditorWithPreviewEnabled = new BooleanFieldEditor(P_LINK_EDITOR_WITH_PREVIEW.getId(), "Link editor with internal preview", devNull);
        linkEditorWithPreviewEnabled.getDescriptionControl(devNull)
                .setToolTipText("When enabled editor caret movements are scrolled in internal preview.\n" + "This works only in some situations e.g. when cursor moves to a headline");
        addField(linkEditorWithPreviewEnabled);
        
        BooleanFieldEditor groupOutlineEnabledPerDefault= new BooleanFieldEditor(P_OUTLINE_GROUPING_ENABLED_PER_DEFAULT.getId(), "Show outline grouped per default", devNull);
        groupOutlineEnabledPerDefault.getDescriptionControl(devNull)
        .setToolTipText("This changes default behaviour of editor outline: When enabled outline items are grouped on new opened editor outlines per default.\n\nWhen grouping is turned off the items in outline are ordered by their offset inside document.");
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
        // we handle the file field special, not added as field, so validating
        // value in this way
        if (pathToInstalledAsciidoctor !=null && !pathToInstalledAsciidoctor.checkState()) {
            setValid(false);
        }
        
        if (pathToJavaForASPlaunch !=null && !pathToJavaForASPlaunch.checkState()) {
            setValid(false);
        }
    }
    

    protected void createAsciidoctorGroup(Composite group) {

        useInstalledAsciidoctor = new AccessibleBooleanFieldEditor(P_USE_INSTALLED_ASCIIDOCTOR_ENABLED.getId(), "Use installed asciidoctor instead ASP", group);
        useInstalledAsciidoctor.getDescriptionControl(group)
                .setToolTipText("When enabled the installed asciidoctor will be used instead of ASP variant.\n\n" + "Be aware about adding correct setup for your CLI arguments in preferences!");
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
        aspServerMinPort = new IntegerFieldEditor(P_ASP_SERVER_MIN_PORT.getId(), "ASP Server port range: min", serverportComposite);
        aspServerMinPort.getLabelControl(serverportComposite)
                .setToolTipText("Set port range used by ASP server auto port detection - means a free port in given range is detected and used to start new server instance");
        aspServerMinPort.getTextControl(serverportComposite).setToolTipText("Set min port for ASP auto port detection");
        aspServerMinPort.setValidRange(1000, 65506);
        aspServerMinPort.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);

        aspServerMaxPort = new IntegerFieldEditor(P_ASP_SERVER_MAX_PORT.getId(), "max:", serverportComposite);
        aspServerMaxPort.getTextControl(serverportComposite).setToolTipText("Set max port for ASP auto port detection");
        aspServerMaxPort.setValidRange(1030, 65536);

        serverportComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        serverportComposite.setLayout(new GridLayout(5, false));

        Button button = new Button(serverportComposite, SWT.NONE);
        button.setText("Stop");
        button.setToolTipText("Will stop current running server instance - no \nmatter which port range is set inside this preferences!");
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                boolean stopped = AsciiDoctorEditorActivator.getDefault().getAspSupport().stop();
                if (stopped) {
                    MessageDialog.openInformation(getShell(), "ASP server shutdown", "Server has been stopped!");
                } else {
                    MessageDialog.openWarning(getShell(), "ASP server shutdown",
                            "Was not able to shutdown server instance.\nEither this server was not created by this eclipse instance or the process was already stopped");
                }
            }

        });
        addField(aspServerMinPort);
        addField(aspServerMaxPort);

        aspLogRecordsShownAsMarkerInEditor = new AccessibleBooleanFieldEditor(P_ASP_SERVER_LOGS_SHOWN_AS_MARKER_IN_EDITOR.getId(), "ASP log records shown as marker in editor", content);
        addField(aspLogRecordsShownAsMarkerInEditor);
        aspServerOutputShownInConsole = new AccessibleBooleanFieldEditor(P_ASP_SERVER_OUTPUT_SHOWN_IN_CONSOLE.getId(), "ASP server output shown in console", content);
        addField(aspServerOutputShownInConsole);
        aspCommunicationShownInConsole = new AccessibleBooleanFieldEditor(P_ASP_COMMUNICATION_SHOWN_IN_CONSOLE.getId(), "ASP communication shown in console", content);
        addField(aspCommunicationShownInConsole);

        Composite pathComposite = new Composite(content, SWT.NONE);
        pathToJavaForASPlaunch = new AccessibleFileFieldEditor(P_PATH_TO_JAVA_BINARY_FOR_ASP_LAUNCH.getId(), "Path to Java binary", pathComposite);
        pathToJavaForASPlaunch.getTextControl(pathComposite).setMessage("Use installed java");
        pathToJavaForASPlaunch.getTextControl(pathComposite)
                .setToolTipText("Full path to another java executable (java/java.exe) which will be called to launch ASP server.\n\nWhen empty, installed java version will be used.");
        pathToJavaForASPlaunch.setEmptyStringAllowed(true);
        pathToJavaForASPlaunch.setErrorMessage("Invalid path to java executable");

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
        pathComposite.setLayout(new GridLayout(3, false));
//      not:addField(pathToInstalledAsciidoctor); >>>> when not adding field as field editor it looks good. so text must be set to preferences by special code * field editors s...cks!
        pseudoAddField(pathToJavaForASPlaunch);

        Button changeControl = useInstalledAsciidoctor.getChangeControl(getBaseComposite());
        createDependency(changeControl, aspLogRecordsShownAsMarkerInEditor.getChangeControl(content), false, true);
        createDependency(changeControl, aspServerOutputShownInConsole.getChangeControl(content), false, true);
        createDependency(changeControl, aspCommunicationShownInConsole.getChangeControl(content), false, true);
        createDependency(changeControl, aspServerMinPort.getLabelControl(serverportComposite), false, true);
        createDependency(changeControl, aspServerMinPort.getTextControl(serverportComposite), false, true);
        createDependency(changeControl, pathToJavaForASPlaunch.getTextControl(pathComposite), false, true);
        createDependency(changeControl, pathToJavaForASPlaunch.getLabelControl(pathComposite), false, true);
        createDependency(changeControl, content, false, true);

    }

    protected void createInstalledAsciidoctorGroup(Composite composite) {

        Group installedAsciidoctorGroup = new Group(composite, SWT.NONE);
        installedAsciidoctorGroup.setText("Installed Asciidoctor");
        installedAsciidoctorGroup.setLayout(new GridLayout(1, false));
        installedAsciidoctorGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        Composite content = installedAsciidoctorGroup;

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
            this.negative = negative;
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
            } else {
                slave.setEnabled(state);
            }
        }

    }

}
