package de.jcup.asciidoctoreditor.document;

import static org.junit.Assert.*;

import org.junit.Test;

public class FormattedTextRecognizerTest {

	
	@Test
	public void alpha_bravo_is_not_recognized_for_start_x_end_y() {
		/* prepare */
		FormattedTextFinder recognizerToTest = new FormattedTextFinder("x","y");
		TestStringScanner scanner = new TestStringScanner("alpha bravo");
		
		/* execute */
		boolean found = recognizerToTest.isFound(scanner);
		
		/* test */
		assertFalse(found);
		assertEquals(0,scanner.pos);
		
	}
	
	@Test
	public void alpha_bravo_IS_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder recognizerToTest = new FormattedTextFinder("a","o");
		String text = "alpha bravo";
		TestStringScanner scanner = new TestStringScanner(text);
		
		/* execute */
		boolean found = recognizerToTest.isFound(scanner);
		
		/* test */
		assertTrue(found);
		assertEquals(text.length(),scanner.pos);
		
	}
	
	@Test
	public void x_alpha_bravo_IS_NOT_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder recognizerToTest = new FormattedTextFinder("a","o");
		String text = "x alpha bravo";
		TestStringScanner scanner = new TestStringScanner(text);
		
		/* execute */
		boolean found = recognizerToTest.isFound(scanner);
		
		/* test */
		assertFalse(found);
		assertEquals(0,scanner.pos);
		
	}
	
	@Test
	public void _alpha_bravo_IS_NOT_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder recognizerToTest = new FormattedTextFinder("a","o");
		String text = " alpha bravo";
		TestStringScanner scanner = new TestStringScanner(text);
		
		/* execute */
		boolean found = recognizerToTest.isFound(scanner);
		
		/* test */
		assertFalse(found);
		assertEquals(0,scanner.pos);
		
	}
	
	@Test
	public void a_lpha_bravo_is_NOT_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder recognizerToTest = new FormattedTextFinder("a","o");
		TestStringScanner scanner = new TestStringScanner("a lpha bravo");
		
		/* execute */
		boolean found = recognizerToTest.isFound(scanner);
		
		/* test */
		assertFalse(found);
		assertEquals(0,scanner.pos);
		
	}
	

	@Test
	public void alpha_brav_o_is_not_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder recognizerToTest = new FormattedTextFinder("a","o");
		TestStringScanner scanner = new TestStringScanner("alpha brav o");
		
		/* execute */
		boolean found = recognizerToTest.isFound(scanner);
		
		/* test */
		assertFalse(found);
		assertEquals(0,scanner.pos);
		
	}
	
	@Test
	public void a_lpha_brav_o_IS_NOT_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder recognizerToTest = new FormattedTextFinder("a","o");
		String text = "a lpha brav o";
		TestStringScanner scanner = new TestStringScanner(text);
		
		/* execute */
		boolean found = recognizerToTest.isFound(scanner);
		
		/* test */
		assertFalse(found);
		assertEquals(0,scanner.pos);
		
	}
	
	@Test
	public void alpha_bravoX_IS_NOT_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder recognizerToTest = new FormattedTextFinder("a","o");
		String text = "alpha bravoX";
		TestStringScanner scanner = new TestStringScanner(text);
		
		/* execute */
		boolean found = recognizerToTest.isFound(scanner);
		
		/* test */
		assertFalse(found);
		assertEquals(0,scanner.pos);
		
	}
	
	@Test
	public void prefixalpha_bravo_IS_NOT_recognized_for_start_a_end_o_when_pos_prefix_length() {
		/* prepare */
		FormattedTextFinder recognizerToTest = new FormattedTextFinder("a","o");
		String prefix = "prefix";
		String text = prefix+"alpha bravo";
		TestStringScanner scanner = new TestStringScanner(text);
		scanner.pos=prefix.length();
		
		/* execute */
		boolean found = recognizerToTest.isFound(scanner);
		
		/* test */
		assertFalse(found);
		assertEquals(0,scanner.pos);
		
	}
	
	@Test
	public void prefix_alpha_bravo_IS_NOT_recognized_for_start_a_end_o_when_pos_prefix_length() {
		/* prepare */
		FormattedTextFinder recognizerToTest = new FormattedTextFinder("a","o");
		String prefix = "prefix ";
		String text = prefix+"alpha bravo";
		TestStringScanner scanner = new TestStringScanner(text);
		scanner.pos=prefix.length();
		
		/* execute */
		boolean found = recognizerToTest.isFound(scanner);
		
		/* test */
		assertTrue(found);
		assertEquals(text.length(),scanner.pos);
		
	}
	
	@Test
	public void alpha_bravo_X_IS_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder recognizerToTest = new FormattedTextFinder("a","o");
		String text = "alpha bravo X";
		TestStringScanner scanner = new TestStringScanner(text);
		
		/* execute */
		boolean found = recognizerToTest.isFound(scanner);
		
		/* test */
		assertTrue(found);
		assertEquals(text.length()-2,scanner.pos);
		
	}
	
	@Test
	public void alpha_bravonLF_xyz_IS_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder recognizerToTest = new FormattedTextFinder("a","o");
		String text = "alpha bravo\nxyz";
		TestStringScanner scanner = new TestStringScanner(text);
		
		/* execute */
		boolean found = recognizerToTest.isFound(scanner);
		
		/* test */
		assertTrue(found);
		assertEquals(text.length()-4,scanner.pos);
		
	}
	
	
	@Test
	public void alpha_bravonLF_IS_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder recognizerToTest = new FormattedTextFinder("a","o");
		String text = "alpha bravo\n";
		TestStringScanner scanner = new TestStringScanner(text);
		
		/* execute */
		boolean found = recognizerToTest.isFound(scanner);
		
		/* test */
		assertTrue(found);
		assertEquals(text.length()-1,scanner.pos);
		
	}
	
	@Test
	public void alpha_bravonCRLF_IS_recognized_for_start_a_end_o() {
		/* prepare */
		FormattedTextFinder recognizerToTest = new FormattedTextFinder("a","o");
		String text = "alpha bravo\r\n";
		TestStringScanner scanner = new TestStringScanner(text);
		
		/* execute */
		boolean found = recognizerToTest.isFound(scanner);
		
		/* test */
		assertTrue(found);
		assertEquals(text.length()-2,scanner.pos);
		
	}

}
