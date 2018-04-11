package de.jcup.asciidoctoreditor;

import static org.junit.Assert.*;

import org.junit.Test;

public class PreviewLayoutTest {

	@Test
	public void external_string_is_mapped_to_external_browser() {
		assertEquals(PreviewLayout.EXTERNAL_BROWSER,PreviewLayout.fromId("external"));
	}
	
	@Test
	public void vertical_string_is_mapped_to_vertical() {
		assertEquals(PreviewLayout.VERTICAL,PreviewLayout.fromId("vertical"));
	}
	@Test
	public void horizontal_string_is_mapped_to_horizontal() {
		assertEquals(PreviewLayout.EXTERNAL_BROWSER,PreviewLayout.fromId("external"));
	}
	
	@Test
	public void unkown_is_mapped_to_null() {
		assertNull(PreviewLayout.fromId("unknown"));
	}
	
	

}
