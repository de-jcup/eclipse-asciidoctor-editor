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
        int index = text.length();

        /* execute */
        String result = toTest.resolveIncludeTextOrNull(text, index);

        /* test */
        assertEquals("include::", result);
    }
    
    @Test
    public void include_subfolder1() {
        /* prepare */
        String text = "include::subfolder1/";
        int index = text.length();

        /* execute */
        String result = toTest.resolveIncludeTextOrNull(text, index);

        /* test */
        assertEquals("include::subfolder1/", result);
    }
    
    @Test
    public void include_subfolder2_subfolder_3() {
        /* prepare */
        String text = "include::subfolder2/subfolder3";
        int index = text.length();

        /* execute */
        String result = toTest.resolveIncludeTextOrNull(text, index);

        /* test */
        assertEquals("include::subfolder2/subfolder3", result);
    }
    
    @Test
    public void include_subfolder2_subfolder_3_but_index_behind2() {
        /* prepare */
        String text = "include::subfolder2";
        int index = text.length();
        text+="/subfolder3";

        /* execute */
        String result = toTest.resolveIncludeTextOrNull(text, index);

        /* test */
        assertEquals("include::subfolder2", result);
    }
    
    @Test
    public void include_subfolder2_subfolder_3_but_index_behind2_slash() {
        /* prepare */
        String text = "include::subfolder2/";
        int index = text.length();
        text+="subfolder3";

        /* execute */
        String result = toTest.resolveIncludeTextOrNull(text, index);

        /* test */
        assertEquals("include::subfolder2/", result);
    }
    
    @Test
    public void include_file1() {
        /* prepare */
        String text = "include::file1";
        int index = text.length();

        /* execute */
        String result = toTest.resolveIncludeTextOrNull(text, index);

        /* test */
        assertEquals("include::file1", result);
    }
    
    @Test
    public void include_file1_pos_before_one() {
        /* prepare */
        String text = "include::file1";
        int index = text.length()-1;

        /* execute */
        String result = toTest.resolveIncludeTextOrNull(text, index);

        /* test */
        assertEquals("include::file", result);
    }
    
    @Test
    public void text_before_then_include_file1() {
        /* prepare */
        String text = "== Headline\ninclude::file1";
        int index = text.length();

        /* execute */
        String result = toTest.resolveIncludeTextOrNull(text, index);

        /* test */
        assertEquals("include::file1", result);
    }

}
