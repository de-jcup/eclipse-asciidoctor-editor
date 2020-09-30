package de.jcup.asciidoctoreditor.diagram.plantuml;

public class SimplePlantUMLParser {

    private static final String INCLUDE = "!include ";
    private static final int INCLUDE_LENGTH = INCLUDE.length();
    
    private static final String INCLUDE_URL = "!includeurl ";
    private static final int INCLUDE_URL_LENGTH = INCLUDE_URL.length();

    public PlantUMLModel parse(String text) {
        PlantUMLModel model = new PlantUMLModel();
        String[] lines = text.split("\n");
        int lineNr = 1;
        for (String line: lines) {
            inspectLine(line, lineNr++,model);
        }
        return model;
    }

    private void inspectLine(String line, int lineNr, PlantUMLModel model) {

        if (line.startsWith(INCLUDE)){
            String includePart = line.substring(INCLUDE_LENGTH).trim();
            addInclude(model, includePart,lineNr);
        }else if (line.startsWith(INCLUDE_URL)) {
            String includePart = line.substring(INCLUDE_URL_LENGTH).trim();
            addInclude(model, includePart,lineNr);
        }
        
    }

    private void addInclude(PlantUMLModel model, String includePart, int lineNr) {
        PlantUMLInclude include = new PlantUMLInclude(includePart);
        include.setLineNumber(lineNr);
        model.getIncludes().add(include);
    }
}
