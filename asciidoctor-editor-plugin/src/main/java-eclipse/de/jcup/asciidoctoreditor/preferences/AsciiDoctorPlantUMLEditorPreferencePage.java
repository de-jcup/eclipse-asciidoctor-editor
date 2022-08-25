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

import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorPlantUMLEditorPreferenceConstants.*;
import static de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil.*;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.asciidoctoreditor.toolbar.ZoomLevel;
import de.jcup.eclipse.commons.ui.preferences.ChangeableComboFieldEditor;

public class AsciiDoctorPlantUMLEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public AsciiDoctorPlantUMLEditorPreferencePage() {
        super(GRID);
        setPreferenceStore(getPreferences().getPreferenceStore());
    }

    @Override
    public void init(IWorkbench workbench) {

    }

    @Override
    protected void createFieldEditors() {
        Composite parent = getFieldEditorParent();

        String labelText = "Default zoom level";
        String[] entries = ZoomLevel.DEFAULT_TEXT_ENTRIES;
        String[][] entryNamesAndValues = new String[entries.length][2];
        int index = 0;
        for (String entry : entries) {
            entryNamesAndValues[index++] = new String[] { entry, entry };
        }
        ChangeableComboFieldEditor comboEditor = new ChangeableComboFieldEditor(P_DEFAULT_ZOOM_LEVEL.getId(), labelText, entryNamesAndValues, parent);
        addField(comboEditor);

    }

}
