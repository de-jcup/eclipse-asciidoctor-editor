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
package de.jcup.asciidoctoreditor.codeassist;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AsciidocReferenceExistingTextCalculatorTest {
    private AsciidocReferenceExistingTextCalculator toTest;

    @Before
    public void before() {
        toTest = new AsciidocReferenceExistingTextCalculator("include::");
    }

    @Test
    public void include_only() {
        /* prepare */
        String text = "include::";
        int index = text.length();

        /* execute */
        String result = toTest.resolveReferenceTextOrNull(text, index);

        /* test */
        assertEquals("include::", result);
    }
    
    @Test
    public void include_subfolder1() {
        /* prepare */
        String text = "include::subfolder1/";
        int index = text.length();

        /* execute */
        String result = toTest.resolveReferenceTextOrNull(text, index);

        /* test */
        assertEquals("include::subfolder1/", result);
    }
    
    @Test
    public void include_subfolder2_subfolder_3() {
        /* prepare */
        String text = "include::subfolder2/subfolder3";
        int index = text.length();

        /* execute */
        String result = toTest.resolveReferenceTextOrNull(text, index);

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
        String result = toTest.resolveReferenceTextOrNull(text, index);

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
        String result = toTest.resolveReferenceTextOrNull(text, index);

        /* test */
        assertEquals("include::subfolder2/", result);
    }
    
    @Test
    public void include_file1() {
        /* prepare */
        String text = "include::file1";
        int index = text.length();

        /* execute */
        String result = toTest.resolveReferenceTextOrNull(text, index);

        /* test */
        assertEquals("include::file1", result);
    }
    
    @Test
    public void include_file1_pos_before_one() {
        /* prepare */
        String text = "include::file1";
        int index = text.length()-1;

        /* execute */
        String result = toTest.resolveReferenceTextOrNull(text, index);

        /* test */
        assertEquals("include::file", result);
    }
    
    @Test
    public void text_before_then_include_file1() {
        /* prepare */
        String text = "== Headline\ninclude::file1";
        int index = text.length();

        /* execute */
        String result = toTest.resolveReferenceTextOrNull(text, index);

        /* test */
        assertEquals("include::file1", result);
    }

}
