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
package de.jcup.asciidoctoreditor.script.parser;

import static de.jcup.asciidoctoreditor.script.parser.AssertParseTokens.*;
import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctoreditor.script.parser.ParseContext;
import de.jcup.asciidoctoreditor.script.parser.ParseToken;
import de.jcup.asciidoctoreditor.script.parser.TokenParser;
import de.jcup.asciidoctoreditor.script.parser.TokenParserException;

public class TokenParserTest {

	private TokenParser parserToTest;

	@Before
	public void before() {
		parserToTest = new TokenParser();
	}

	@Test
	public void bug_106_cat_with_heredoc_followed_by_negative_and_string_throws_no_exception() throws Exception{
		assertParsing("cat <<-\" OF\"").simplyDoesNotFail();;
	}
	
	@Test
	public void bug_106_cat_with_heredoc_followed_by_negative_and_string_has_expected_tokens() throws Exception{
		assertParsing("cat <<-\" OF\"").resultsIn("cat","<<-\" OF\"");
	}
	
	@Test
	public void bug_105_$_directly_followed_by_simple_string() throws Exception {
		assertParsing("IFS=$'\n'").resultsIn("IFS=", "$","'\n'");
	}
	
	
	@Test
	public void bug_105_$_directly_followed_by_double_string() throws Exception {
		assertParsing("IFS=$\"\n\"").resultsIn("IFS=", "$","\"\n\"");
	}
	
	@Test
	public void bug_105_$_directly_followed_by_tick_string() throws Exception {
		assertParsing("IFS=$`\n`").resultsIn("IFS=", "$","`\n`");
	}
	
	@Test
	public void bug_105_$a_followed_by_simple_string__results_in_noe_token() throws Exception {
		assertParsing("IFS=$a'\n'").resultsIn("IFS=", "$a","'\n'");
	}
	@Test
	public void bug_105_$_a_followed_by_simple_string__results_in_noe_token() throws Exception {
		assertParsing("IFS=$_a'\n'").resultsIn("IFS=", "$_a","'\n'");
	}

	@Test
	public void bug_91_$v_slash_$_curlyb_x_array_$y_array_curly__returns_three_tokens() throws Exception {
		assertParsing("$v/${x[$y]}").resultsIn("$v", "/", "${x[$y]}");
	}

	@Test
	public void bug_91_$v_space_slash_space_$_curlyb_x_array_$y_array_space_curly__returns_three_tokens()
			throws Exception {
		assertParsing("$v / ${x[$y] }").resultsIn("$v", "/", "${x[$y] }");
	}

	@Test
	public void bug_91_$x_space_CurlyBrace_recognized_as_token_$x_and_token_curly_brace() throws Exception {
		assertParsing("$x }").resultsIn("$x", "}");
	}

	@Test
	public void bug_91_variable_with_slash_and_ending_curly_brace_results_in_three_tokens() throws Exception {
		assertParsing("$_retVals[${pair/=*/}]=${pair/*=/}").resultsIn("$_retVals[${pair/=*/}]", "=", "${pair/*=/}");
	}

	@Test
	public void bug_91_$xCurlyBrace_recognized_as_token_$x_and_token_curly_brace() throws Exception {
		// Origin problem was:
		// lm -'%.s-' {1..$x} # <-- this the reason! "$x}" makes the problem
		// ->"$x }" did make no problems!"
		assertParsing("$x}").resultsIn("$x", "}");
	}

	@Test
	public void herestring_found_when_herestring_a() throws Exception {
		assertParsing("<<<a").resultsIn("<<<a");
	}

	@Test
	public void herestring_found_when_herestring_a_nl_b() throws Exception {
		assertParsing("<<<a\nb").resultsIn("<<<a", "b");
	}

	@Test
	public void herestring_found_when_herestring_a_space_b() throws Exception {
		assertParsing("<<<a b").resultsIn("<<<a", "b");
	}

	@Test
	public void herestring_found_when_herestring_space_a() throws Exception {
		assertParsing("<<< a").resultsIn("<<<a");
	}

	@Test
	public void herestring_found_when_herestring_double_quote_a_double_quote() throws Exception {
		assertParsing("<<<\"a\"").resultsIn("<<<\"a\"");
	}

