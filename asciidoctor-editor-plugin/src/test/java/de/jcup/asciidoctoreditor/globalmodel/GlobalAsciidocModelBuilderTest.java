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
package de.jcup.asciidoctoreditor.globalmodel;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;

import de.jcup.asciidoctoreditor.SystemOutLogAdapter;

public class GlobalAsciidocModelBuilderTest {

    private static File baseFolder1;
    private static File test1_adoc;
    private static File test2_adoc;
    private static File test3_adoc;
    private static File test4_adoc;
    private static File test5_adoc;
    private static File test6_not_existing_adoc;
    private static File baseFolder2;

    @BeforeClass
    public static void before() {
        baseFolder1 = new File("./src/test/resources/workspace1/project1");

        test1_adoc = new File(baseFolder1, "test1.adoc");
        test2_adoc = new File(baseFolder1, "test2.adoc");
        test3_adoc = new File(baseFolder1, "subfolder1/test3.adoc");
        test4_adoc = new File(baseFolder1, "test4.adoc");
        
        baseFolder2 = new File("./src/test/resources/workspace1/project2");
        test5_adoc = new File(baseFolder2, "test5.adoc");
        test6_not_existing_adoc = new File(baseFolder2, "test6-not-existing.adoc");

        /* check preconditions */
        assertTrue(test1_adoc.exists());
        assertTrue(test2_adoc.exists());
        assertTrue(test3_adoc.exists());
        assertTrue(test4_adoc.exists());
        assertTrue(test5_adoc.exists());
        
        assertFalse(test6_not_existing_adoc.exists());
    }

    @Test
    public void build_workspace1_project1_start_with_test4_endless_include_recursive_can_be_read_and_is_same_object() {
        /* execute */
        GlobalAsciidocModel model = new GlobalAsciidocModelBuilder().from(baseFolder1).build();

        /* test */
        assertNotNull(model);

        AsciidocFile asciidocFile1 = model.getAsciidocFileOrNull(test4_adoc, new SystemOutLogAdapter());
        assertNotNull(asciidocFile1);

        AsciidocFile asciidocFile2 = assertAdocFileIncludesExistingFileAndIsNotFallback(asciidocFile1, test4_adoc);
        assertEquals(asciidocFile1, asciidocFile2);

        assertFalse(asciidocFile1.isFallback());
        asciidocFile1.getIncludeNodes().stream().map(node->node.getIncludedAsciidocFile()).collect(Collectors.toList()).contains(asciidocFile2);
        asciidocFile1.getAsciidocFilesWhichIncludeThisFile().contains(asciidocFile2);
    }

    @Test
    public void test_5_includes_not_existing_but_has_fallback() throws IOException {
        /* execute */
        GlobalAsciidocModel model = new GlobalAsciidocModelBuilder().from(baseFolder2).logWith(new SystemOutLogAdapter()).build();

        /* test */
        assertNotNull(model);

        AsciidocFile asciidocFile1 = model.getAsciidocFileOrNull(test5_adoc, new SystemOutLogAdapter());
        assertNotNull(asciidocFile1);

        List<AsciidocIncludeNode> includeNodes = asciidocFile1.getIncludeNodes();
        assertEquals(1,includeNodes.size());
        
        AsciidocIncludeNode includeNode = includeNodes.iterator().next();
        AsciidocFile includedFile = includeNode.getIncludedAsciidocFile();
        assertEquals(test6_not_existing_adoc.getCanonicalFile().toString(), includedFile.getFile().toString());
        assertTrue(includedFile.isFallback());
        assertFalse(includedFile.getFile().exists());
    }

    @Test
    public void build_workspace1_project1_start_with_test1() {
        /* execute */
        GlobalAsciidocModel model = new GlobalAsciidocModelBuilder().from(baseFolder1).build();

        /* test */
        assertNotNull(model);

        AsciidocFile asciidocFile1 = model.getAsciidocFileOrNull(test1_adoc, new SystemOutLogAdapter());
        assertNotNull(asciidocFile1);

        AsciidocFile asciidocFile2 = assertAdocFileIncludesExistingFileAndIsNotFallback(asciidocFile1, test2_adoc);
        AsciidocFile asciidocFile3 = assertAdocFileIncludesExistingFileAndIsNotFallback(asciidocFile2, test3_adoc);
        assertAdocFileIncludesNothing(asciidocFile3);
    }

    @Test
    public void build_workspace1_project1_start_with_test2() {
        /* execute */
        GlobalAsciidocModel model = new GlobalAsciidocModelBuilder().from(baseFolder1).build();

        /* test */
        assertNotNull(model);

        AsciidocFile asciidocFile2 = model.getAsciidocFileOrNull(test2_adoc, new SystemOutLogAdapter());
        assertNotNull(asciidocFile2);

        AsciidocFile asciidocFile3 = assertAdocFileIncludesExistingFileAndIsNotFallback(asciidocFile2, test3_adoc);
        assertAdocFileIncludesNothing(asciidocFile3);
    }

    private void assertAdocFileIncludesNothing(AsciidocFile asciidocFile) {
        assertNotNull(asciidocFile);

        List<AsciidocIncludeNode> includedAsciidocFiles = asciidocFile.getIncludeNodes();
        assertTrue(includedAsciidocFiles.isEmpty());

    }

    private AsciidocFile assertAdocFileIncludesExistingFileAndIsNotFallback(AsciidocFile asciidocFile, File expectedToBeIncludedRelative) {
        File expectedToBeIncluded;
        try {
            /*
             * next line wil throw an NoSuchFileException when files does not exist -
             * because using nio methods which do automatically check this
             */
            expectedToBeIncluded = expectedToBeIncludedRelative.toPath().toRealPath().toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertNotNull(asciidocFile);

        List<AsciidocIncludeNode> includedAsciidocFiles = asciidocFile.getIncludeNodes();
        assertFalse(includedAsciidocFiles.isEmpty());

        for (AsciidocIncludeNode includedNode : includedAsciidocFiles) {
            AsciidocFile included = includedNode.getIncludedAsciidocFile();
            File includedFile = included.getFile();
            if (Objects.equals(expectedToBeIncluded, includedFile)) {
                
                assertFalse("Included found, but it is marked as a fallback!", included.isFallback());
                
                return included;
            }
        }
        fail("The asciidocfile: " + asciidocFile + " does not include: " + expectedToBeIncluded);
        return null;
    }

}
