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

public class CustomAttributesEntrySupport extends AbstractCustomEntrySupport{

    public static final CustomAttributesEntrySupport DEFAULT = new CustomAttributesEntrySupport();
    
    protected String getCustomEntriesDataKey() {
        return AsciiDoctorEditorPreferenceConstants.P_CUSTOM_ATTRIBUTES_DATA.getId();
    }

    protected String getCustomEntriesEnabledKey() {
        return AsciiDoctorEditorPreferenceConstants.P_CUSTOM_ATTRIBUTES_ENABLED.getId();
    }



}
