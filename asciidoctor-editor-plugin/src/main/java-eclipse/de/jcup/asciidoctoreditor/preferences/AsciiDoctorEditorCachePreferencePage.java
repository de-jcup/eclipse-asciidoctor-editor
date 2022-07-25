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

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;

public class AsciiDoctorEditorCachePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    
    private static final String TOOLTIP_KEEP_TEMP_FILES = "Internal temp files older than given days, will be automatically deleted on startup and close of eclipse";

    public AsciiDoctorEditorCachePreferencePage() {
        setPreferenceStore(AsciiDoctorEditorUtil.getPreferences().getPreferenceStore());
    }

    @Override
    public void init(IWorkbench workbench) {

    }

    @Override
    protected void createFieldEditors() {
        Composite parent = getFieldEditorParent();
        
        IntegerFieldEditor aspServerMinPort = new IntegerFieldEditor(P_DAYS_TO_KEEP_TEMPFILES.getId(), "Days to keep temp files:", parent);
        aspServerMinPort.getLabelControl(parent)
                .setToolTipText(TOOLTIP_KEEP_TEMP_FILES);
        aspServerMinPort.getTextControl(parent).setToolTipText(TOOLTIP_KEEP_TEMP_FILES);
        aspServerMinPort.setValidRange(0,30);
        aspServerMinPort.setValidateStrategy(StringFieldEditor.VALIDATE_ON_KEY_STROKE);
        
        addField(aspServerMinPort);

    }


}