	@Test
	public void herestring_found_when_herestring_double_quote_a_double_quote_space_X() throws Exception {
		assertParsing("<<<\"a\" X").resultsIn("<<<\"a\"", "X");
	}

	@Test
	public void herestring_found_when_a_herestring_$text() throws Exception {
		assertParsing("a <<<$text").resultsIn("a", "<<<$text");
	}

	@Test
	public void herestring_found_when_a_herestring_space_$text() throws Exception {
		assertParsing("a <<< $text").resultsIn("a", "<<<$text");
	}

	@Test
	public void herestring_found_when_a_herestring_space_text_multilined_string_x() throws Exception {
		assertParsing("a <<< \"text\nmultilined\" x").resultsIn("a", "<<<\"text\nmultilined\"", "x");
	}

	@Test
	public void herestring_found_when_a_herestring_text_multilined_string_x() throws Exception {
		assertParsing("a <<<\"text\nmultilined\" x").resultsIn("a", "<<<\"text\nmultilined\"", "x");
	}

	@Test
	public void herestring_found_when_a_herestring_$text_nl_b_x() throws Exception {
		assertParsing("a <<<$text\nb x").resultsIn("a", "<<<$text", "b", "x");
	}

	@Test
	public void herestring_found_when_a_herestring_space_$text_nl_b() throws Exception {
		assertParsing("a <<< $text\nb").resultsIn("a", "<<<$text", "b");
	}

	@Test
	public void heredoc_found_when_a_heredoc_EOF_nl_b_space_hyphen_x_nl_EOF() throws Exception {
		assertParsing("a <<EOF\nb 'x\nEOF").resultsIn("a", "<<EOF", "b 'x", "EOF");
	}

	@Test
	public void herestring_found_when_a_herestring_EOF_nl_b_space_hyphen_x_nl_EOF() throws Exception {
		assertParsing("a <<<EOF\nb 'x\nEOF").resultsIn("a", "<<<EOF", "b", "'x\nEOF");
	}

	@Test
	public void heredoc_found_when_a_heredoc_negative_hyphen_EOF_hyphen_nl_b_space_hyphen_x_nl_EOF() throws Exception {
		assertParsing("a <<-'EOF'\nb 'x\nEOF").resultsIn("a", "<<-'EOF'", "b 'x", "EOF");
	}

	@Test
	public void bug_86_heredocs_found_when_cat_gt_ampersand2_space_heredoc_negative_hyphen_EOF_hyphen_nl_echo_hyphen_one_hyphen_but_no_problem_nlEOF()
			throws Exception {
		/* @formatter:off*/
		assertParsing("cat >&2 <<-'EOF'\necho 'one hyphen but no problem\nEOF").
		    resultsIn("cat", ">&2", "<<-'EOF'","echo 'one hyphen but no problem","EOF");
		/* @formatter:on*/
	}

	@Test
	public void bug_78_heredocs_xyz_bla_space_curlybracket_xyz_newline_test__contains_also_token_test_at_end()
			throws Exception {
		assertParsing("cat <<xyz\nbla {\nxyz\ntest").resultsIn("cat", "<<xyz", "bla {", "xyz", "test");
	}

	@Test
	public void bug_78_heredocs_space_xyz_bla_space_curlybracket_xyz__recognized_that_bla_space_curlybracket_is_one_single_token_only()
			throws Exception {
		assertParsing("cat << xyz\nbla {\nxyz").resultsIn("cat", "<<xyz", "bla {", "xyz");
	}

	@Test
	public void bug_78_heredocs_xyz_bla_space_curlybracket_xyz__recognized_that_bla_space_curlybracket_is_one_single_token_only()
			throws Exception {
		assertParsing("cat <<xyz\nbla {\nxyz").resultsIn("cat", "<<xyz", "bla {", "xyz");
	}

	/*
	 * http://tldp.org/LDP/abs/html/here-docs.html Example 19-7. Parameter
	 * substitution turned off
	 */
	@Test
	public void bug_78_heredocs_single_quote_xyz_single_quote_bla_space_curlybracket_xyz__recognized_that_bla_space_curlybracket_is_one_single_token_only()
			throws Exception {
		assertParsing("cat <<'xyz'\nbla {\nxyz").resultsIn("cat", "<<'xyz'", "bla {", "xyz");
	}

