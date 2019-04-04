package de.jcup.asciidoctor.converter.markdown;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctor.converter.TestFileAccess;

public class MarkdownFilesToAsciidoctorConverterTest {

    private MarkdownFilesToAsciidoctorConverter converterToTest;

    @Before
    public void before() {
        converterToTest = new MarkdownFilesToAsciidoctorConverter();
    }
    
    @Test
    public void convert_file_markdown1_works() throws Exception{
        /* prepare*/
        File origin = TestFileAccess.getTestResource("markdown/origin/markdown1.md");
        String expected = TestFileAccess.getTestResourceAsString("markdown/expected/markdown1.adoc");
        /* execute */
        String converted = converterToTest.convert(origin);
        System.out.println(converted);
        
        /* test */
        assertEquals(expected,converted);
        
    }
    
    @Test
    public void convert_string_title() {
        assertEquals("= Title", converterToTest.convert("# Title")); 
    }
    
    @Test
    public void convert_string_headline1() {
        assertEquals("== Headline", converterToTest.convert("## Headline")); 
    }

}
