package de.jcup.asciidoctoreditor.preferences;
/*
 * Copyright 2017 Albert Tregnaghi
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

/**
 * Constant definitions for plug-in preferences
 */
public enum AsciiDoctorPlantUMLEditorSyntaxColorPreferenceConstants implements PreferenceIdentifiable, PreferenceLabeled {
    COLOR_PLANTUML_KEYWORD("colorPlantUMLKeyword", "Keywords"), COLOR_PLANTUML_NORMAL_TEXT("colorPlantUMLNormalText", "Normal text"),
    COLOR_PLANTUML_PREPROCESSOR("colorPlantUMLPreprocessor", "Preprocessor"), COLOR_PLANTUML_DOUBLESTRING("colorPlantUMLDoubleString", "Strings"), COLOR_PLANTUML_COLOR("colorPlantUMLColor", "Colors"),
    COLOR_PLANTUML_NOTE("colorPlantUMLNote", "Notes"), COLOR_PLANTUML_COMMENT("colorPlantUMLComment", "Comments"), COLOR_PLANTUML_TYPE("colorPlantUMLType", "Types"),
    COLOR_PLANTUML_SKINPARAMETER("colorPlantUMLSkinparameter", "Skin parameters"), COLOR_PLANTUML_DIVIDER("colorPlantUMLDivider", "Dividers"), COLOR_PLANTUML_ARROW("colorPlantUMLArrow", "Arrows"),
    COLOR_PLANTUML_LABEL("colorPlantUMLLabel", "Labels"),

    ;

    private String id;
    private String labelText;

    private AsciiDoctorPlantUMLEditorSyntaxColorPreferenceConstants(String id, String labelText) {
        this.id = id;
        this.labelText = labelText;
    }

    public String getLabelText() {
        return labelText;
    }

    public String getId() {
        return id;
    }

}