	/*
	 * http://tldp.org/LDP/abs/html/here-docs.html Example 19-7. Parameter
	 * substitution turned off
	 */
	@Test
	public void bug_78_heredocs_double_quote_xyz_double_quote_bla_space_curlybracket_xyz__recognized_that_bla_space_curlybracket_is_one_single_token_only()
			throws Exception {
		assertParsing("cat <<\"xyz\"\nbla {\nxyz").resultsIn("cat", "<<\"xyz\"", "bla {", "xyz");
	}

	/*
	 * http://tldp.org/LDP/abs/html/here-docs.html Example 19-4. Multi-line
	 * message, with tabs suppressed
	 */
	@Test
	public void bug_78_heredocs_hyphen_xyz_double_quote_bla_space_curlybracket_xyz__recognized_that_bla_space_curlybracket_is_one_single_token_only()
			throws Exception {
		assertParsing("cat <<-xyz\nbla {\nxyz").resultsIn("cat", "<<-xyz", "bla {", "xyz");
	}

	@Test
	public void bracket_bracket_1_plus_1_bracket_close_close__recognized() throws Exception {
		assertParsing("echo $((1+1))").resultsIn("echo", "$((1+1))");
	}

	@Test
	public void sbracket_sbracket_1_plus_1_sbracket_close_close__recognized() throws Exception {
		assertParsing("echo $[[1+1]]").resultsIn("echo", "$[[1+1]]");
	}

	@Test
	public void $1_has_start_0_end_2() throws Exception {
		/* prepare */
		String string = "$1";

		/* execute */
		List<ParseToken> tokens = parserToTest.parse(string);

		ParseToken token = assertThat(tokens).resolveToken("$1");
		assertEquals(0, token.getStart());
		assertEquals(2, token.getEnd());
	}

	@Test
	public void moveUntilNextCharWillBeNoStringContent_no_string_contend_handled_as_expected() throws Exception {
		/* prepare */
		ParseContext context = new ParseContext();
		context.chars = "$(tput 'STRING')".toCharArray();
		context.pos = 2;// at t(put)

		/* execute */
		parserToTest.moveUntilNextCharWillBeNoStringContent(context);
		context.moveForward(); // we must simulate the for next move forwarding!
		parserToTest.moveUntilNextCharWillBeNoStringContent(context);
		context.moveForward();
		parserToTest.moveUntilNextCharWillBeNoStringContent(context);
		context.moveForward();
		parserToTest.moveUntilNextCharWillBeNoStringContent(context);
		context.moveForward();

		/* test */
		assertEquals("tput", context.sb.toString());

		/* execute */
		parserToTest.moveUntilNextCharWillBeNoStringContent(context);
		context.moveForward();

		/* test */
		assertEquals("tput ", context.sb.toString());

		/* execute */
		parserToTest.moveUntilNextCharWillBeNoStringContent(context);
		context.moveForward();

		/* test */
		assertEquals("tput 'STRING'", context.sb.toString());
	}

	@Test
	public void moveUntilNextCharWillBeNoStringContent_tests() throws Exception {
		assertMoveUntilNextCharWillBeNoStringContent("$('nonsense ;-)'", 2, "'nonsense ;-)'", 15);
		assertMoveUntilNextCharWillBeNoStringContent("'abc'd", 0, "'abc'", 4);
		assertMoveUntilNextCharWillBeNoStringContent("'abc'd", 1, "a", 1);
		assertMoveUntilNextCharWillBeNoStringContent("('abc'd", 0, "(", 0);
		assertMoveUntilNextCharWillBeNoStringContent("('abc'd", 1, "'abc'", 5);
	}

	@Test
	public void moveUntilNextCharWillBeNoStringContent_tests_with_escaped_strings() throws Exception {
		assertMoveUntilNextCharWillBeNoStringContent("$('non\\\'sense ;-)'", 2, "'non\\\'sense ;-)'", 17);
	}

	@Test
	public void moveUntilNextCharWillBeNoStringContent_tests_with_other_stringtype_inside() throws Exception {
		assertMoveUntilNextCharWillBeNoStringContent("$('non\"sense ;-)'", 2, "'non\"sense ;-)'", 16);
	}

