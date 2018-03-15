package de.jcup.asciidoctoreditor.script.parser;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;

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
	public void empty_string_has_no_parser_results() {
		Collection<AsciiDoctorHeadline> result = parserToTest.parse("");
		
		assertNotNull(result);
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void one_line_headline1__has_one_result() {
		Collection<AsciiDoctorHeadline> result = parserToTest.parse("=headline1");
		
		assertEquals(1, result.size());
		AsciiDoctorHeadline headlineFound = result.iterator().next();
		assertEquals(1,headlineFound.getDeep());
		assertEquals("headline1",headlineFound.getName());
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
