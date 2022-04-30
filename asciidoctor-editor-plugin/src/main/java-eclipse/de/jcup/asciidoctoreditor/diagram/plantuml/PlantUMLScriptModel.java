package de.jcup.asciidoctoreditor.diagram.plantuml;

import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModel;


/**
 * We extend here the AsciiDoctorScriptModel but only to be able to reuse the outline support which does need this class.
 * The interesting part is the plantuml model, which is used by plant uml tree content provider. 
 * @author de-jcup
 *
 */
public class PlantUMLScriptModel extends AsciiDoctorScriptModel {

    private PlantUMLModel plantUMLModel;

    public void setPlantUML(PlantUMLModel model) {
        this.plantUMLModel = model;
    }

    public PlantUMLModel getPlantUMLModel() {
        return plantUMLModel;
    }

}
