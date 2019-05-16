package de.jcup.asciidoctoreditor;

public enum AsciiDoctorBackendType{
    HTML5("html5"),
    PDF("pdf");
    
    private String backendString;

    private AsciiDoctorBackendType(String backendString) {
        this.backendString=backendString;
    }
    
    public String getBackendString() {
        return backendString;
    }
}