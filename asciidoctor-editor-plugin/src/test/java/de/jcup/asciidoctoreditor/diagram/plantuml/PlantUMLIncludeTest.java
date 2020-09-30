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
