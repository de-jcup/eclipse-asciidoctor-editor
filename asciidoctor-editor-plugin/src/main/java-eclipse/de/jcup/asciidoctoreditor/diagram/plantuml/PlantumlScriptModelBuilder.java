package de.jcup.asciidoctoreditor.diagram.plantuml;

import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModelBuilder;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModelException;

public class PlantumlScriptModelBuilder implements AsciiDoctorScriptModelBuilder {

    private SimplePlantUMLParser parser = new SimplePlantUMLParser();

    @Override
    public PlantUMLScriptModel build(String plantUML) throws AsciiDoctorScriptModelException {
        PlantUMLModel model = parser.parse(plantUML);
        PlantUMLScriptModel result = new PlantUMLScriptModel();
        result.setPlantUML(model);
        return result;
    }

}
