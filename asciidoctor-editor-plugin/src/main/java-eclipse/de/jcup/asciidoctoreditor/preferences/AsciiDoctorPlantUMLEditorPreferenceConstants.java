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
public enum AsciiDoctorPlantUMLEditorPreferenceConstants implements PreferenceIdentifiable {

    P_PLANTUML_EDITOR_STORE_DIAGRAMS_IN_PROJECT("pumlStoreDiagramsInProject"),

    @Deprecated // shall be removed
    P_PLANTUML_EDITOR_OUTPUT_FORMAT("pumlOutputFormat"),;

    private String id;

    private AsciiDoctorPlantUMLEditorPreferenceConstants(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
