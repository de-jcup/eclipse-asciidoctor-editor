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

import static de.jcup.asciidoctoreditor.script.AssertScriptModel.*;

import org.junit.Before;
import org.junit.Test;

public class AsciiDoctorScriptModelBuilderTest {

	private AsciiDoctorScriptModelBuilder builderToTest;

	@Before
	public void before() {
		builderToTest = new AsciiDoctorScriptModelBuilder();
	}

	@Test
	public void a_line_with_5_spaces_and_Xfunction_test_is_NOT_recognized() throws Exception {
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build("     Xfunction test {}");
		/* test */
		assertThat(asciidoctorScriptModel).hasNoFunctions();

	}

}
