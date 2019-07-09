/*
 * Copyright 2019 Albert Tregnaghi
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
