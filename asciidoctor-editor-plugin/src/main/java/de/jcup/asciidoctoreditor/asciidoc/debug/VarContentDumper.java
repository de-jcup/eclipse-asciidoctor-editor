package de.jcup.asciidoctoreditor.asciidoc.debug;

import java.util.Map;
import java.util.Objects;

public class VarContentDumper {

    private StringBuilder sb;

    public VarContentDumper() {
        sb = new StringBuilder();
    }
    
    public void add(String key, Map<String,Object> map) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("map:\n");
        
        for (String mapKey: map.keySet()) {
            sb.append("     ");
            sb.append(mapKey);
            sb.append("=");
            Object mapValue = map.get(mapKey);
            sb.append(mapValue);
            sb.append("\n");
        }
        
        add(key,sb.toString());
    }
    
    public void add(String key, Object obj) {
        add(key,Objects.toString(obj));
    }
    
    public void add(String key, String content) {
        sb.append(key).append("=").append(content).append("\n");
    }
    
    @Override
    public String toString() {
        return sb.toString();
    }

    public void addNewLine() {
        sb.append("\n");
    }
}
