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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.asciidoctoreditor.diagram.plantuml.PlantUMLOutputFormat;
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
		BooleanFieldEditor storeDiagramsInproject = new BooleanFieldEditor(P_PLANTUML_EDITOR_STORE_DIAGRAMS_IN_PROJECT.getId(), "Store diagram images in project", parent);
		storeDiagramsInproject.getDescriptionControl(parent).setToolTipText("When enabled generated diagrams will be stored in eclipse \nproject and parent folder is automatically refreshed.");
		addField(storeDiagramsInproject);
		
		String labelText = "Output format";
        PlantUMLOutputFormat[] allStyles = PlantUMLOutputFormat.values();
        String[][] entryNamesAndValues= new String[allStyles.length][2];
        int index=0;
        for (PlantUMLOutputFormat style: allStyles) {
            entryNamesAndValues[index++]=new String[] {
                    style.name(),style.getAsciiDocFormatString()
            };
        }
        ChangeableComboFieldEditor comboEditor = new ChangeableComboFieldEditor(P_PLANTUML_EDITOR_OUTPUT_FORMAT.getId(), labelText, entryNamesAndValues, parent);
        addField(comboEditor);

	}

}