	private void assertMoveUntilNextCharWillBeNoStringContent(String code, int codePos, String expectedContent,
			int expectedNextPos) {
		/* prepare */
		ParseContext context = new ParseContext();
		context.chars = code.toCharArray();
		context.pos = codePos;

		/* execute */
		parserToTest.moveUntilNextCharWillBeNoStringContent(context);

		/* test */
		assertEquals(expectedContent, context.sb.toString());
		assertEquals(expectedNextPos, context.pos);
	}

	@Test
	public void a_variable_array_with_string_inside_and_escaped_string_char_having_bracket() throws Exception {
		assertParsing("$abc['\\\'nonsense]']").resultsIn("$abc['\\\'nonsense]']");
	}

	@Test
	public void a_variable_array_with_string_inside_having_bracket() throws Exception {
		assertParsing("$abc['nonsense]']").resultsIn("$abc['nonsense]']");
	}

	@Test
	public void a_variable_curly_braced_with_string_inside_having_curly_bracket() throws Exception {
		assertParsing("${'nonsense }'}").resultsIn("${'nonsense }'}");
	}

	@Test
	public void a_variable_group_with_string_inside_having_close_bracket_like_group() throws Exception {
		assertParsing("$('nonsense ;-)')").resultsIn("$('nonsense ;-)')");
	}

	@Test
	public void complex_variable_with_group() throws Exception {
		/* @formatter:off*/
		assertParsing("DIST=$(grep \"DISTRIB_ID\" /etc/lsb-release|awk -F \"=\" '{print $2}'|tr -d \"\\\"', \\n\")").
		    resultsIn(
		    		"DIST=",
					"$(grep \"DISTRIB_ID\" /etc/lsb-release|awk -F \"=\" '{print $2}'|tr -d \"\\\"', \\n\")"
		    		);
		/* @formatter:on*/
	}

	@Test
	public void bugfix_54_a_variable_having_braces_and_a_string_inside_is_closed_by_braces() throws Exception {
		assertParsing("BLACK=$(tput setaf 0 'STRING')").resultsIn("BLACK=", "$(tput setaf 0 'STRING')");
	}

	@Test
	public void bugfix_54_a_variable_having_braces_is_closed_by_braces() throws Exception {
		assertParsing("BLACK=$(tput setaf 0)").resultsIn("BLACK=", "$(tput setaf 0)");
	}

	@Test
	public void bugfix_41_3__variable_with_array_having_string_containing_space_recognized_correct() throws Exception {
		assertParsing("x=${y[`z1 z2`]}").resultsIn("x=", "${y[`z1 z2`]}");
	}

	@Test
	public void bugfix_47__$$_is_no_longer_a_problem() throws Exception {
		assertParsing("export DB2CLP=**$$**").resultsIn("export", "DB2CLP=", "**", "$$", "**");
	}

	@Test
	public void bugfix_46__variable_containing_multiple_curly_end_brackets_are_supported() throws Exception {
		assertParsing("${NAWK:=${awk:=awk}}").resultsIn("${NAWK:=${awk:=awk}}");
	}

	@Test
	public void bugfix_45() throws Exception {
		/* @formatter:off*/
		assertParsing("cd \"$(dirname \"$ASCIIDOCTOR_SOURCE\")\"\n\n# Check if the database exists").
		    resultsIn(
		    		"cd",
					"\"$(dirname \"",
					"$ASCIIDOCTOR_SOURCE",
					"\")\"",
					"# Check if the database exists"
		    		);
		/* @formatter:on*/
	}

	@Test
	public void bugfix_45_simplified() throws Exception {
		assertParsing("\"a\"$z\"b\" # x").resultsIn("\"a\"", "$z", "\"b\"", "# x");
	}

	@Test
	public void bugfix_43() throws Exception {
		assertParsing("alpha() { eval  \"a\"=${_e#*=} }").resultsIn("alpha()", "{", "eval", "\"a\"=", "${_e#*=}", "}");
	}

	@Test
	public void bugfix_41_2_handle_arrays() throws Exception {
		/* @formatter:off*/
		assertParsing("alpha() { a=${b[`id`]} } beta(){ }").
			resultsIn("alpha()", "{", "a=", "${b[`id`]}", "}", "beta()", "{", "}");
		/* @formatter:on*/
	}

	@Test
	public void $bracketPIDbracket_create_databaseDOTsql_is_recognizedas_two_tokens() throws Exception {
		assertParsing("${PID}_create_database.sql").resultsIn("${PID}", "_create_database.sql");
	}

