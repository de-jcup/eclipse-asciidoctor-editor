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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;

public class AsciiDoctorEditorCodeAssistencePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public AsciiDoctorEditorCodeAssistencePreferencePage() {
		setPreferenceStore(AsciiDoctorEditorUtil.getPreferences().getPreferenceStore());
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {
	    createCodeAssistencGroup(getFieldEditorParent());
	}
	
	protected void createCodeAssistencGroup(Composite parent) {

        BooleanFieldEditor codeAssistWithAsciiDoctorKeywords = new BooleanFieldEditor(P_CODE_ASSIST_ADD_KEYWORDS.getId(), "AsciiDoctor keywords", parent);
        codeAssistWithAsciiDoctorKeywords.getDescriptionControl(parent)
                .setToolTipText("When enabled the standard keywords supported by asciidoctor editor are always automatically available as code proposals");
        addField(codeAssistWithAsciiDoctorKeywords);

        
        BooleanFieldEditor codeAssistWithSimpleWords = new BooleanFieldEditor(P_CODE_ASSIST_ADD_SIMPLEWORDS.getId(), "Existing words", parent);
        codeAssistWithSimpleWords.getDescriptionControl(parent).setToolTipText("When enabled the current source will be scanned for words. The existing words will be available as code proposals");
        addField(codeAssistWithSimpleWords);
        
        BooleanFieldEditor toolTipsEnabled = new BooleanFieldEditor(P_TOOLTIPS_ENABLED.getId(), "Tooltips for keywords", parent);
        toolTipsEnabled.getDescriptionControl(parent).setToolTipText("When enabled tool tips will occure for keywords");
        addField(toolTipsEnabled);
        
        BooleanFieldEditor dynamicIncludeCodeAssist = new BooleanFieldEditor(P_CODE_ASSIST_DYNAMIC_FOR_INCLUDES.getId(), "Dynamic code assist for includes", parent);
        dynamicIncludeCodeAssist.getDescriptionControl(parent).setToolTipText("When enabled file pathes are dynamically suggested for includes");
        addField(dynamicIncludeCodeAssist);
        
        BooleanFieldEditor dynamicImageCodeAssist = new BooleanFieldEditor(P_CODE_ASSIST_DYNAMIC_FOR_IMAGES.getId(), "Dynamic code assist for images", parent);
        dynamicImageCodeAssist.getDescriptionControl(parent).setToolTipText("When enabled file pathes are dynamically suggested for images");
        addField(dynamicImageCodeAssist);
        
        BooleanFieldEditor dynamicPlantumlMacroCodeAssist = new BooleanFieldEditor(P_CODE_ASSIST_DYNAMIC_FOR_PLANTUML_MACRO.getId(), "Dynamic code assist for plantuml macros", parent);
        dynamicPlantumlMacroCodeAssist.getDescriptionControl(parent).setToolTipText("When enabled file pathes are dynamically suggested for plantuml macros");
        addField(dynamicPlantumlMacroCodeAssist);
        
        BooleanFieldEditor dynamicDitaaMacroCodeAssist = new BooleanFieldEditor(P_CODE_ASSIST_DYNAMIC_FOR_DITAA_MACRO.getId(), "Dynamic code assist for ditaa macros", parent);
        dynamicDitaaMacroCodeAssist.getDescriptionControl(parent).setToolTipText("When enabled file pathes are dynamically suggested for ditaa macros");
        addField(dynamicDitaaMacroCodeAssist);
        
        
    }
	
	

}