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

public class DefaultAsciiDoctorScriptModelBuilderTest {

    private AsciiDoctorScriptModelBuilder builderToTest;

    @Before
    public void before() {
        builderToTest = new DefaultAsciiDoctorScriptModelBuilder();
    }

    @Test
    public void a_headline_with_anker_before_gets_id_of_anker() throws Exception {
        AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build("[[anker1]]\n== Headline1");
        /* test */
        assertThat(asciidoctorScriptModel).hasHeadlines(1).hasHeadline("Headline1").hasHeadlineWithId("anker1");

    }

    @Test
    public void three_headlines_with_same_name_got_numbering_first_has_no_number() throws Exception {

        /* execute */
        AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build("== Headline1\nsometext\n\n== Headline1\nsometext\n\n== Headline1\nsometext\n\n");

        /* test */
        /* @formatter:off*/
		assertThat(asciidoctorScriptModel).
			hasHeadlines(3).
			hasHeadlineWithId("_headline1").
			hasHeadlineWithId("_headline1_2").
			hasHeadlineWithId("_headline1_3");
		/* @formatter:on*/

    }

    @Test
    public void a_line_with_5_spaces_and_Xfunction_test_is_NOT_recognized() throws Exception {
        AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build("     Xfunction test {}");
        /* test */
        assertThat(asciidoctorScriptModel).hasNoHeadlines();

    }

}
