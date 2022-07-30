package de.jcup.asciidoctoreditor.model;

import java.io.File;

public class AsciidocCrossReference {

    File file;
    String id;
    int positionStartIndex;
    int length;

    public File getFile() {
        return file;
    }
    
    public String getId() {
        return id;
    }
    
    public int getPositionStart() {
        return positionStartIndex;
    }
    
    public int getLength() {
        return length;
    }
    @Override
    public String toString() {
        return file.getName()+" - "+id;
    }
}
