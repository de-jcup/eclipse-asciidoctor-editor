package de.jcup.asciidoctoreditor;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CustomEntrySupport {

    boolean areCustomEntriesEnabled();
    
    boolean areCustomEntriesEnabledPerDefault(); 

    void setCustomEntriesEnabled(boolean enabled);

    void setCustomEntries(List<KeyValue> definitionWorkingCopy);

    /**
     * Fetches custom environment entries
     * 
     * @return
     */
    Map<String, String> fetchConfiguredEntriesAsMap();
    
    
    Set<KeyValue> fetchConfiguredEntriesData();


}