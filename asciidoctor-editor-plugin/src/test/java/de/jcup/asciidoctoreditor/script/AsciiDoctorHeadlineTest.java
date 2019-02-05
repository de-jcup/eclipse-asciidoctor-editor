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

import org.junit.Test;

public class AsciiDoctorHeadlineTest {

	@Test
	public void calculateId_contains_umlauts_as_well() {
		assertEquals("_my_sectiön", AsciiDoctorHeadline.calculateId("My Sectiön"));
		assertEquals("_äpfel", AsciiDoctorHeadline.calculateId("Äpfel"));
		assertEquals("_würgreflex", AsciiDoctorHeadline.calculateId("Würgreflex"));
		assertEquals("_beißen", AsciiDoctorHeadline.calculateId("Beißen"));
	}
	
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
