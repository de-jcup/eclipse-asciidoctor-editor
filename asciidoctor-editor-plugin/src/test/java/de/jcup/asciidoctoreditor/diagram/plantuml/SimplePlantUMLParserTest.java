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
