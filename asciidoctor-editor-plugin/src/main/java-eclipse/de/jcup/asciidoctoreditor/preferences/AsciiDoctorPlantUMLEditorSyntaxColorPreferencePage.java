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

import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorPlantUMLEditorSyntaxColorPreferenceConstants.*;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.asciidoctoreditor.ui.AsciiDoctorEditorColorConstants;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;

public class AsciiDoctorPlantUMLEditorSyntaxColorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public AsciiDoctorPlantUMLEditorSyntaxColorPreferencePage() {
        setPreferenceStore(AsciiDoctorEditorUtil.getPreferences().getPreferenceStore());
    }

    @Override
    public void init(IWorkbench workbench) {

    }

    @Override
    protected void createFieldEditors() {
        Composite parent = getFieldEditorParent();
        Map<AsciiDoctorPlantUMLEditorSyntaxColorPreferenceConstants, ColorFieldEditor> editorMap = new HashMap<AsciiDoctorPlantUMLEditorSyntaxColorPreferenceConstants, ColorFieldEditor>();
        for (AsciiDoctorPlantUMLEditorSyntaxColorPreferenceConstants colorIdentifier : AsciiDoctorPlantUMLEditorSyntaxColorPreferenceConstants.values()) {
            ColorFieldEditor editor = new ColorFieldEditor(colorIdentifier.getId(), colorIdentifier.getLabelText(), parent);
            editorMap.put(colorIdentifier, editor);
            addField(editor);
        }
        addDarkThemeDefaultsButton(parent, editorMap);

    }

    private void addDarkThemeDefaultsButton(Composite parent, Map<AsciiDoctorPlantUMLEditorSyntaxColorPreferenceConstants, ColorFieldEditor> editorMap) {
        Button restoreDarkThemeColorsButton = new Button(parent, SWT.PUSH);
        restoreDarkThemeColorsButton.setText("Restore Defaults for Dark Theme");
        restoreDarkThemeColorsButton.setToolTipText("Same as 'Restore Defaults' but for dark themes.\n Editor makes just a suggestion, you still have to apply or cancel the settings.");
        restoreDarkThemeColorsButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                /* editor colors */
                changeColor(editorMap, COLOR_PLANTUML_NORMAL_TEXT, AsciiDoctorEditorColorConstants.GRAY_JAVA);
                changeColor(editorMap, COLOR_PLANTUML_KEYWORD, AsciiDoctorEditorColorConstants.DARK_THEME_LIHT_RED);

                changeColor(editorMap, COLOR_PLANTUML_NOTE, AsciiDoctorEditorColorConstants.DARK_THEME_BRIGHT_CYAN);
                changeColor(editorMap, COLOR_PLANTUML_COMMENT, AsciiDoctorEditorColorConstants.GREEN_JAVA);
                changeColor(editorMap, COLOR_PLANTUML_TYPE, AsciiDoctorEditorColorConstants.TASK_CYAN);
                changeColor(editorMap, COLOR_PLANTUML_SKINPARAMETER, AsciiDoctorEditorColorConstants.DARK_THEME_GRAY);
                changeColor(editorMap, COLOR_PLANTUML_COLOR, AsciiDoctorEditorColorConstants.DARK_THEME_MEDIUM_BLUE);
                changeColor(editorMap, COLOR_PLANTUML_PREPROCESSOR, AsciiDoctorEditorColorConstants.DARK_THEME_CYAN);
                changeColor(editorMap, COLOR_PLANTUML_DOUBLESTRING, AsciiDoctorEditorColorConstants.ORANGE);
                changeColor(editorMap, COLOR_PLANTUML_DIVIDER, AsciiDoctorEditorColorConstants.DARK_THEME_LIGHT_GREEN);
                changeColor(editorMap, COLOR_PLANTUML_ARROW, AsciiDoctorEditorColorConstants.DARK_THEME_PINK);
                changeColor(editorMap, COLOR_PLANTUML_LABEL, AsciiDoctorEditorColorConstants.DARK_THEME_LIGHT_BLUE);

            }

            private void changeColor(Map<AsciiDoctorPlantUMLEditorSyntaxColorPreferenceConstants, ColorFieldEditor> editorMap, AsciiDoctorPlantUMLEditorSyntaxColorPreferenceConstants colorId,
                    RGB rgb) {
                editorMap.get(colorId).getColorSelector().setColorValue(rgb);
            }

        });
    }

}