/*
 * Copyright 2021 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.preferences;

import de.jcup.asciidoctoreditor.ASPSupport;
import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;

public class AsciiDoctorEditorEnvironmentPreferencePage extends AbstractAsciiDoctorEditorCustomEntriesPreferencePage {

    public AsciiDoctorEditorEnvironmentPreferencePage() {
        setDescription("Setup custom asciidoc environment entries.\n\nChanges will restart an active ASP server to provide the new environment values.");
        setTitle("Asciidoc Environment"); // for programmatic part only (normal preferences dialog uses data from plugin.xml)
        
        setPropertyTableColumnHeaders(new String[] {"Environment key", "Value"});
    }

    @Override
    public boolean performOk() {

        CustomEntrySupport customEnvironmentEntrySupport = getSupport();
        
        boolean enabledStateAsBefore = customEntriesEnabled == customEnvironmentEntrySupport.areCustomEntriesEnabled();
        boolean sameContentAsBefore = checkSameDefinitionsAsBefore();
        if (!sameContentAsBefore || !enabledStateAsBefore) {

            customEnvironmentEntrySupport.setCustomEntriesEnabled(customEntriesEnabled);
            customEnvironmentEntrySupport.setCustomEntries(definitionWorkingCopy);

            if (!AsciiDoctorEditorPreferences.getInstance().isUsingInstalledAsciidoctor()) {

                ASPSupport aspSupport = AsciiDoctorEditorActivator.getDefault().getAspSupport();

                aspSupport.configurationChanged();
            }
        }
        return super.performOk();
    }

    protected CustomEnvironmentEntrySupport getSupport() {
        return CustomEnvironmentEntrySupport.DEFAULT;
    }
    
    protected String getAddDialogTitle() {
        return "Add new environment variable";
    }

    protected String getOverwriteDialogTitle() {
        return "Overwrite environment definition?";
    }
    
    protected String getChangeEntryDialogTitle() {
        return "Change environment entry";
    }


}