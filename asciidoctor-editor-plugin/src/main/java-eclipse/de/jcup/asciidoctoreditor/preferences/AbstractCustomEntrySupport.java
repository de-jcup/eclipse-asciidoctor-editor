package de.jcup.asciidoctoreditor.preferences;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;

import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;

public abstract class AbstractCustomEntrySupport implements CustomEntrySupport {

    public AbstractCustomEntrySupport() {
        super();
    }

    public Set<KeyValue> fetchConfiguredEntriesData() {
        String data = getPreferenceStore().getString(getCustomEntriesDataKey());
        Set<KeyValue> freshCopy = new HashSet<>(KeyValueConverter.DEFAULT.convertStringToList(data));
        return freshCopy;
    }

    protected abstract String getCustomEntriesDataKey();

    protected abstract String getCustomEntriesEnabledKey();

    @Override
    public final boolean areCustomEntriesEnabled() {
        return getPreferenceStore().getBoolean(getCustomEntriesEnabledKey());
    }

    @Override
    public final boolean areCustomEntriesEnabledPerDefault() {
        return getPreferenceStore().getDefaultBoolean(getCustomEntriesEnabledKey());
    }
    
    
    @Override
    public final void setCustomEntriesEnabled(boolean enabled) {
        getPreferenceStore().setValue(getCustomEntriesEnabledKey(), enabled);
    }

    @Override
    public final void setCustomEntries(List<KeyValue> definitionWorkingCopy) {
        String convertListTostring = KeyValueConverter.DEFAULT.convertListTostring(definitionWorkingCopy);
        getPreferenceStore().setValue(getCustomEntriesDataKey(), convertListTostring);
    }

    @Override
    public final Map<String, String> fetchConfiguredEntriesAsMap() {
        HashMap<String, String> map = new HashMap<>();
        for (KeyValue keyValue : fetchConfiguredEntriesData()) {
            map.put(keyValue.getKey(), keyValue.getValue());
        }
        return map;
    }
    
    private IPreferenceStore getPreferenceStore() {
        return AsciiDoctorEditorUtil.getPreferences().getPreferenceStore();
    }

}