package de.jcup.asciidoctoreditor.codeassist;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AsciidocIncludeExistingTextCalculatorTest {
    private AsciidocIncludeExistingTextCalculator toTest;

    @Before
    public void before() {
        toTest = new AsciidocIncludeExistingTextCalculator();
    }

    @Test
    public void include_only() {
        /* prepare */
        String text = "include::";
        int index = text.length()-1;

        /* execute */
        String result = toTest.resolveIncludeTextOrNull(text, index);

        /* test */
        assertEquals("include::", result);
    }
    
    @Test
    public void include_subfolder1() {
        /* prepare */
        String text = "include::subfolder1/";
        int index = text.length()-1;

        /* execute */
        String result = toTest.resolveIncludeTextOrNull(text, index);

        /* test */
        assertEquals("include::subfolder1/", result);
    }
    
    @Test
    public void include_file1() {
        /* prepare */
        String text = "include::file1";
        int index = text.length()-1;

        /* execute */
        String result = toTest.resolveIncludeTextOrNull(text, index);

        /* test */
        assertEquals("include::file1", result);
    }
    
    @Test
    public void text_before_then_include_file1() {
        /* prepare */
        String text = "== Headline\ninclude::file1";
        int index = text.length()-1;

        /* execute */
        String result = toTest.resolveIncludeTextOrNull(text, index);

        /* test */
        assertEquals("include::file1", result);
    }

}
