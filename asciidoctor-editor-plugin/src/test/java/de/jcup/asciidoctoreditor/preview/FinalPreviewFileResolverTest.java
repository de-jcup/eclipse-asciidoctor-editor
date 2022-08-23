package de.jcup.asciidoctoreditor.preview;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Objects;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.jcup.asciidoctoreditor.EditorType;

public class FinalPreviewFileResolverTest {

    private static File plantUMLHTMLpreview1;
    private FinalPreviewFileResolver resolverToTest;

    @BeforeClass
    public static void beforeAll() {
        /* prepare */
        plantUMLHTMLpreview1 = new File("./src/test/resources/final-preview/plantuml-internal-editor-preview-1.html");
        
        /* check precondition */
        assertTrue(plantUMLHTMLpreview1.exists());
    }
    
    
    @Before
    public void before() {
        resolverToTest = new FinalPreviewFileResolver();
    }
    
    @Test
    public void editor_type_asciidoc_returns_html5_preview_file() {
        
        /* execute */
        File result = resolverToTest.resolvePreviewFileFromGeneratedHTMLFile(plantUMLHTMLpreview1, EditorType.ASCIIDOC);
        
        /* test */
        File expected = plantUMLHTMLpreview1;
        assertSameFile(expected, result);
    }
    
    @Test
    public void editor_type_plantuml_returns_file_by_name_of_html5_preview_file() {
        /* execute */
        File result = resolverToTest.resolvePreviewFileFromGeneratedHTMLFile(plantUMLHTMLpreview1, EditorType.PLANTUML);
        
        /* test */
        File expected = new File("/home/somebody/.eclipse-asciidoctor-editor/tmp/.no-project/img/diagram1.plantuml.svg");
        assertSameFile(expected, result);
    }
    
    private void assertSameFile(File file1, File file2) {
        assertEquals(Objects.toString(file1), Objects.toString(file2));
    }

}
