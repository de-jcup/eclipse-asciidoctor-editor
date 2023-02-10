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

public class AsciiDoctorEditorAttributesPreferencePage extends AbstractAsciiDoctorEditorCustomEntriesPreferencePage {

    private boolean hasChanged;

    public AsciiDoctorEditorAttributesPreferencePage() {
        setDescription("Setup custom asciidoc attribute entries used for rendering.\n\nExisting attribute definitions from origin documents will be overriden!");
        setTitle("Asciidoc attributes"); // for programmatic part only (normal preferences dialog uses data from plugin.xml)
        
        setPropertyTableColumnHeaders(new String[] {"Attribute", "Value"});
    }

    @Override
    public boolean performOk() {

        CustomAttributesEntrySupport customAttributesEntrySupport = getSupport();
        
        boolean enabledStateAsBefore = customEntriesEnabled == customAttributesEntrySupport.areCustomEntriesEnabled();
        boolean sameContentAsBefore = checkSameDefinitionsAsBefore();
        if (!sameContentAsBefore || !enabledStateAsBefore) {

            customAttributesEntrySupport.setCustomEntriesEnabled(customEntriesEnabled);
            customAttributesEntrySupport.setCustomEntries(definitionWorkingCopy);
            
            hasChanged=true;
        }
        return super.performOk();
    }
    
    public boolean hasChanged() {
        return hasChanged;
    }

    protected CustomAttributesEntrySupport getSupport() {
        return CustomAttributesEntrySupport.DEFAULT;
    }
    
    protected String getAddDialogTitle() {
        return "Add new attribute";
    }

    protected String getOverwriteDialogTitle() {
        return "Overwrite attribute definition?";
    }
    
    protected String getChangeEntryDialogTitle() {
        return "Change attribute entry";
    }

}