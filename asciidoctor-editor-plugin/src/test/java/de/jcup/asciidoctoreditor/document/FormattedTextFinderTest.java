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
package de.jcup.asciidoctoreditor.document;

import static org.junit.Assert.*;

import org.junit.Test;

public class FormattedTextFinderTest {

	@Test
	public void bugfix_139__handle_formatted_parts_when_ended_by_expected_chars__must_NOT_be_found() {
		bugfix_139_must_be_found("a",0,false);
		bugfix_139_must_be_found("A",0,false);
		bugfix_139_must_be_found("ü",0,false);
	}
	
	
	@Test
	public void bugfix_139__handle_formatted_parts_when_ended_by_expected_chars__must_be_found() {
		bugfix_139_must_be_found(" ");
		bugfix_139_must_be_found("\n");
		
		bugfix_139_must_be_found(".");
		bugfix_139_must_be_found(",");
		bugfix_139_must_be_found(";");
		bugfix_139_must_be_found(":");
		bugfix_139_must_be_found("!");
		bugfix_139_must_be_found("?");
		bugfix_139_must_be_found("-");
	}
	
	private void bugfix_139_must_be_found(String terminator) {
		bugfix_139_must_be_found(terminator, 27,true);
	}
	private void bugfix_139_must_be_found(String terminator,int expectedPos, boolean mustBeFound) {
		// see https://github.com/de-jcup/eclipse-asciidoctor-editor/issues/139
		/* prepare */
		FormattedTextFinder finderToTest = new FormattedTextFinder("_","_");
		TestStringScanner scanner = new TestStringScanner("_Configure Global Security_"+terminator);
		
		/* execute */
		boolean found = finderToTest.isFound(scanner);
		
		/* test */
		assertEquals(mustBeFound,found);
		assertEquals(expectedPos,scanner.pos);
	}
	
	@Test
	public void alpha_bravo_is_not_recognized_for_start_x_end_y() {
		/* prepare */
		FormattedTextFinder finderToTest = new FormattedTextFinder("x","y");
		TestStringScanner scanner = new TestStringScanner("alpha bravo");
		
		/* execute */
		boolean found = finderToTest.isFound(scanner);
		
		/* test */
		assertFalse(found);
		assertEquals(0,scanner.pos);
		
	}
	
	@Test
	public void alpha_bravo_IS_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder finderToTest = new FormattedTextFinder("a","o");
		String text = "alpha bravo";
		TestStringScanner scanner = new TestStringScanner(text);
		
		/* execute */
		boolean found = finderToTest.isFound(scanner);
		
