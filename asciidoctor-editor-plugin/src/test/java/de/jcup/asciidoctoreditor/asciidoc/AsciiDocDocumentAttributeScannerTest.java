package de.jcup.asciidoctoreditor.asciidoc;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class AsciiDocDocumentAttributeScannerTest {
    private AsciiDocDocumentAttributeScanner toTest;

    @Before
    public void before() {
        toTest = new AsciiDocDocumentAttributeScanner();
    }
    @Test
    public void test_null() {
        /* prepare */
        /* execute */
        Map<String, Object> result = toTest.scan(null);

        /* test */
        assertTrue(result.isEmpty());
    }
    
    @Test
    public void test_colon_alone() {
        /* prepare */
        /* execute */
        Map<String, Object> result = toTest.scan(":");

        /* test */
        assertTrue(result.isEmpty());
    }
    
    @Test
    public void test_two_colon_empty() {
        /* prepare */
        /* execute */
        Map<String, Object> result = toTest.scan("::");

        /* test */
        assertTrue(result.isEmpty());
    }
    
    @Test
    public void test_abc_but_empty() {
        /* prepare */
        /* execute */
        Map<String, Object> result = toTest.scan(":abc:");

        /* test */
        assertTrue(result.containsKey("abc"));
        assertEquals("",result.get("abc"));
    }
    
    @Test
    public void test_abc_value() {
        /* prepare */
        /* execute */
        Map<String, Object> result = toTest.scan(":abc: value");
        
        /* test */
        assertTrue(result.containsKey("abc"));
        assertEquals("value",result.get("abc"));
    }
    
    @Test
    public void test_abc_value1_new_line_def_value2() {
        /* prepare */
        /* execute */
        Map<String, Object> result = toTest.scan(":abc: value1\n:def:value2");
        
        /* test */
        assertTrue(result.containsKey("abc"));
        assertEquals("value1",result.get("abc"));
        assertTrue(result.containsKey("def"));
        assertEquals("value2",result.get("def"));
        
        assertEquals(2,result.size());
    }
    
    
    

}
