package de.jcup.asciidoctoreditor.script;

import static org.junit.Assert.*;

import org.junit.Test;

public class AsciiDoctorInlineAnchorTest {

	@Test
	public void label_alpha_has_alpha_as_id() {
		assertEquals("alpha", new AsciiDoctorInlineAnchor("[[alpha]]", -1, -1).getId());
	}

	@Test
	public void label_alpha_hyphen_rocks_da_house_has_alpha_hyphen_rocks_da_house_as_id() {
		assertEquals("alpha-rocksdahouse", new AsciiDoctorInlineAnchor("[[alpha-rocksdahouse]]", -1, -1).getId());
	}

	@Test
	public void label_null_has_null_id() {
		assertNull(new AsciiDoctorInlineAnchor(null, -1, -1).getId());
	}

}
