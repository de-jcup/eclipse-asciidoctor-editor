package de.jcup.asciidoctoreditor.diagram.plantuml;

import static org.junit.Assert.*;

import org.junit.Test;

public class PlantUMLOutputFormatTest {

    @Test
    public void fromString_works_as_expected() {
        assertEquals(PlantUMLOutputFormat.SVG,PlantUMLOutputFormat.fromString("SVG"));
        assertEquals(PlantUMLOutputFormat.SVG,PlantUMLOutputFormat.fromString("svg"));
        
        assertEquals(PlantUMLOutputFormat.SVG,PlantUMLOutputFormat.fromString(null));
        assertEquals(PlantUMLOutputFormat.SVG,PlantUMLOutputFormat.fromString(""));
        assertEquals(PlantUMLOutputFormat.SVG,PlantUMLOutputFormat.fromString("other"));

        assertEquals(PlantUMLOutputFormat.TXT,PlantUMLOutputFormat.fromString("txt"));
        assertEquals(PlantUMLOutputFormat.TXT,PlantUMLOutputFormat.fromString("TXT"));
        
        assertEquals(PlantUMLOutputFormat.PNG,PlantUMLOutputFormat.fromString("PNG"));
        assertEquals(PlantUMLOutputFormat.PNG,PlantUMLOutputFormat.fromString("png"));
    }
    
    @Test
    public void asciidocFormatString_as_expected() {
        assertEquals("svg", PlantUMLOutputFormat.SVG.getAsciiDocFormatString());
        assertEquals("png", PlantUMLOutputFormat.PNG.getAsciiDocFormatString());
        assertEquals("txt", PlantUMLOutputFormat.TXT.getAsciiDocFormatString());
    }
    

}
