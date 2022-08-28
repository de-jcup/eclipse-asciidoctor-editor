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
public enum AsciiDoctorEditorValidationPreferenceConstants implements PreferenceIdentifiable, PreferenceLabeled {

    VALIDATE_ERROR_LEVEL("validationErrorLevel", "Validation error level"),

    VALIDATE_INCLUDES("validateIncludesEnabled", "Validate includes"),

    VALIDATE_IMAGES("validateImagesEnabled", "Validate images"),

    VALIDATE_DIAGRAMS("validateDiagramsEnabled", "Validate diagrams"),

    VALIDATE_URLS("validateURLsEnabled", "Validate URLs"),

    ;

    private String id;
    private String labelText;

    private AsciiDoctorEditorValidationPreferenceConstants(String id, String labelText) {
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