	@Test
	public void echo_$myVar1_echo_$myVar2_parsed_correctly() throws Exception {
		assertParsing("echo $myVar1 echo $myVar2").resultsIn("echo", "$myVar1", "echo", "$myVar2");
	}

	@Test
	public void bugfix_39__a_variable_containing_hash_is_not_recognized_as_comment() throws Exception {
		/* prepare */
		String string = "if [ ${#TitleMap[*]} -eq 0 ]";

		/* execute */
		List<ParseToken> tokens = parserToTest.parse(string);

		/* test */
		assertThat(tokens).containsNotToken("#TitleMap[*]} -eq 0 ]");

	}

	@Test
	public void for_abc_10_newlines_x_token_x_has_position_13() throws Exception {
		/* prepare */
		String string = "abc\n\n\n\n\n\n\n\n\n\nx";

		/* execute */
		List<ParseToken> tokens = parserToTest.parse(string);

		/* test */
		assertThat(tokens).token("x").hasStart(13);

	}

	@Test
	public void for_a_cariage_return_newline_x__token_x_has_position_3() throws Exception {
		/* prepare */
		String string = "a\r\nx";
		System.out.println(string);
		/* execute */
		List<ParseToken> tokens = parserToTest.parse(string);

		/* test */
		assertThat(tokens).token("x").hasStart(3);

	}

	@Test
	public void for_ab_cariage_return_newline_x__token_x_has_position_4() throws Exception {
		/* prepare */
		String string = "ab\r\nx";
		System.out.println(string);
		/* execute */
		List<ParseToken> tokens = parserToTest.parse(string);

		/* test */
		assertThat(tokens).token("x").hasStart(4);

	}

	@Test
	public void for_ab_cariage_return_newline_cariage_return_newline_x__token_x_has_position_4() throws Exception {
		/* prepare */
		String string = "ab\r\n\r\nx";
		System.out.println(string);
		/* execute */
		List<ParseToken> tokens = parserToTest.parse(string);

		/* test */
		assertThat(tokens).token("x").hasStart(6);

	}

	@Test
	public void for_abc_10_cariage_return_newlines_x_token_x_has_position_13() throws Exception {
		/* prepare */
		String string = "abc\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\nx";
		System.out.println(string);
		/* execute */
		List<ParseToken> tokens = parserToTest.parse(string);

		/* test */
		assertThat(tokens).token("x").hasStart(23);

	}

	@Test
	public void for_abc__token_abc_has_pos_0() throws Exception {
		/* prepare */
		String string = "abc";

		/* execute */
		List<ParseToken> tokens = parserToTest.parse(string);

		/* test */
		assertThat(tokens).token("abc").hasStart(0);

	}

	@Test
	public void for_abc__token_abc_has_end_2() throws Exception {
		/* prepare */
		String string = "abc";

		/* execute */
		List<ParseToken> tokens = parserToTest.parse(string);

		/* test */
		assertThat(tokens).token("abc").hasEnd(3);

	}

	@Test
	public void for_space_abc__token_abc_has_pos_1() throws Exception {
		/* prepare */
		String string = " abc";

		/* execute */
		List<ParseToken> tokens = parserToTest.parse(string);

		/* test */
		assertThat(tokens).token("abc").hasStart(1);

	}

	@Test
	public void for_space_abc__token_abc_has_end_3() throws Exception {
		/* prepare */
		String string = " abc";

		/* execute */
		List<ParseToken> tokens = parserToTest.parse(string);

		/* test */
		assertThat(tokens).token("abc").hasEnd(4);

	}

	@Test
	public void token_abc_followed_by_open_curly_brace_results_in_two_tokens() throws Exception {
		assertParsing("abc{").resultsIn("abc", "{");
	}

	@Test
	public void token_abc_followed_by_close_curly_brace_results_in_two_tokens() throws Exception {
		assertParsing("abc}").resultsIn("abc", "}");
	}

	@Test
	public void token_abc_followed_by_open_and_close_curly_brace_results_in_three_tokens() throws Exception {
		assertParsing("abc{}").resultsIn("abc", "{", "}");
	}

	@Test
	public void semicolon_abc_results_in_token_abc_only() throws Exception {
		assertParsing(";abc").resultsIn("abc");
	}

