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
