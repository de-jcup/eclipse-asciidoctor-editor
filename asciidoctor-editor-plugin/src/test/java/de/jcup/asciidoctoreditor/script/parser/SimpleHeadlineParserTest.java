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
package de.jcup.asciidoctoreditor.script.parser;

import static de.jcup.asciidoctoreditor.script.parser.AssertHeadlines.*;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctoreditor.script.AsciiDoctorHeadline;

public class SimpleHeadlineParserTest {
    private SimpleHeadlineParser parserToTest;

    @Before
    public void before() {
        parserToTest = new SimpleHeadlineParser();
    }

    @Test
    public void two_headline4_with_text_are_recognized_as_headlines_4() throws Exception {
        /* prepare */
        String text = "==== headline1\n==== headline2";
        // .............01234567890 123456789012

        /* execute */
        List<AsciiDoctorHeadline> result = parserToTest.parse(text);

        /* test */
        /* @formatter:off*/
		assertHeadlines(result).
			hasHeadline("headline1").
				withPosition(0).
				withEnd(13).
			and().
			hasHeadline("headline2").
				withPosition(15).
				withEnd(28);
		/* @formatter:on*/
    }

    @Test
    public void two_headline4_with_no_text_are_NOT_recognized_as_headlines_4() throws Exception {
        /* prepare */
        String text = "====\n====";

        /* execute */
        List<AsciiDoctorHeadline> result = parserToTest.parse(text);

        /* test */
        /* @formatter:off*/
		assertHeadlines(result).
			hasNoHeadlines();
		/* @formatter:on*/
    }

    @Test
    public void headline1_new_line_headline2__headline1_has_pos_0_headline2_pos_12() throws Exception {

        /* prepare */
        String text = "= headline1\n= headline2";
        // .............01234567890 123456789012

        /* execute */
        List<AsciiDoctorHeadline> result = parserToTest.parse(text);

        /* test */
        /* @formatter:off*/
		assertHeadlines(result).
			hasHeadline("headline1").
				withPosition(0).
				withEnd(10).
			and().
			hasHeadline("headline2").
				withPosition(12).
				withEnd(22);
		/* @formatter:on*/

    }

    @Test
    public void headline1_new_line_headline2__headline2_has_deep1() throws Exception {

        /* prepare */
        String text = "= headline1\n= headline2";
        // .............01234567890 12

        /* execute */
        List<AsciiDoctorHeadline> result = parserToTest.parse(text);

        /* test */
        assertHeadlines(result).hasHeadlines(2).hasHeadline("headline2").withDeep(1);

    }

    @Test
    public void wrongheadline1_new_line_headline2__headline2_has_deep1() throws Exception {

        /* prepare */
        String text = "=wrongheadline1\n= headline2";
        // .............01234567890 12

        /* execute */
        List<AsciiDoctorHeadline> result = parserToTest.parse(text);

        /* test */
        assertHeadlines(result).hasHeadlines(1).hasHeadline("headline2").withDeep(1);

    }

    @Test
    public void empty_string_has_no_parser_results() {
        Collection<AsciiDoctorHeadline> result = parserToTest.parse("");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void one_line_headline1__has_one_result_with_deep_1() {
        List<AsciiDoctorHeadline> result = parserToTest.parse("= headline1");

        assertHeadlines(result).hasHeadlines(1).hasHeadline("headline1").withDeep(1);
    }

    @Test
    public void one_line_space_headline1__has_one_result() {
        Collection<AsciiDoctorHeadline> result = parserToTest.parse("= headline1");

        assertEquals(1, result.size());
        AsciiDoctorHeadline headlineFound = result.iterator().next();
        assertEquals(1, headlineFound.getDeep());
        assertEquals("headline1", headlineFound.getName());
    }

    @Test
    public void three_headlines__has_3_result() {
        Collection<AsciiDoctorHeadline> result = parserToTest.parse("= headline1\n== headline2\n= headline3");

        assertEquals(3, result.size());
        Iterator<AsciiDoctorHeadline> iterator = result.iterator();
        assertEquals(1, iterator.next().getDeep());
        assertEquals(2, iterator.next().getDeep());
        assertEquals(1, iterator.next().getDeep());
    }

}
