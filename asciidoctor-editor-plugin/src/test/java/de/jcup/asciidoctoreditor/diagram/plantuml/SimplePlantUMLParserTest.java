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

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SimplePlantUMLParserTest {
    
    private SimplePlantUMLParser parserToTest;

    @Before
    public void before() {
        parserToTest = new SimplePlantUMLParser();
    }
    
    @Test
    public void a_local_include_is_found_by_parser() {
        /* prepare */
        /* @formatter:off */
        String plantUml="@startuml context\n" + 
                "!include C4_Context.puml\n" + 
                "\n" + 
                "Person(administrator, \"Administrator\")\n" + 
                "System(jenkins, \"Jenkins\")\n" + 
                "Rel(administrator, jenkins, \"Administrates Jenkins\", \"SSH\")\n" + 
                "\n" + 
                "@enduml";
        /* @formatter:on */

        /* execute */
        PlantUMLModel model = parserToTest.parse(plantUml);
        

        /* test */
        List<PlantUMLInclude> includes = model.getIncludes();
        assertEquals(1,includes.size());
        PlantUMLInclude include = includes.get(0);
        assertEquals("C4_Context.puml", include.getLocation());
        assertEquals(2, include.getLineNumber());
    }

    @Test
    public void a_url_include_is_found_by_parser() {
        /* prepare */
        /* @formatter:off */
        String plantUml="@startuml context\n" + 
                "!includeurl https://raw.githubusercontent.com/RicardoNiepel/C4-PlantUML/release/1-0/C4_Container.puml\n" + 
                "\n" + 
                "Person(administrator, \"Administrator\")\n" + 
                "System(jenkins, \"Jenkins\")\n" + 
                "Rel(administrator, jenkins, \"Administrates Jenkins\", \"SSH\")\n" + 
                "\n" + 
                "@enduml";
        /* @formatter:on */

        /* execute */
        PlantUMLModel model = parserToTest.parse(plantUml);
        

        /* test */
        List<PlantUMLInclude> includes = model.getIncludes();
        assertEquals(1,includes.size());
        PlantUMLInclude include = includes.get(0);
        assertEquals("https://raw.githubusercontent.com/RicardoNiepel/C4-PlantUML/release/1-0/C4_Container.puml", include.getLocation());
        assertEquals(2, include.getLineNumber());
    }
    
}
