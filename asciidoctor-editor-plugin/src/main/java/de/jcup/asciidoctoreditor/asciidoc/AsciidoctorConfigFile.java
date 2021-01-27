package de.jcup.asciidoctoreditor.asciidoc;

import java.nio.file.Path;

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

}
