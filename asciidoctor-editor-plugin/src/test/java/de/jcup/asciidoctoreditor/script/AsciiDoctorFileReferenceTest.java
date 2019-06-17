package de.jcup.asciidoctoreditor.script;

import static org.junit.Assert.*;

import org.junit.Test;

public class AsciiDoctorFileReferenceTest {

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
