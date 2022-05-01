/*
 * Copyright 2019 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.asciidoc;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class AsciiDocDocumentAttributeScanner {

    public Map<String, Object> scan(String doc) {
        if (doc == null) {
            return Collections.emptyMap();
        }
        if (doc.indexOf(':') == -1) {
            return Collections.emptyMap();
        }
        Map<String, Object> map = new TreeMap<String, Object>();
        String[] lines = doc.split("\n");
        for (String line : lines) {
            if (line == null) {
                continue;
            }
            int index = line.indexOf(':');
            if (index != 0) {
                continue;
            }
            int nextIndex = line.indexOf(':', 1);
            if (nextIndex == -1) {
                continue;
            }
            String keyName = line.substring(1, nextIndex).trim();
            if (keyName.isEmpty()) {
                continue;
            }
            String value = line.substring(nextIndex + 1).trim();
            map.put(keyName, value.trim());
        }

        return map;
    }

}
