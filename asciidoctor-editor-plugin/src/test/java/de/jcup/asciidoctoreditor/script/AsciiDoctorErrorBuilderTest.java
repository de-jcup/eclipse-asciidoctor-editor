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
package de.jcup.asciidoctoreditor.script;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctoreditor.script.ValidationResult.Type;

public class AsciiDoctorErrorBuilderTest {
	AsciiDoctorErrorBuilder builderToTest;

	@Before
	public void before() {
		builderToTest = new AsciiDoctorErrorBuilder();
	}

	@Test
	public void even_a_null_message_results_in_error() {

		/* prepare */
		String message = null;

		/* execute */
		AsciiDoctorMarker error = builderToTest.build(message);

		/* test */
		assertNotNull(error);
		assertEquals(-1, error.getStart());
		assertEquals(-1, error.getEnd());
		assertEquals(Type.ERROR, error.getType());
		assertEquals("Unknown failure", error.getMessage());
	}

	@Test
	public void an_argument_error_with_adoc_colon_in_string_results_in_last_message_only() {
		/* prepare */
		String message = "(ArgumentError) asciidoctor: FAILED: C:/develop/projects/JCUP/eclipse-asciidoctor-editor/asciidoctor-editor-other/testscripts/17.failure_asciidocfile_not_utf_8_but_umlauts.adoc: Failed to load AsciiDoc document - invalid byte sequence in UTF-8";

		/* execute */
		AsciiDoctorMarker error = builderToTest.build(message);

		/* test */
		assertNotNull(error);
		assertEquals(-1, error.getStart());
		assertEquals(-1, error.getEnd());
		assertEquals(Type.ERROR, error.getType());
		assertEquals("Failed to load AsciiDoc document - invalid byte sequence in UTF-8", error.getMessage());
	}
	@Test
	public void an_argument_error_with_adoc_no_colon_in_string_results_in_complete_message() {
		/* prepare */
		String message = "(ArgumentError) asciidoctor: FAILED: C:/develop/projects/JCUP/eclipse-asciidoctor-editor/asciidoctor-editor-other/testscripts/17.failure_asciidocfile_not_utf_8_but_umlauts.adoc Failed to load AsciiDoc document - invalid byte sequence in UTF-8";

		/* execute */
		AsciiDoctorMarker error = builderToTest.build(message);

		/* test */
		assertNotNull(error);
		assertEquals(-1, error.getStart());
		assertEquals(-1, error.getEnd());
		assertEquals(Type.ERROR, error.getType());
		assertEquals(message, error.getMessage());
	}

}
