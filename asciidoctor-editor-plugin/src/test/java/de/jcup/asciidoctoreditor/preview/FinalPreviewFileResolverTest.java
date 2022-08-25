/*
 * Copyright 2022 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.preview;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Objects;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.jcup.asciidoctoreditor.EditorType;

public class FinalPreviewFileResolverTest {

    private static File plantUMLHTMLpreview1;
    private FinalPreviewFileResolver resolverToTest;

    @BeforeClass
    public static void beforeAll() {
        /* prepare */
        plantUMLHTMLpreview1 = new File("./src/test/resources/final-preview/plantuml-internal-editor-preview-1.html");
        
        /* check precondition */
        assertTrue(plantUMLHTMLpreview1.exists());
    }
    
    
    @Before
    public void before() {
        resolverToTest = new FinalPreviewFileResolver();
    }
    
    @Test
    public void editor_type_asciidoc_returns_html5_preview_file() {
        
        /* execute */
        File result = resolverToTest.resolvePreviewFileFromGeneratedHTMLFile(plantUMLHTMLpreview1, EditorType.ASCIIDOC);
        
        /* test */
        File expected = plantUMLHTMLpreview1;
        assertSameFile(expected, result);
    }
    
    @Test
    public void editor_type_plantuml_returns_file_by_name_of_html5_preview_file() {
        /* execute */
        File result = resolverToTest.resolvePreviewFileFromGeneratedHTMLFile(plantUMLHTMLpreview1, EditorType.PLANTUML);
        
        /* test */
        File expected = new File("/home/somebody/.eclipse-asciidoctor-editor/tmp/.no-project/img/diagram1.plantuml.svg");
        assertSameFile(expected, result);
    }
    
    private void assertSameFile(File file1, File file2) {
        assertEquals(Objects.toString(file1), Objects.toString(file2));
    }

}
