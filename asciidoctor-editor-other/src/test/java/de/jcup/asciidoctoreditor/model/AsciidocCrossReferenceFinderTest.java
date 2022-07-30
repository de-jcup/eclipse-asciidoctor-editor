/*
 * Copyright 2021 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.model;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctoreditor.LogAdapter;
import de.jcup.asciidoctoreditor.RootParentFinder;

public class AsciidocCrossReferenceFinderTest {

    private LogAdapter logAdapter;
    private RootParentFinder rootParentFinder;
    private AsciidocCrossReferenceFinder finderToTest;
    private File workspace1Folder;

    @Before
    public void before() {
        rootParentFinder = mock(RootParentFinder.class);
        logAdapter = mock(LogAdapter.class);

        finderToTest = new AsciidocCrossReferenceFinder(rootParentFinder, logAdapter);
    
        workspace1Folder = new File("./src/test/resources/crossreferences/workspace1");
        assertTrue(workspace1Folder.exists());
    
    }
    

    @Test
    public void section_test1_is_defined_2_times_in_different_files_length_same_but_pos_differs() {
        /* prepare */
       
        when(rootParentFinder.findRootParent()).thenReturn(workspace1Folder);

        /* execute */
        List<AsciidocCrossReference> references = finderToTest.findReferences("section-test1");

        /* test */
        assertEquals(2, references.size());

        for (AsciidocCrossReference reference : references) {
            File referenceFile = reference.getFile();
            String name = referenceFile.getName();
            if (name.contentEquals("test1a.adoc")) {
                assertEquals("test1a pos check failed", 19, reference.getPositionStart());
            }else {
                assertEquals("test1b* pos check failed", 74, reference.getPositionStart());
            }
            assertEquals(17,reference.length);
        }

    }
    
    @Test
    public void test3_in_subfolder_found_by_cross_reference_id() {
        when(rootParentFinder.findRootParent()).thenReturn(workspace1Folder);

        /* execute */
        List<AsciidocCrossReference> references = finderToTest.findReferences("section-test3");

        /* test */
        assertEquals(1, references.size());
        assertEquals("test3.adoc", references.iterator().next().getFile().getName());

    }

}
