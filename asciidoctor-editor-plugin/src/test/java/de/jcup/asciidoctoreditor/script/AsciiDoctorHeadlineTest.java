package de.jcup.asciidoctoreditor.script;

import static org.junit.Assert.*;

import org.junit.Test;

public class AsciiDoctorHeadlineTest {

	@Test
	public void calculateId_x_space_y_is__converted_to_underscore_x_underscore_y() {
		assertEquals("_x_y",AsciiDoctorHeadline.calculateId("x y"));
	}
	
	@Test
	public void calculateId_X_space_y_is__converted_to_underscore_x_underscore_y() {
		assertEquals("_x_y",AsciiDoctorHeadline.calculateId("X y"));
	}
	
	@Test
	public void calculateId_more_complex_scenarios() {
		assertEquals("_alpha_centauri_is_coool",AsciiDoctorHeadline.calculateId("Alpha centauri is!!!!coool!!!"));
		assertEquals("_alpha_centauri_is_coool",AsciiDoctorHeadline.calculateId("Alpha centauri is!!-----!coool!!!"));
		assertEquals("_alpha_centauri_is_coool",AsciiDoctorHeadline.calculateId("Alpha centauri is-coool!!!"));
		assertEquals("_alpha_centauri_is_coool",AsciiDoctorHeadline.calculateId("Alpha centauri is coool..."));
	}


}
