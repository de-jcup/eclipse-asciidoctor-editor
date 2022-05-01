/*
 * Copyright 2021 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.asciidoctoreditor.diagram.plantuml;

import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModel;

/**
 * We extend here the AsciiDoctorScriptModel but only to be able to reuse the
 * outline support which does need this class. The interesting part is the
 * plantuml model, which is used by plant uml tree content provider.
 * 
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
