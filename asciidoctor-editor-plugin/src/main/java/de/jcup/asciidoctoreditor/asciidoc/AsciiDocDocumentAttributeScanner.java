package de.jcup.asciidoctoreditor.asciidoc;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class AsciiDocDocumentAttributeScanner {

    public Map<String, Object> scan(String doc) {
        if (doc==null) {
            return Collections.emptyMap();
        }
        if (doc.indexOf(':')==-1) {
            return Collections.emptyMap();
        }
        Map<String,Object> map = new TreeMap<String, Object>();
        String[] lines = doc.split("\n");
        for (String line: lines) {
            if (line==null) {
                continue;
            }
            int index = line.indexOf(':');
            if (index!=0) {
                continue;
            }
            int nextIndex = line.indexOf(':',1);
            if (nextIndex==-1) {
                continue;
            }
            String keyName = line.substring(1,nextIndex).trim();
            if (keyName.isEmpty()) {
                continue;
            }
            String value = line.substring(nextIndex+1).trim();
            map.put(keyName, value.trim());
        }
        
        
        return map;
    }

}
