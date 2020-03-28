package de.jcup.asciidoctoreditor.provider;

import java.io.File;

class FileMatch{
    private File file;
    private String value;
    
    public FileMatch(File file, String value) {
        this.file=file;
        this.value=value;
    }

    public String getValue() {
        return value;
    }
    
    public File getFile() {
        return file;
    }
    
    
}