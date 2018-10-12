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

import static de.jcup.asciidoctoreditor.script.parser.AssertInlineAnchors.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctoreditor.script.AsciiDoctorInlineAnchor;
public class SimpleInlineAnchorParserTest {
	private SimpleInlineAnchorParser parserToTest;
	
	@Before
	public void before(){
		parserToTest=new SimpleInlineAnchorParser();
	}

	@Test
	public void inline_anchor_section_13() throws Exception {

		/* prepare */
		String text = "[[section-13]]";
		//.............01234567890 123456789012

		/* execute */
		List<AsciiDoctorInlineAnchor> result = parserToTest.parse(text);

		/* test */
		/* @formatter:off*/
		assertInlineAnchors(result).
			hasInlineAnchors(1).
			hasInlineAnchor("[[section-13]]").
				withId("section-13").
				withPosition(0).
				withEnd(13);
		/* @formatter:on*/

	}
	
	@Test
	public void parser_handles_2_inline_anchors() throws Exception {

		/* prepare */
		String text = "[[section-alpha-bravo]]\n==Headline\n\n[[section-delta-gamma]]\ntext\n[[wrong]";
		//.............01234567890123456789012 3456789012 3 456789012345678901234567 89012345

		/* execute */
		List<AsciiDoctorInlineAnchor> result = parserToTest.parse(text);

		/* test */
		/* @formatter:off*/
		assertInlineAnchors(result).
			hasInlineAnchors(2).
			hasInlineAnchor("[[section-alpha-bravo]]").
				withId("section-alpha-bravo").
				withPosition(0).
				withEnd(22).
			and().
			hasInlineAnchor("[[section-delta-gamma]]").
				withPosition(36).
				withEnd(58);
		/* @formatter:on*/

	}
	
	@Test
	public void custom_anchor_section_13() throws Exception {

		/* prepare */
		String text = "[#section-13]";
		//.............01234567890 123456789012

		/* execute */
		List<AsciiDoctorInlineAnchor> result = parserToTest.parse(text);

		/* test */
		/* @formatter:off*/
		assertInlineAnchors(result).
			hasInlineAnchors(1).
			hasInlineAnchor("[#section-13]").
				withId("section-13").
				withPosition(0).
				withEnd(12);
		/* @formatter:on*/

	}
	
	@Test
	public void parser_handles_2_custom_anchors() throws Exception {

		/* prepare */
		String text = "[#section-alpha-bravo]\n==Headline\n\n[#section-delta-gamma]\ntext\n[[wrong]";
		//.............01234567890123456789012 3456789012 3 456789012345678901234567 89012345

		/* execute */
		List<AsciiDoctorInlineAnchor> result = parserToTest.parse(text);

		/* test */
		/* @formatter:off*/
		assertInlineAnchors(result).
			hasInlineAnchors(2).
			hasInlineAnchor("[#section-alpha-bravo]").
				withId("section-alpha-bravo").
				withPosition(0).
				withEnd(21).
			and().
			hasInlineAnchor("[#section-delta-gamma]").
				withPosition(35).
				withEnd(56);
		/* @formatter:on*/

	}
	
}
