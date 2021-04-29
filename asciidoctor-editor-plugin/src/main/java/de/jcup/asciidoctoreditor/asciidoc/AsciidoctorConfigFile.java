package de.jcup.asciidoctoreditor.asciidoc;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class AsciidoctorConfigFile {

    private String content;
    private String asciidoctorconfigdir;
    private String absolutePath;
    private Path location;

    public AsciidoctorConfigFile(String content, Path location) {
        if (location == null) {
            throw new IllegalArgumentException("location amy not be null");
        }
        if (content == null) {
            throw new IllegalArgumentException("content amy not be null");
        }
        this.content = content;
        this.location= location;

        this.asciidoctorconfigdir = location.getParent().toAbsolutePath().toString();

    }
    
    public Path getLocation() {
        return location;
    }

    public String getAsciidoctorconfigdir() {
        return asciidoctorconfigdir;
    }

    public String getContent() {
        return content;
    }

    public String getContentCustomized() {
        return ":asciidoctorconfigdir: " + asciidoctorconfigdir + "\n" + content;
    }

    public Map<String, String> toContentCustomizedMap() {
        Map<String, String> map = new LinkedHashMap<>();
        String[] lines = getContentCustomized().split("\n");
        for (String line: lines) {
            if (!line.startsWith(":")) {
                continue;
            }
            String[] splitted = line.split(":");
            if (splitted.length<3) {
                continue;
            }
            String key=splitted[1];
            String value=splitted[2];
            map.put(key.trim(), value.trim());
        }
        
        return map;
    }

}
