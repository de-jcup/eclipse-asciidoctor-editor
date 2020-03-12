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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctoreditor.TestResourcesLoader;

public class AsciiDoctorFileReferenceValidatorTest {

    private AsciiDoctorFileReferenceValidator validatorToTest;

    @Before
    public void before() {
        validatorToTest = new AsciiDoctorFileReferenceValidator();
    }
    
    @Test
    public void valid_include() {
        /* prepare */
        File editorFile = TestResourcesLoader.assertTestFile("codeassist/include/test1/editorfile1.adoc");
        Collection<AsciiDoctorMarker> errors = new ArrayList<>();
        
        /* execute */
        validatorToTest.validate(editorFile, Collections.singleton(new AsciiDoctorFileReference("include::otherfile1.adoc[]", 1, 20, 20)),errors);
        
        /* test */
        assertTrue(errors.isEmpty());
    }
    
    @Test
    public void non_existing_include() {
        /* prepare */
        File editorFile = TestResourcesLoader.assertTestFile("codeassist/include/test1/editorfile1.adoc");
        Collection<AsciiDoctorMarker> errors = new ArrayList<>();
        
        /* execute */
        validatorToTest.validate(editorFile, Collections.singleton(new AsciiDoctorFileReference("include::unkknown.adoc[]", 1, 20, 20)),errors);
        
        /* test */
        assertEquals(1,errors.size());
    }
    
    @Test
    public void non_existing_include_only_start_tag() {
        /* prepare */
        File editorFile = TestResourcesLoader.assertTestFile("codeassist/include/test1/editorfile1.adoc");
        Collection<AsciiDoctorMarker> errors = new ArrayList<>();
        
        /* execute */
        validatorToTest.validate(editorFile, Collections.singleton(new AsciiDoctorFileReference("include::", 1, 20, 20)),errors);
        
        /* test */
        assertEquals(1,errors.size());
    }
    
    @Test
    public void resolve_image_file_path_with_imagedir() {
        /* prepare */
        validatorToTest.setImageDir("./subfolder2/");
        File editorFile = TestResourcesLoader.assertTestFile("codeassist/include/test1/editorfile1.adoc");
        Collection<AsciiDoctorMarker> errors = new ArrayList<>();
        
        /* execute */
        validatorToTest.validate(editorFile, Collections.singleton(new AsciiDoctorFileReference("image::asciidoctor-editor.png", 1, 20, 20)),errors);
        
        /* test */
        assertEquals(0,errors.size());
    }
    
    @Test
    public void resolve_image_file_path_with_imagedir__with_filename_having_spaces_inside() {
        /* prepare */
        validatorToTest.setImageDir("./subfolder2/");
        File editorFile = TestResourcesLoader.assertTestFile("codeassist/include/test1/editorfile1.adoc");
        Collection<AsciiDoctorMarker> errors = new ArrayList<>();
        
        /* execute */
        validatorToTest.validate(editorFile, Collections.singleton(new AsciiDoctorFileReference("image::asciidoctor-editor with spaces.png", 1, 20, 20)),errors);
        
        /* test */
        assertEquals(0,errors.size());
    }
    
    @Test
    public void resolved_image_file_path_with_imagedir_not_found() {
        /* prepare */
        validatorToTest.setImageDir("./subfolder2/");
        File editorFile = TestResourcesLoader.assertTestFile("codeassist/include/test1/editorfile1.adoc");
        Collection<AsciiDoctorMarker> errors = new ArrayList<>();
        
        /* execute */
        validatorToTest.validate(editorFile, Collections.singleton(new AsciiDoctorFileReference("image::unknown.png", 1, 20, 20)),errors);
        
        /* test */
        assertEquals(1,errors.size());
    }

}
