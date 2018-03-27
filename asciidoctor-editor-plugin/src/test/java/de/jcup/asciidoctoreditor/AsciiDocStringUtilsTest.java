package de.jcup.asciidoctoreditor;

import static org.junit.Assert.*;

import org.junit.Test;
import static de.jcup.asciidoctoreditor.AsciiDocStringUtils.*;
public class AsciiDocStringUtilsTest {

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
		assertEquals("src/include1.adoc",resolveFilenameOfIncludeOrNull("include::src/include1.adoc[]"));
	}

}
