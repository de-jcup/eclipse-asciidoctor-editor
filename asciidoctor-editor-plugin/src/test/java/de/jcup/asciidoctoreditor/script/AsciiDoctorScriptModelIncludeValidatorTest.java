package de.jcup.asciidoctoreditor.script;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctoreditor.TestResourcesLoader;

public class AsciiDoctorScriptModelIncludeValidatorTest {

    private AsciiDoctorScriptModelIncludeValidator validatorToTest;

    @Before
    public void before() {
        validatorToTest = new AsciiDoctorScriptModelIncludeValidator();
    }
    
    @Test
    public void valid_include() {
        /* prepare */
        File editorFile = TestResourcesLoader.assertTestFile("codeassist/include/test1/editorfile1.adoc");
        AsciiDoctorScriptModel model = new  AsciiDoctorScriptModel();
        model.getIncludes().add(new AsciiDoctorInclude("include::otherfile1.adoc[]", "include::otherfile1.adoc", 1, 20, 20));
        
        /* execute */
        validatorToTest.validate(model, editorFile);
        
        /* test */
        assertFalse(model.hasErrors());
    }
    
    @Test
    public void non_existing_include() {
        /* prepare */
        File editorFile = TestResourcesLoader.assertTestFile("codeassist/include/test1/editorfile1.adoc");
        AsciiDoctorScriptModel model = new  AsciiDoctorScriptModel();
        model.getIncludes().add(new AsciiDoctorInclude("include::unkknown.adoc[]", "include::unkknown.adoc", 1, 20, 20));
        
        /* execute */
        validatorToTest.validate(model, editorFile);
        
        /* test */
        assertTrue(model.hasErrors());
        assertEquals(1,model.getErrors().size());
    }
    
    @Test
    public void non_existing_include_only_start_tag() {
        /* prepare */
        File editorFile = TestResourcesLoader.assertTestFile("codeassist/include/test1/editorfile1.adoc");
        AsciiDoctorScriptModel model = new  AsciiDoctorScriptModel();
        model.getIncludes().add(new AsciiDoctorInclude("include::", "include::", 1, 20, 20));
        
        /* execute */
        validatorToTest.validate(model, editorFile);
        
        /* test */
        assertTrue(model.hasErrors());
        assertEquals(1,model.getErrors().size());
    }

}
