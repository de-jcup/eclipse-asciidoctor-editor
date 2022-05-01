/*
 * Copyright 2018 Albert Tregnaghi
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

import static de.jcup.asciidoctoreditor.asciidoc.AsciiDocStringUtils.*;
import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

public class AsciiDocStringUtilsTest {

    @Test
    public void deleted_temporary_directory_will_be_autocreated_again() throws Exception {
        /* prepare */
        Path tmp = Files.createTempDirectory("asciidoc-test");
        Path filePath = tmp.resolve("sub1/sub2/sub3/testme.puml");

        Files.delete(tmp);

        assertTrue(Files.notExists(tmp));

        /* execute */
        AsciiDocStringUtils.writeTextToUTF8File("transforemd-textexample", filePath.toFile());

    }

    @Test
    public void resolveDitaDiagramname_has_name() throws Exception {
        assertEquals("diagrams/diagram_kubernetes_deployment_architecture.ditaa",
                resolveFilenameOfDiagramMacroOrNull("ditaa::diagrams/diagram_kubernetes_deployment_architecture.ditaa[format=png, alt=\"Diagram about kubernetes deployment architecture\"]"));
    }

    @Test
    public void resolvePlantUMLDiagramname_has_name() throws Exception {
        assertEquals("diagrams/diagram_target_architecture.plantuml",
                resolveFilenameOfDiagramMacroOrNull("plantuml::diagrams/diagram_target_architecture.plantuml[format=svg, alt=\"Class diagram of target and install setup architecture\", width=1024]"));
    }

    @Test
    public void resolveFilenameOfIncludeOrNull_gargamel_has_no_filename_but_null() {
        assertNull(resolveFilenameOfIncludeOrNull("gargamel"));
    }

    @Test
    public void resolveFilenameOfIncludeOrNull_include_colon_colon_has_no_filename_but_null() {
        assertNull(resolveFilenameOfIncludeOrNull("include::"));
    }

    @Test
    public void resolveFilenameOfIncludeOrNull_include_colon_colon_src_slash_include1_dot_java_has_no_filename_but_null() {
        assertNull(resolveFilenameOfIncludeOrNull("include::src/include1.java"));
    }

    @Test
    public void resolveFilenameOfIncludeOrNull_include_colon_colon_src_slash_include1_dot_adoc_brackets_has_src_slash_include1_dot_adoc() {
        assertEquals("src/include1.adoc", resolveFilenameOfIncludeOrNull("include::src/include1.adoc[]"));
    }

    @Test
    public void resolveFilenameOfIncludeOrNull_include_colon_colon_src_slash_include1_dot_java_brackets_has_src_slash_include1_dot_java() {
        assertEquals("src/include1.java", resolveFilenameOfIncludeOrNull("include::src/include1.java[]"));
    }

    @Test
    public void resolveFilenameOfIncludeOrNull_include_colon_colon_src_slash_include1_dot_java_brackets_with_something_inside_has_src_slash_include1_dot_java() {
        assertEquals("src/include1.java", resolveFilenameOfIncludeOrNull("include::src/include1.java[somethinginside]"));
    }

    @Test
    public void resolveFilenameOfImageOrNull_image_colon_colon_src_slash_subfolder_slash_imagename_dot_png_brackets_with_something_inside_has_subfolder_slash_imagename_dot_png() {
        assertEquals("subfolder/imagename.png", resolveFilenameOfImageOrNull("image::subfolder/imagename.png[something]"));
    }

    @Test
    public void resolveTextFromStartToBracketsEnd_includeTexts_for_an_image_complete_resolved_0_0() {
        String include = "something::very-special-and-useful[title=\"AsciiDoctor Editor Logo\" opts=\"inline\"]";
        String line = include + "... something else";
        assertEquals(include, resolveTextFromStartToBracketsEnd(line, 0, 0).text);
    }

    @Test
    public void resolveTextFromStartToBracketsEnd_includeTexts_for_an_image_complete_resolved_0_5() {
        String include = "something::very-special-and-useful[title=\"AsciiDoctor Editor Logo\" opts=\"inline\"]";
        String line = include + "... something else";
        assertEquals(include, resolveTextFromStartToBracketsEnd(line, 0, 5).text);
    }

}
