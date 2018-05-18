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
package de.jcup.asciidoctoreditor;

import static org.junit.Assert.*;

import org.junit.Test;
import static de.jcup.asciidoctoreditor.AsciiDocStringUtils.*;

public class AsciiDocStringUtilsTest {
	@Test
	public void resolveDitaDiagramname_has_name() throws Exception {
		assertEquals("diagrams/diagram_kubernetes_deployment_architecture.ditaa",resolveFilenameOfDiagramMacroOrNull("ditaa::diagrams/diagram_kubernetes_deployment_architecture.ditaa[format=png, alt=\"Diagram about kubernetes deployment architecture\"]"));
	}
	
	@Test
	public void resolvePlantUMLDiagramname_has_name() throws Exception {
		assertEquals("diagrams/diagram_target_architecture.plantuml",resolveFilenameOfDiagramMacroOrNull("plantuml::diagrams/diagram_target_architecture.plantuml[format=svg, alt=\"Class diagram of target and install setup architecture\", width=1024]"));
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
	public void resolveFilenameOfIncludeOrNull_include_colon_colon_src_slash_include1_dot_adoc_has_no_filename_but_null() {
		assertNull(resolveFilenameOfIncludeOrNull("include::src/include1.adoc"));
	}

	@Test
	public void resolveFilenameOfIncludeOrNull_include_colon_colon_src_slash_include1_dot_adoc_brackets_has_src_slash_include1_dot_adoc() {
		assertEquals("src/include1.adoc", resolveFilenameOfIncludeOrNull("include::src/include1.adoc[]"));
	}

}