	@Test
	public void semicolon_abc_semicolon_def_results_in_tokens_abc_and_def() throws Exception {
		assertParsing(";abc;def").resultsIn("abc", "def");
	}

	@Test
	public void semicolon_abc_space_def_results_in_tokens_abc_and_def() throws Exception {
		assertParsing(";abc def").resultsIn("abc", "def");
	}

	@Test
	public void do_x_do_y_done() throws Exception {
		/* prepare */
		String string = "do\nx\ndo\ny\ndone";

		/* execute */
		List<ParseToken> tokens = parserToTest.parse(string);

		/* test */
		assertThat(tokens).containsToken("do", 2).containsOneToken("x").containsOneToken("y").containsOneToken("done");
	}

	@Test
	public void do_x_done_do_y_done_do_done_done_do() throws Exception {
		/* prepare */
		String string = "do\nx\ndone\ndo\ny\ndone\n\ndo\ndone\ndone\ndo\n\n";

		/* execute */
		List<ParseToken> tokens = parserToTest.parse(string);

		/* test */
		/* @formatter:off*/
		assertThat(tokens).
			containsTokens("do","x","done","do","y","done","do","done","done","do");
		/* @formatter:on */
	}

	@Test
	public void do_x_done_do_y_done_do_done_done_do__with_backslash_r_inside() throws Exception {
		/* prepare */
		String string = "do\nx\ndone\ndo\r\ny\ndone\n\ndo\ndone\ndone\ndo\n\n";

		/* execute */
		List<ParseToken> tokens = parserToTest.parse(string);

		/* test */
		/* @formatter:off*/
		assertThat(tokens).
			containsTokens("do","x","done","do","y","done","do","done","done","do");
		/* @formatter:on */
	}

	@Test
	public void a_simple_string_containing_space_do_space_does_not_result_in_a_token_do() throws Exception {
		/* execute */
		List<ParseToken> tokens = parserToTest.parse("' do '");

		/* test */
		assertThat(tokens).containsNotToken("do");

	}

	@Test
	public void a_double_string_containing_space_do_space_does_not_result_in_a_token_do() throws Exception {
		/* execute */
		List<ParseToken> tokens = parserToTest.parse("\" do \"");

		/* test */
		assertThat(tokens).containsNotToken("do");
	}

	@Test
	public void a_double_ticked_string_containing_space_do_space_does_not_result_in_a_token_do() throws Exception {
		/* execute */
		List<ParseToken> tokens = parserToTest.parse("` do `");

		/* test */
		assertThat(tokens).containsNotToken("do");
	}

	@Test
	public void a_double_ticked_do_string_followed_by_space_and_do_does_result_in_do_token() throws Exception {
		/* execute */
		List<ParseToken> tokens = parserToTest.parse("`do` do `");

		/* test */
		assertThat(tokens).containsOneToken("do");

	}

	@Test
	public void a_double_ticked_do_string_followed_by_space_and_ESCAPE_and_do_does_result_in_NO_do_token()
			throws Exception {
		/* execute */
		List<ParseToken> tokens = parserToTest.parse("`do\\` do `");

		/* test */
		assertThat(tokens).containsNotToken("do");

	}

	@Test
	public void a_single_do_string_followed_by_space_and_ESCAPE_and_do_does_result_in_NO_do_token() throws Exception {
		/* execute */
		List<ParseToken> tokens = parserToTest.parse("'do\\' do '");

		/* test */
		assertThat(tokens).containsNotToken("do");

	}

	@Test
	public void a_double_do_string_followed_by_space_and_ESCAPE_and_do_does_result_in_NO_do_token() throws Exception {
		/* execute */
		List<ParseToken> tokens = parserToTest.parse("\"do\\\" do \"");

		/* test */
		assertThat(tokens).containsNotToken("do");

	}

	@Test
	public void a_double_string_containing_single_string_has_token_with_singlestring_contained() throws Exception {
		/* execute */
		List<ParseToken> tokens = parserToTest.parse("\" This is the 'way' it is \"");

		/* test */
		assertThat(tokens).containsOneToken("\" This is the 'way' it is \"");

	}

