/*
 * Copyright 2020 Albert Tregnaghi
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

public class SimplePlantUMLParser {

    private static final String INCLUDE = "!include ";
    private static final int INCLUDE_LENGTH = INCLUDE.length();

    private static final String INCLUDE_URL = "!includeurl ";
    private static final int INCLUDE_URL_LENGTH = INCLUDE_URL.length();

    public PlantUMLModel parse(String text) {
        PlantUMLModel model = new PlantUMLModel();
        String[] lines = text.split("\n");
        int lineNr = 1;
        int index = 0;
        for (String line : lines) {
            inspectLine(index, line, lineNr++, model);
            index = index + line.length() + 1; // add the new line was well..
        }
        return model;
    }

    private void inspectLine(int index, String line, int lineNr, PlantUMLModel model) {
        String location = null;
        if (line.startsWith(INCLUDE)) {
            location = line.substring(INCLUDE_LENGTH).trim();
        } else if (line.startsWith(INCLUDE_URL)) {
            location = line.substring(INCLUDE_URL_LENGTH).trim();
        }
        if (location != null) {
            addInclude(index, model, location, lineNr, line);
        }

    }

    private void addInclude(int pos, PlantUMLModel model, String location, int lineNr, String line) {
        PlantUMLInclude include = new PlantUMLInclude(location);
        include.setLineNumber(lineNr);
        include.setPosition(pos);
        include.setLine(line);
        model.getIncludes().add(include);
    }
}
