package de.jcup.asciidoctoreditor.diagram.plantuml;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class PlantUMLFileEndingsTest {

    @Test
    public void wrong_file_endings_not_recognized_as_plantuml_file() {
        assertFalse(PlantUMLFileEndings.isPlantUmlFile(new File("test.txt")));
        assertFalse(PlantUMLFileEndings.isPlantUmlFile(new File("test.pdf")));
        assertFalse(PlantUMLFileEndings.isPlantUmlFile(new File("test.adoc")));
    }

    @Test
    public void null_file_not_recognized_as_plantuml_file() {
        assertFalse(PlantUMLFileEndings.isPlantUmlFile(null));
    }

    @Test
    public void correct_file_endings_are_recognized_as_plantuml_file() {
        assertTrue(PlantUMLFileEndings.isPlantUmlFile(new File("test.puml")));
        assertTrue(PlantUMLFileEndings.isPlantUmlFile(new File("test.PUML")));
        assertTrue(PlantUMLFileEndings.isPlantUmlFile(new File("test.plantuml")));
        assertTrue(PlantUMLFileEndings.isPlantUmlFile(new File("test.iuml")));
        assertTrue(PlantUMLFileEndings.isPlantUmlFile(new File("test.pu")));
    }

}
