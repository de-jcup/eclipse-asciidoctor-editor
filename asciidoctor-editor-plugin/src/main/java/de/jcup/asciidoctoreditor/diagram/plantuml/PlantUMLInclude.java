package de.jcup.asciidoctoreditor.diagram.plantuml;

import java.net.URI;

public class PlantUMLInclude implements PlantUMLElement {

    private String location;
    private boolean local;
    private int lineNumber;

    public PlantUMLInclude(String location) {
        this.location = location;
        try {
            URI uri = URI.create(location);
            if (uri.getHost() != null) {
                local = false;
            } else {
                local = true;
            }
        } catch (Exception e) {
            local = true;
        }
    }

    public String getLocation() {
        return location;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }

}
