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

public class PlantUMLIncludeTest {

    @Test
    public void location_with_http_localhost_is_not_local() {
        /* prepare + execute */
        PlantUMLInclude include = new PlantUMLInclude("http://localhost/xyz.puml");

        /* test */
        assertFalse(include.isLocal());
    }

    @Test
    public void location_with_https_localhost_is_not_local() {
        /* prepare + execute */
        PlantUMLInclude include = new PlantUMLInclude("https://localhost/xyz.puml");

        /* test */
        assertFalse(include.isLocal());
    }

    @Test
    public void location_with_C4_Content_pumlis_local() {
        /* prepare + execute */
        PlantUMLInclude include = new PlantUMLInclude("C4_Component.puml");

        /* test */
        assertTrue(include.isLocal());
    }

}
