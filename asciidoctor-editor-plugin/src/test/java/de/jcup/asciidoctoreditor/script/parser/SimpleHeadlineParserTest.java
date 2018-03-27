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
	public void before(){
		parserToTest=new SimpleHeadlineParser();
	}

	@Test
	public void headline1_new_line_headline2__headline1_has_pos_0_headline2_pos_12() throws Exception {

		/* prepare */
		String text = "= headline1\n= headline2";
		//.............01234567890 123456789012

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
		//.............01234567890 12

		/* execute */
		List<AsciiDoctorHeadline> result = parserToTest.parse(text);

		/* test */
		assertHeadlines(result).hasHeadlines(2).hasHeadline("headline2").withDeep(1);

	}
	
	
		
	@Test
	public void empty_string_has_no_parser_results() {
		Collection<AsciiDoctorHeadline> result = parserToTest.parse("");
		
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void one_line_headline1__has_one_result_with_deep_1() {
		List<AsciiDoctorHeadline> result = parserToTest.parse("=headline1");
		
		assertHeadlines(result).hasHeadlines(1).hasHeadline("headline1").withDeep(1);
	}
	
	@Test
	public void one_line_space_headline1__has_one_result() {
		Collection<AsciiDoctorHeadline> result = parserToTest.parse("= headline1");
		
		assertEquals(1, result.size());
		AsciiDoctorHeadline headlineFound = result.iterator().next();
		assertEquals(1,headlineFound.getDeep());
		assertEquals("headline1",headlineFound.getName());
	}
	
	@Test
	public void three_headlines__has_3_result() {
		Collection<AsciiDoctorHeadline> result = parserToTest.parse("=headline1\n==headline2\n=headline3");
		
		assertEquals(3, result.size());
		Iterator<AsciiDoctorHeadline> iterator = result.iterator();
		assertEquals(1,iterator.next().getDeep());
		assertEquals(2,iterator.next().getDeep());
		assertEquals(1,iterator.next().getDeep());
	}

}
