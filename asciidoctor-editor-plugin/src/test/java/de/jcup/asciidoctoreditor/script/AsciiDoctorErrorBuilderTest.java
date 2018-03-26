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
		AsciiDoctorError error = builderToTest.build(message);

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
		AsciiDoctorError error = builderToTest.build(message);

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
		AsciiDoctorError error = builderToTest.build(message);

		/* test */
		assertNotNull(error);
		assertEquals(-1, error.getStart());
		assertEquals(-1, error.getEnd());
		assertEquals(Type.ERROR, error.getType());
		assertEquals(message, error.getMessage());
	}

}
