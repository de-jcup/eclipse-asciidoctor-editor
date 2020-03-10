package de.jcup.asciidoctoreditor.diagram.plantuml;

public enum PlantUMLOutputFormat {
    // see https://asciidoctor.org/docs/asciidoctor-diagram/
    SVG,
    
    PNG,
    
    TXT;

    public String getAsciiDocFormatString() {
        return name().toLowerCase();
    }

    /**
     * Will try to resolve format by string - if not possible default format will be returned
     * @param string
     * @return format, never <code>null</code>
     */
    public static PlantUMLOutputFormat fromString(String string) {
        if (string==null) {
            return getDefaultFormat();
        }
        for (PlantUMLOutputFormat format: values()) {
            if (string.equalsIgnoreCase(format.getAsciiDocFormatString())) {
                return format;
            }
        }
        return getDefaultFormat();
    }

    public static PlantUMLOutputFormat getDefaultFormat() {
        return SVG;
    }
}
