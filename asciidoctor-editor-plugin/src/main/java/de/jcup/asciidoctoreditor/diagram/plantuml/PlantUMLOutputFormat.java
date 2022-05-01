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

public enum PlantUMLOutputFormat {
    // see https://asciidoctor.org/docs/asciidoctor-diagram/
    SVG,

    PNG,

    TXT;

    public String getAsciiDocFormatString() {
        return name().toLowerCase();
    }

    /**
     * Will try to resolve format by string - if not possible default format will be
     * returned
     * 
     * @param string
     * @return format, never <code>null</code>
     */
    public static PlantUMLOutputFormat fromString(String string) {
        if (string == null) {
            return getDefaultFormat();
        }
        for (PlantUMLOutputFormat format : values()) {
            if (string.equalsIgnoreCase(format.getAsciiDocFormatString())) {
                return format;
            }
        }
        return getDefaultFormat();
    }

    public static PlantUMLOutputFormat getDefaultFormat() {
        return SVG;
    }
}
