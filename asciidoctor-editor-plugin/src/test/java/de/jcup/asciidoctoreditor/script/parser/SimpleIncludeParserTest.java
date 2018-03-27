package de.jcup.asciidoctoreditor.script.parser;

import static de.jcup.asciidoctoreditor.script.parser.AssertIncludes.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctoreditor.script.AsciiDoctorInclude;
public class SimpleIncludeParserTest {
	private SimpleIncludeParser parserToTest;
	
	@Before
	public void before(){
		parserToTest=new SimpleIncludeParser();
	}

	@Test
	public void include_target_txt_found_include() throws Exception {

		/* prepare */
		String text = "include::label.txt";
		//.............01234567890 123456789012

		/* execute */
		List<AsciiDoctorInclude> result = parserToTest.parse(text);

		/* test */
		/* @formatter:off*/
		assertIncludes(result).
			hasIncludes(1).
			hasInclude("include::label.txt").
				withPosition(0).
				withEnd(17);
		/* @formatter:on*/

	}
	
	@Test
	public void include_target_txt_new_line_headline1_new_line_include_target2_txt_found_2includes() throws Exception {

		/* prepare */
		String text = "include::label.txt\n= headline1\ninclude::target2.txt";
		//.............01234567890123456789 012345678901 23456789012345678901

		/* execute */
		List<AsciiDoctorInclude> result = parserToTest.parse(text);

		/* test */
		/* @formatter:off*/
		assertIncludes(result).
			hasIncludes(2).
			hasInclude("include::label.txt").
				withPosition(0).
				withEnd(17).
			and().
			hasInclude("include::target2.txt").
				withPosition(31).
				withEnd(50);
		/* @formatter:on*/

	}
	
	
	@Test
	public void headline1_new_line_include_target_txt_found_include() throws Exception {

		/* prepare */
		String text = "headline1\ninclude::label.txt";
		//.............0123456789 0123456789012345678

		/* execute */
		List<AsciiDoctorInclude> result = parserToTest.parse(text);

		/* test */
		/* @formatter:off*/
		assertIncludes(result).
			hasIncludes(1).
			hasInclude("include::label.txt").
				withPosition(10).
				withEnd(27);
		/* @formatter:on*/

	}
	
}
