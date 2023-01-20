/*
 * Copyright 2023 Albert Tregnaghi
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