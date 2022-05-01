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

import static org.junit.Assert.*;

import org.junit.Test;

public class PlantUMLOutputFormatTest {

    @Test
    public void fromString_works_as_expected() {
        assertEquals(PlantUMLOutputFormat.SVG, PlantUMLOutputFormat.fromString("SVG"));
        assertEquals(PlantUMLOutputFormat.SVG, PlantUMLOutputFormat.fromString("svg"));

        assertEquals(PlantUMLOutputFormat.SVG, PlantUMLOutputFormat.fromString(null));
        assertEquals(PlantUMLOutputFormat.SVG, PlantUMLOutputFormat.fromString(""));
        assertEquals(PlantUMLOutputFormat.SVG, PlantUMLOutputFormat.fromString("other"));

        assertEquals(PlantUMLOutputFormat.TXT, PlantUMLOutputFormat.fromString("txt"));
        assertEquals(PlantUMLOutputFormat.TXT, PlantUMLOutputFormat.fromString("TXT"));

        assertEquals(PlantUMLOutputFormat.PNG, PlantUMLOutputFormat.fromString("PNG"));
        assertEquals(PlantUMLOutputFormat.PNG, PlantUMLOutputFormat.fromString("png"));
    }

    @Test
    public void asciidocFormatString_as_expected() {
        assertEquals("svg", PlantUMLOutputFormat.SVG.getAsciiDocFormatString());
        assertEquals("png", PlantUMLOutputFormat.PNG.getAsciiDocFormatString());
        assertEquals("txt", PlantUMLOutputFormat.TXT.getAsciiDocFormatString());
    }

}
