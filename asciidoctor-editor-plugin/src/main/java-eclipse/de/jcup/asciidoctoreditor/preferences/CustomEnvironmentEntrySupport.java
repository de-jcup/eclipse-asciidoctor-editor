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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;

import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;

public class CustomEnvironmentEntrySupport {

    public static CustomEnvironmentEntrySupport DEFAULT = new CustomEnvironmentEntrySupport();

    public final static String PREFERENCE_KEY_CUSTOM_ENV_ENTRIES_DATA = "custom.env.entries.data";;
    public final static String PREFERENCE_KEY_CUSTOM_ENV_ENTRIES_ENABLED = "custom.env.entries.enabled";

    private IPreferenceStore getPreferenceStore() {
        return AsciiDoctorEditorUtil.getPreferences().getPreferenceStore();
    }

    public Set<KeyValue> fetchConfiguredEnvironmentEntriesData() {
        String data = getPreferenceStore().getString(PREFERENCE_KEY_CUSTOM_ENV_ENTRIES_DATA);
        HashSet<KeyValue> freshCopy = new HashSet<>(KeyValueConverter.DEFAULT.convertStringToList(data));
        return freshCopy;
    }

    public boolean areCustomEnvironmentEntriesEnabled() {
        return getPreferenceStore().getBoolean(PREFERENCE_KEY_CUSTOM_ENV_ENTRIES_ENABLED);
    }

    public void setCustomEnvironmentEntriesEnabled(boolean enabled) {
        getPreferenceStore().setValue(PREFERENCE_KEY_CUSTOM_ENV_ENTRIES_ENABLED, enabled);
    }

    public void setCustomEnvironmentEntries(List<KeyValue> definitionWorkingCopy) {
        String convertListTostring = KeyValueConverter.DEFAULT.convertListTostring(definitionWorkingCopy);
        getPreferenceStore().setValue(PREFERENCE_KEY_CUSTOM_ENV_ENTRIES_DATA, convertListTostring);
    }

    /**
     * Fetches custom environment entries
     * 
     * @return
     */
    public Map<String, String> fetchConfiguredEnvironmentEntriesAsMap() {
        HashMap<String, String> map = new HashMap<>();
        for (KeyValue keyValue : fetchConfiguredEnvironmentEntriesData()) {
            map.put(keyValue.getKey(), keyValue.getValue());
        }
        return map;
    }

}
