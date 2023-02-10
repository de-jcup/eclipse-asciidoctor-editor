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
import java.util.Objects;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
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
import de.jcup.asciidoctoreditor.presentation.AccessibleBooleanFieldEditor;
import de.jcup.asciidoctoreditor.presentation.AccessibleDirectoryFieldEditor;
import de.jcup.asciidoctoreditor.presentation.AccessibleFileFieldEditor;

/**
 * Parts are inspired b4444y <a href=
 * "https://github.com/eclipse/eclipse.jdt.ui/blob/parent/org.eclipse.jdt.ui/ui/org/eclipse/jdt/internal/ui/preferences/JavaEditorAppearanceConfigurationBlock.java">org.eclipse.jdt.internal.ui.preferences.JavaEditorAppearanceConfigurationBlock
 * </a>
 * 
 * @author Albert Tregnaghi
 *
 */
public class AsciiDoctorEditorRuntimePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    protected static final int INDENT = 20;

    protected static void indent(Control control) {
        if (control.getLayoutData() instanceof GridData) {
            ((GridData) control.getLayoutData()).horizontalIndent += INDENT;
        }
    }

    private ArrayList<ParentButtonChildSelectionListener> parentChildListeners = new ArrayList<>();
    private AccessibleDirectoryFieldEditor pathToInstalledAsciidoctor;
    private IntegerFieldEditor aspServerMinPort;
    private IntegerFieldEditor aspServerMaxPort;
    private AccessibleBooleanFieldEditor aspLogRecordsShownAsMarkerInEditor;
    private AccessibleFileFieldEditor pathToJavaForASPlaunch;
    private AccessibleBooleanFieldEditor useInstalledAsciidoctor;
    private Composite baseComposite;
    private AccessibleBooleanFieldEditor aspServerOutputShownInConsole;
    private AccessibleBooleanFieldEditor aspCommunicationShownInConsole;
    private Group aspGroupContent;
    private Composite aspServerportComposite;
    private Composite aspJavaPathComposite;

    public AsciiDoctorEditorRuntimePreferencePage() {
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
        parentChildListeners.forEach((a) -> a.updateChildComponent());

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
        AsciiDoctorEditorPreferences preferences = AsciiDoctorEditorPreferences.getInstance();
        
        boolean oldUseInstalledAsciidocEnabled = preferences.getBooleanPreference(P_USE_INSTALLED_ASCIIDOCTOR_ENABLED);
        boolean newUseInstalledAsciidocEnabled = useInstalledAsciidoctor.getBooleanValue();

        boolean ok = super.performOk();
        
        
        boolean changed = false;
        String newPathAsciidoc = pathToInstalledAsciidoctor.getStringValue();
        String oldPathAsciidoc = preferences.getStringPreference(AsciiDoctorEditorPreferenceConstants.P_PATH_TO_INSTALLED_ASCIICDOCTOR);
       
        String newPathJava = pathToJavaForASPlaunch.getStringValue();
        String oldPathJava =  preferences.getStringPreference(AsciiDoctorEditorPreferenceConstants.P_PATH_TO_JAVA_BINARY_FOR_ASP_LAUNCH);
        
    
        changed = changed || !Objects.equals(oldPathAsciidoc, newPathAsciidoc);
        changed = changed || !Objects.equals(oldPathJava, newPathJava);
        changed = changed || oldUseInstalledAsciidocEnabled!=newUseInstalledAsciidocEnabled;
        
        if (changed) {
            // we handle the directory field special, not added as field, so setting default
            // in this way
            preferences.setStringPreference(AsciiDoctorEditorPreferenceConstants.P_PATH_TO_INSTALLED_ASCIICDOCTOR, newPathAsciidoc);
            preferences.setStringPreference(AsciiDoctorEditorPreferenceConstants.P_PATH_TO_JAVA_BINARY_FOR_ASP_LAUNCH, newPathJava);
            AsciiDoctorEditorActivator.getDefault().getAspSupport().configurationChanged();
        }
        return ok;
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

        createASPGroup(baseComposite);
        createSpacer(baseComposite);
        createUseInstalledAsciidoctor(baseComposite);
        createInstalledAsciidoctorGroup(baseComposite);
        
        Button changeControl = useInstalledAsciidoctor.getChangeControl(getBaseComposite());
        createDependency(changeControl, aspLogRecordsShownAsMarkerInEditor.getChangeControl(aspGroupContent), false, true);
        createDependency(changeControl, aspServerOutputShownInConsole.getChangeControl(aspGroupContent), false, true);
        createDependency(changeControl, aspCommunicationShownInConsole.getChangeControl(aspGroupContent), false, true);
        createDependency(changeControl, aspServerMinPort.getLabelControl(aspServerportComposite), false, true);
        createDependency(changeControl, aspServerMinPort.getTextControl(aspServerportComposite), false, true);
        createDependency(changeControl, pathToJavaForASPlaunch.getTextControl(aspJavaPathComposite), false, true);
        createDependency(changeControl, pathToJavaForASPlaunch.getLabelControl(aspJavaPathComposite), false, true);
        createDependency(changeControl, aspGroupContent, false, true);
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

    @Override
    protected void checkState() {
        super.checkState();
        // we handle the file field special, not added as field, so validating
        // value in this way
        if (pathToInstalledAsciidoctor != null && !pathToInstalledAsciidoctor.checkState()) {
            setValid(false);
        }

        if (pathToJavaForASPlaunch != null && !pathToJavaForASPlaunch.checkState()) {
            setValid(false);
        }
    }

    protected void createUseInstalledAsciidoctor(Composite group) {

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
        aspGroupContent = aspGroup;
        /* ------------------ */
        /* ASP server setting */
        /* ------------------ */
        aspServerportComposite = new Composite(aspGroupContent, SWT.NONE);
        aspServerMinPort = new IntegerFieldEditor(P_ASP_SERVER_MIN_PORT.getId(), "ASP Server port range: min", aspServerportComposite);
        aspServerMinPort.getLabelControl(aspServerportComposite)
                .setToolTipText("Set port range used by ASP server auto port detection - means a free port in given range is detected and used to start new server instance");
        aspServerMinPort.getTextControl(aspServerportComposite).setToolTipText("Set min port for ASP auto port detection");
        aspServerMinPort.setValidRange(1000, 65506);
        aspServerMinPort.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);

        aspServerMaxPort = new IntegerFieldEditor(P_ASP_SERVER_MAX_PORT.getId(), "max:", aspServerportComposite);
        aspServerMaxPort.getTextControl(aspServerportComposite).setToolTipText("Set max port for ASP auto port detection");
        aspServerMaxPort.setValidRange(1030, 65536);

        aspServerportComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        aspServerportComposite.setLayout(new GridLayout(5, false));

        Button button = new Button(aspServerportComposite, SWT.NONE);
        button.setText("Stop");
        button.setToolTipText("Will stop current running server instance - no \nmatter which port range is set inside this preferences!");
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                boolean stopped = AsciiDoctorEditorActivator.getDefault().getAspSupport().stop(true);
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

        aspLogRecordsShownAsMarkerInEditor = new AccessibleBooleanFieldEditor(P_ASP_SERVER_LOGS_SHOWN_AS_MARKER_IN_EDITOR.getId(), "ASP log records shown as marker in editor", aspGroupContent);
        addField(aspLogRecordsShownAsMarkerInEditor);
        aspServerOutputShownInConsole = new AccessibleBooleanFieldEditor(P_ASP_SERVER_OUTPUT_SHOWN_IN_CONSOLE.getId(), "ASP server output shown in console", aspGroupContent);
        addField(aspServerOutputShownInConsole);
        aspCommunicationShownInConsole = new AccessibleBooleanFieldEditor(P_ASP_COMMUNICATION_SHOWN_IN_CONSOLE.getId(), "ASP communication shown in console", aspGroupContent);
        addField(aspCommunicationShownInConsole);

        aspJavaPathComposite = new Composite(aspGroupContent, SWT.NONE);
        pathToJavaForASPlaunch = new AccessibleFileFieldEditor(P_PATH_TO_JAVA_BINARY_FOR_ASP_LAUNCH.getId(), "Path to Java binary", aspJavaPathComposite);
        pathToJavaForASPlaunch.getTextControl(aspJavaPathComposite).setMessage("Use Eclipse JRE");
        pathToJavaForASPlaunch.getTextControl(aspJavaPathComposite)
                .setToolTipText("Full path to another java executable (java/java.exe) which will be called to launch ASP server.\n\nWhen empty, the JRE which does run Eclipse will be used.");
        pathToJavaForASPlaunch.setEmptyStringAllowed(true);
        pathToJavaForASPlaunch.setErrorMessage("Invalid path to java executable");

        pathToJavaForASPlaunch.getTextControl(aspJavaPathComposite).addFocusListener(new FocusListener() {

            @Override
            public void focusLost(FocusEvent e) {
                /* when focus lost we must check */
                checkState();
            }

            @Override
            public void focusGained(FocusEvent e) {

            }
        });

        aspJavaPathComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        aspJavaPathComposite.setLayout(new GridLayout(3, false));
//      not:addField(pathToInstalledAsciidoctor); >>>> when not adding field as field editor it looks good. so text must be set to preferences by special code * field editors s...cks!
        pseudoAddField(pathToJavaForASPlaunch);
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
        updateChildComponents();
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

    private void updateChildComponents() {
        for (ParentButtonChildSelectionListener listener : parentChildListeners) {
            listener.updateChildComponent();
        }
    }

}
