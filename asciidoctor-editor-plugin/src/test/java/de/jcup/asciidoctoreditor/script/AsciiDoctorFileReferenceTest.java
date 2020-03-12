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
package de.jcup.asciidoctoreditor.script;

import static org.junit.Assert.*;

import org.junit.Test;

public class AsciiDoctorFileReferenceTest {
    @Test
    public void image_abc_without_brackets_is_found__so_even_when_invalid() {
        /* execute */
        AsciiDoctorFileReference reference = new AsciiDoctorFileReference("image::abc.png", 1, 2, 3);
        
        /* test */
        assertEquals("image::abc.png", reference.getFullExpression());
        assertEquals("image::abc.png", reference.getTarget());
        assertEquals("abc.png", reference.getFilePath());
        assertEquals("image::", reference.getTargetPrefix());
        assertEquals(1,reference.getPosition());
        assertEquals(2,reference.getEnd());
        assertEquals(3,reference.getLengthToNameEnd()); 
    }
    @Test
    public void image_abc_SPACE_with_spaces_png__is_resulting_in_filePath_abc_SPACE_with_spaces_png() {
        /* execute */
        AsciiDoctorFileReference reference = new AsciiDoctorFileReference("image::abc with spaces.png[]", 1, 2, 3);
        
        /* test */
        assertEquals("image::abc with spaces.png[]", reference.getFullExpression());
        assertEquals("image::abc with spaces.png", reference.getTarget());
        assertEquals("abc with spaces.png", reference.getFilePath());
        assertEquals("image::", reference.getTargetPrefix());
        assertEquals(1,reference.getPosition());
        assertEquals(2,reference.getEnd());
        assertEquals(3,reference.getLengthToNameEnd()); 
    }
    
    @Test
    public void include_abd_adoc__is_resolved() {
        /* execute */
        AsciiDoctorFileReference reference = new AsciiDoctorFileReference("include::abc.adoc[]", 1, 2, 3);
        
        /* test */
        assertEquals("include::abc.adoc[]", reference.getFullExpression());
        assertEquals("include::abc.adoc", reference.getTarget());
        assertEquals("abc.adoc", reference.getFilePath());
        assertEquals("include::", reference.getTargetPrefix());
        assertEquals(1,reference.getPosition());
        assertEquals(2,reference.getEnd());
        assertEquals(3,reference.getLengthToNameEnd()); 
    }
    
    @Test
    public void plantuml_abd_puml__is_resolved() {
        /* execute */
        AsciiDoctorFileReference reference = new AsciiDoctorFileReference("plantuml::abc.puml[]", 1, 2, 3);
        
        /* test */
        assertEquals("plantuml::abc.puml[]", reference.getFullExpression());
        assertEquals("plantuml::abc.puml", reference.getTarget());
        assertEquals("abc.puml", reference.getFilePath());
        assertEquals("plantuml::", reference.getTargetPrefix());
        assertEquals(1,reference.getPosition());
        assertEquals(2,reference.getEnd());
        assertEquals(3,reference.getLengthToNameEnd()); 
    }
    
    @Test
    public void ditaa_abd_ditaa__is_resolved() {
        /* execute */
        AsciiDoctorFileReference reference = new AsciiDoctorFileReference("ditaa::abc.ditaa[]", 1, 2, 3);
        
        /* test */
        assertEquals("ditaa::abc.ditaa[]", reference.getFullExpression());
        assertEquals("ditaa::abc.ditaa", reference.getTarget());
        assertEquals("abc.ditaa", reference.getFilePath());
        assertEquals("ditaa::", reference.getTargetPrefix());
        assertEquals(1,reference.getPosition());
        assertEquals(2,reference.getEnd());
        assertEquals(3,reference.getLengthToNameEnd()); 
    }
    
    @Test
    public void image_abc_svg__is_resolved() {
        /* execute */
        AsciiDoctorFileReference reference = new AsciiDoctorFileReference("image::abc.svg[]", 1, 2, 3);
        
        /* test */
        assertEquals("image::abc.svg[]", reference.getFullExpression());
        assertEquals("image::abc.svg", reference.getTarget());
        assertEquals("abc.svg", reference.getFilePath());
        assertEquals("image::", reference.getTargetPrefix());
        assertEquals(1,reference.getPosition());
        assertEquals(2,reference.getEnd());
        assertEquals(3,reference.getLengthToNameEnd()); 
    }

}
