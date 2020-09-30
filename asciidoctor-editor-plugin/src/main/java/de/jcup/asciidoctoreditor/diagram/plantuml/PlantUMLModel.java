package de.jcup.asciidoctoreditor.diagram.plantuml;

import java.util.ArrayList;
import java.util.List;

public class PlantUMLModel {

    private List<PlantUMLInclude> includes = new ArrayList<>();

    public List<PlantUMLInclude> getIncludes(){
        return includes;
    }
}
