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

import java.io.File;

public class PlantUMLFileEndings {

    public static final String DOT_IUML = ".iuml";
    public static final String DOT_PU = ".pu";
    public static final String DOT_PLANTUML = ".plantuml";
    public static final String DOT_PUML = ".puml";

    public static String[] asArray() {
        return new String[] { DOT_PUML, DOT_PLANTUML, DOT_PU, DOT_IUML };
    }

    public static boolean isPlantUmlFile(File file) {
        if (file == null) {
            return false;
        }
        String name = file.getName();
        if (name == null) {
            return false;
        }
        name = name.toLowerCase();
        for (String ending : asArray()) {
            if (name.endsWith(ending)) {
                return true;
            }
        }
        return false;
    }
}