	@Test
	public void a_double_string_containing_double_ticked_string_has_token_with_singlestring_contained()
			throws Exception {
		/* execute */
		List<ParseToken> tokens = parserToTest.parse("\" This is the `way` it is \"");

		/* test */
		assertThat(tokens).containsOneToken("\" This is the `way` it is \"");

	}

	@Test
	public void a_single_string_containing_double_string_has_token_with_singlestring_contained() throws Exception {
		/* execute */
		List<ParseToken> tokens = parserToTest.parse("' This is the \\\"way\\\" it is '");

		/* test */
		assertThat(tokens).containsOneToken("' This is the \\\"way\\\" it is '");

	}

	@Test
	public void a_single_string_containing_double_ticked_string_has_token_with_singlestring_contained()
			throws Exception {
		/* execute */
		List<ParseToken> tokens = parserToTest.parse("' This is the `way` it is '");

		/* test */
		assertThat(tokens).containsOneToken("' This is the `way` it is '");

	}

	@Test
	public void abc_def_ghji_is_parsed_as_three_tokens() throws Exception {
		/* execute */
		List<ParseToken> tokens = parserToTest.parse("abc def ghji");

		/* test */
		assertNotNull(tokens);
		assertEquals(3, tokens.size());

		Iterator<ParseToken> it = tokens.iterator();
		ParseToken token1 = it.next();
		ParseToken token2 = it.next();
		ParseToken token3 = it.next();

		assertEquals("abc", token1.getText());
		assertEquals("def", token2.getText());
		assertEquals("ghji", token3.getText());
	}

	@Test
	public void some_spaces_abc_def_ghji_is_parsed_as_three_tokens() throws Exception {

		/* execute */
		List<ParseToken> tokens = parserToTest.parse("    abc def ghji");

		/* test */
		assertNotNull(tokens);
		assertEquals(3, tokens.size());

		Iterator<ParseToken> it = tokens.iterator();
		ParseToken token1 = it.next();
		ParseToken token2 = it.next();
		ParseToken token3 = it.next();

		assertEquals("abc", token1.getText());
		assertEquals("def", token2.getText());
		assertEquals("ghji", token3.getText());
	}

	@Test
	public void abc_def_ghji_is_parsed_as_three_tokens__and_correct_positions() throws Exception {
		/* execute */
		List<ParseToken> tokens = parserToTest.parse("abc def ghji");
		// ............................................01234567890

		/* test */
		assertThat(tokens).containsTokens("abc", "def", "ghji");
		assertThat(tokens).token("abc").hasStart(0);
		assertThat(tokens).token("def").hasStart(4);
		assertThat(tokens).token("ghji").hasStart(8);

	}

	@Test
	public void comment1_returns_one_tokens() throws Exception {
		List<ParseToken> tokens = parserToTest.parse("#comment1");

		assertThat(tokens).containsOneToken("#comment1");

	}

	@Test
	public void comment1_new_line_returns_one_tokens() throws Exception {
		List<ParseToken> tokens = parserToTest.parse("#comment1\n");

		assertThat(tokens).containsOneToken("#comment1");

	}

	@Test
	public void comment1_new_line_function_space_name_returns_3_tokens_comment1_function_and_name() throws Exception {
		List<ParseToken> tokens = parserToTest.parse("#comment1\nfunction name");

		assertThat(tokens).containsTokens("#comment1", "function", "name");
	}

	@Test
	public void comment1_new_line_function_space_name_directly_followed_by_brackets_returns_3_tokens_comment1_function_and_name()
			throws Exception {
		List<ParseToken> tokens = parserToTest.parse("#comment1\nfunction name()");

		assertThat(tokens).containsTokens("#comment1", "function", "name()");
	}

	/* -------------------------------------------------------------------- */
	/* --------------------------- Helpers -------------------------------- */
	/* -------------------------------------------------------------------- */
	private class AssertTokenParser {
		private String code;

		public AssertTokenParser(String code) {
			this.code = code;
		}

		public void resultsIn(String... expectedTokens) throws TokenParserException {
			List<ParseToken> tokens = parserToTest.parse(code);

			assertThat(tokens).containsTokens(expectedTokens);
		}
		
		public void simplyDoesNotFail() throws TokenParserException {
			parserToTest.parse(code);
		}

	}

	private AssertTokenParser assertParsing(String code) {
		return new AssertTokenParser(code);
	}

}