		/* test */
		assertTrue(found);
		assertEquals(text.length(),scanner.pos);
		
	}
	
	@Test
	public void x_alpha_bravo_IS_NOT_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder finderToTest = new FormattedTextFinder("a","o");
		String text = "x alpha bravo";
		TestStringScanner scanner = new TestStringScanner(text);
		
		/* execute */
		boolean found = finderToTest.isFound(scanner);
		
		/* test */
		assertFalse(found);
		assertEquals(0,scanner.pos);
		
	}
	
	@Test
	public void _alpha_bravo_IS_NOT_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder finderToTest = new FormattedTextFinder("a","o");
		String text = " alpha bravo";
		TestStringScanner scanner = new TestStringScanner(text);
		
		/* execute */
		boolean found = finderToTest.isFound(scanner);
		
		/* test */
		assertFalse(found);
		assertEquals(0,scanner.pos);
		
	}
	
	@Test
	public void a_lpha_bravo_is_NOT_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder finderToTest = new FormattedTextFinder("a","o");
		TestStringScanner scanner = new TestStringScanner("a lpha bravo");
		
		/* execute */
		boolean found = finderToTest.isFound(scanner);
		
		/* test */
		assertFalse(found);
		assertEquals(0,scanner.pos);
		
	}
	

	@Test
	public void alpha_brav_o_is_not_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder finderToTest = new FormattedTextFinder("a","o");
		TestStringScanner scanner = new TestStringScanner("alpha brav o");
		
		/* execute */
		boolean found = finderToTest.isFound(scanner);
		
		/* test */
		assertFalse(found);
		assertEquals(0,scanner.pos);
		
	}
	
	@Test
	public void a_lpha_brav_o_IS_NOT_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder finderToTest = new FormattedTextFinder("a","o");
		String text = "a lpha brav o";
		TestStringScanner scanner = new TestStringScanner(text);
		
		/* execute */
		boolean found = finderToTest.isFound(scanner);
		
		/* test */
		assertFalse(found);
		assertEquals(0,scanner.pos);
		
	}
	
	@Test
	public void alpha_bravoX_IS_NOT_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder finderToTest = new FormattedTextFinder("a","o");
		String text = "alpha bravoX";
		TestStringScanner scanner = new TestStringScanner(text);
		
		/* execute */
		boolean found = finderToTest.isFound(scanner);
		
		/* test */
		assertFalse(found);
		assertEquals(0,scanner.pos);
		
	}
	
	@Test
	public void prefixalpha_bravo_IS_NOT_recognized_for_start_a_end_o_when_pos_prefix_length() {
		/* prepare */
		FormattedTextFinder finderToTest = new FormattedTextFinder("a","o");
		String prefix = "prefix";
		String text = prefix+"alpha bravo";
		TestStringScanner scanner = new TestStringScanner(text);
		scanner.pos=prefix.length();
		
		/* execute */
		boolean found = finderToTest.isFound(scanner);
		
		/* test */
		assertFalse(found);
		assertEquals(0,scanner.pos);
		
	}
	
	@Test
	public void prefix_alpha_bravo_IS_NOT_recognized_for_start_a_end_o_when_pos_prefix_length() {
		/* prepare */
		FormattedTextFinder finderToTest = new FormattedTextFinder("a","o");
		String prefix = "prefix ";
		String text = prefix+"alpha bravo";
		TestStringScanner scanner = new TestStringScanner(text);
		scanner.pos=prefix.length();
		
		/* execute */
		boolean found = finderToTest.isFound(scanner);
		
		/* test */
		assertTrue(found);
		assertEquals(text.length(),scanner.pos);
		
	}
	
	@Test
	public void alpha_bravo_X_IS_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder finderToTest = new FormattedTextFinder("a","o");
		String text = "alpha bravo X";
		TestStringScanner scanner = new TestStringScanner(text);
		
		/* execute */
		boolean found = finderToTest.isFound(scanner);
		
		/* test */
		assertTrue(found);
		assertEquals(text.length()-2,scanner.pos);
		
	}
	
	@Test
	public void alpha_bravonLF_xyz_IS_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder finderToTest = new FormattedTextFinder("a","o");
		String text = "alpha bravo\nxyz";
		TestStringScanner scanner = new TestStringScanner(text);
		
		/* execute */
		boolean found = finderToTest.isFound(scanner);
		
		/* test */
		assertTrue(found);
		assertEquals(text.length()-4,scanner.pos);
		
	}
	
	
	@Test
	public void alpha_bravonLF_IS_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder finderToTest = new FormattedTextFinder("a","o");
		String text = "alpha bravo\n";
		TestStringScanner scanner = new TestStringScanner(text);
		
		/* execute */
		boolean found = finderToTest.isFound(scanner);
		
		/* test */
		assertTrue(found);
		assertEquals(text.length()-1,scanner.pos);
		
	}
	
	@Test
	public void alpha_bravonCRLF_IS_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder finderToTest = new FormattedTextFinder("a","o");
		String text = "alpha bravo\r\n";
		TestStringScanner scanner = new TestStringScanner(text);
		
		/* execute */
		boolean found = finderToTest.isFound(scanner);
		
		/* test */
		assertTrue(found);
		assertEquals(text.length()-2,scanner.pos);
		
	}

}
