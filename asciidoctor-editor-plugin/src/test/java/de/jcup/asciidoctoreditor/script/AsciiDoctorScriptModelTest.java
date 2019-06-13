/*
 * Copyright 2017 Albert Tregnaghi
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

public class AsciiDoctorScriptModelTest {
	private AsciiDoctorScriptModel modelToTest;

	@Before
	public void before() {
		modelToTest = new AsciiDoctorScriptModel();
	}

	@Test
	public void has_errors_returns_true_when_one_error_is_added() {
		/* execute */
		modelToTest.getErrors().add(new AsciiDoctorMarker(100, 120, "buh"));
		
		/* test */
		assertTrue(modelToTest.hasErrors());
	}
	
	@Test
	public void has_errors_returns_false_when_no_error_is_added() {
		/* test */
		assertFalse(modelToTest.hasErrors());
	}
	
}
