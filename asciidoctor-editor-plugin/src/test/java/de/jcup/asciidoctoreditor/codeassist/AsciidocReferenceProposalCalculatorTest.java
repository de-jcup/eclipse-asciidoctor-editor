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

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctoreditor.TestResourcesLoader;

public class AsciidocReferenceProposalCalculatorTest {

    private AsciidocReferenceProposalCalculator toTest;
    private File editorFile1;

    @Before
    public void before() {
        toTest = new AsciidocReferenceProposalCalculator("include::", new EditorFileParentAsBaseParentResolver(), new CodeAssistFileFilter());
        editorFile1 = TestResourcesLoader.assertTestFile("codeassist/include/test1/editorfile1.adoc");
        /*
         * Hmm. we should use a mock for internal calculator - currently no mockit
         * available, so let as is
         */
    }

    @Test
    public void editorFile1_only_include_results_in_2_files_one_folder_as_label() throws Exception {
        /* prepare */
        String text = "include::";
        int index = text.length();

        /* execute */
        Set<AsciidocReferenceProposalData> result = toTest.calculate(editorFile1, text, index);

        /* test */
        assertNotNull(result);
        assertEquals(5, result.size());
        Iterator<AsciidocReferenceProposalData> it = result.iterator();
        assertEquals("include::otherfile1.adoc[]", it.next().getProposedCode());
        assertEquals("include::otherfile2.txt[]", it.next().getProposedCode());
        assertEquals("include::otherfile3.adoc[]", it.next().getProposedCode());
        assertEquals("include::subfolder1/", it.next().getProposedCode());
        assertEquals("include::subfolder2/", it.next().getProposedCode());

    }

    @Test
    public void editorFile1_subfolder1_already_includes_returns_result_for_subfolder() throws Exception {
        /* prepare */
        String text = "include::subfolder1";
        int index = text.length();

        /* execute */
        Set<AsciidocReferenceProposalData> result = toTest.calculate(editorFile1, text, index);

        /* test */
        assertNotNull(result);
        assertEquals(1, result.size());
        Iterator<AsciidocReferenceProposalData> it = result.iterator();
        assertEquals("include::subfolder1/otherfile4.adoc[]", it.next().getProposedCode());
    }

    @Test
    public void editorFile1_include_subfolder1_results_in_1_file() throws Exception {
        /* prepare */
        String text = "include::subfolder1/";
        int index = text.length();

        /* execute */
        Set<AsciidocReferenceProposalData> result = toTest.calculate(editorFile1, text, index);

        /* test */
        assertNotNull(result);
        assertEquals(1, result.size());
        Iterator<AsciidocReferenceProposalData> it = result.iterator();
        assertEquals("include::subfolder1/otherfile4.adoc[]", it.next().getProposedCode());

    }

    @Test
    public void otherfile4_dot_dot_slash_subfolder2_results_in_2_files() throws Exception {
        /* prepare */
        File otherFile14 = TestResourcesLoader.assertTestFile("codeassist/include/test1/subfolder1/otherfile4.adoc");

        String text = "include::../subfolder2/";
        int index = text.length();

        /* execute */
        Set<AsciidocReferenceProposalData> result = toTest.calculate(otherFile14, text, index);

        /* test */
        assertNotNull(result);
        assertEquals(5, result.size());
        Iterator<AsciidocReferenceProposalData> it = result.iterator();
        assertEquals("include::../subfolder2/asciidoctor-editor with spaces.png[]", it.next().getProposedCode());
        assertEquals("include::../subfolder2/asciidoctor-editor.png[]", it.next().getProposedCode());
        assertEquals("include::../subfolder2/ditaa-file1.ditaa[]", it.next().getProposedCode());
        assertEquals("include::../subfolder2/puml-file1.puml[]", it.next().getProposedCode());
        assertEquals("include::../subfolder2/subfolder3/", it.next().getProposedCode());

    }

    @Test
    public void editorFile1_include_sub_results_in_2_folders() throws Exception {
        /* prepare */
        String text = "include::sub";
        int index = text.length() - 1;

        /* execute */
        Set<AsciidocReferenceProposalData> result = toTest.calculate(editorFile1, text, index);

        /* test */
        assertNotNull(result);
        assertEquals(2, result.size());
        Iterator<AsciidocReferenceProposalData> it = result.iterator();
        assertEquals("include::subfolder1/", it.next().getProposedCode());
        assertEquals("include::subfolder2/", it.next().getProposedCode());

    }

    @Test
    public void editorFile1_include_subfolder2_slash_sub_results_in_1_folder() throws Exception {
        /* prepare */
        String text = "include::subfolder2/sub";
        int index = text.length() - 1;

        /* execute */
        Set<AsciidocReferenceProposalData> result = toTest.calculate(editorFile1, text, index);

        /* test */
        assertNotNull(result);
        assertEquals(1, result.size());
        Iterator<AsciidocReferenceProposalData> it = result.iterator();
        assertEquals("include::subfolder2/subfolder3/", it.next().getProposedCode());

    }

}
