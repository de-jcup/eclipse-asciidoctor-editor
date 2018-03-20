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
import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctoreditor.TestScriptLoader;
import de.jcup.asciidoctoreditor.script.AsciiDoctorError;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModel;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModelBuilder;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModelException;

public class AsciiDoctorScriptModelBuilderTest {

	private AsciiDoctorScriptModelBuilder builderToTest;

	@Before
	public void before() {
		builderToTest = new AsciiDoctorScriptModelBuilder();
	}

	@Test
	public void none_of_the_testscripts_contains_any_failure() throws Exception {
		/* prepare */
		StringBuilder errorCollector = new StringBuilder();
		List<String> scriptNames = TestScriptLoader.fetchAllTestScriptNames();
		for (String scriptName : scriptNames) {

			String script = TestScriptLoader.loadScriptFromTestScripts(scriptName);

			/* execute */
			try {
				AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(script);
				/* test */
				if (asciidoctorScriptModel.hasErrors()) {
					errorCollector.append("script file:").append(scriptName).append(" contains errors:\n");
					for (AsciiDoctorError error : asciidoctorScriptModel.getErrors()) {
						errorCollector.append("-");
						errorCollector.append(error.getMessage());
						errorCollector.append("\n");
					}
				}
			} catch (AsciiDoctorScriptModelException e) {
				/* test */
				errorCollector.append("script file:").append(scriptName).append(" contains errors:\n");
				errorCollector.append("-");
				Throwable root = e;
				while (root.getCause()!=null){
					root=root.getCause();
				}
				errorCollector.append("Root cause:"+root.getMessage());
				errorCollector.append("\n");
				
				root.printStackTrace();
			}

		}
		if (errorCollector.length() > 0) {
			fail(errorCollector.toString());
		}
	}

	@Test
	public void has_no_debugtoken_list__when_debug_is_turned_off_means_default() throws Exception {
		/* prepare */
		String script = "a b";

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(script);

		/* test */
		assertThat(asciidoctorScriptModel).hasNoDebugTokens();
	}

	@Test
	public void has_debugtoken_list___when_debug_is_turned_on() throws Exception {
		/* prepare */
		String script = "a b";
		builderToTest.setDebug(true);

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(script);

		/* test */
		assertThat(asciidoctorScriptModel).hasDebugTokens(2);
	}

	@Test
	public void bugfix_52_$x_followed_by_comment_line_with_if_results_in_no_error() throws Exception {
		/* prepare */
		String script = "a=$x\n# check if the host is pingable";

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(script);

		/* test */
		assertThat(asciidoctorScriptModel).hasErrors(0);
	}

	@Test
	public void bugfix_47_no_longer_errors_for_file() throws Exception {
		/* prepare */
		String script = TestScriptLoader.loadScriptFromTestScripts("bugfix_47.sh");

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(script);

		/* test */
		assertThat(asciidoctorScriptModel).hasErrors(0);
	}

	@Test
	public void bugfix_46_no_longer_errors_for_file() throws Exception {
		/* prepare */
		String script = TestScriptLoader.loadScriptFromTestScripts("bugfix_46.sh");

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(script);

		/* test */
		assertThat(asciidoctorScriptModel).hasErrors(0);
	}

	@Test
	public void bugfix_41_1_handle_arrays() throws Exception {
		/* prepare */
		String script = TestScriptLoader.loadScriptFromTestScripts("bugfix_41_1.sh");

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(script);

		/* test */
		assertThat(asciidoctorScriptModel).hasErrors(0);
	}

	@Test
	public void bugfix_41_2_handle_arrays() throws Exception {
		/* prepare */
		String script = TestScriptLoader.loadScriptFromTestScripts("bugfix_41_2.sh");

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(script);

		/* test */
		assertThat(asciidoctorScriptModel).hasErrors(0);
	}

	@Test
	public void bugfix_41_3_handle_arrays_simplified() throws Exception {
		/* prepare */
		String script = TestScriptLoader.loadScriptFromTestScripts("bugfix_41_3.sh");

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(script);

		/* test */
		assertThat(asciidoctorScriptModel).hasErrors(0);
	}

	@Test
	public void bugfix_39__variable_with_hash_do_not_result_errors() throws Exception {
		/* prepare */
		String code = "declare -A TitleMap\nif [ ${#TitleMap[*]} -eq 0 ]\nthen\n   displayerr \"Map is empty\"\n    exit 1\nfi";

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(code);

		/* test */
		assertThat(asciidoctorScriptModel).hasErrors(0);
	}

	@Test
	public void function_a_open_bracket_open_bracket_close_bracket_has_error() throws Exception {
		/* prepare */
		String code = "function a {{}";

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(code);

		/* test */
		assertThat(asciidoctorScriptModel).hasFunction("a").hasErrors(1);
	}

	@Test
	public void usage_space_x_msg_space_y_fatal_space_z() throws Exception {
		/* prepare */
		String code = "Usage () {x} Msg () {y} Fatal () {z}";

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(code);

		/* test */
		assertThat(asciidoctorScriptModel).hasFunction("Usage").hasFunction("Msg").hasFunction("Fatal").hasFunctions(3);
	}

	@Test
	public void usage_x_msg_y_fatal_z() throws Exception {
		/* prepare */
		String code = "Usage() {x} Msg() {y} Fatal() {z}";

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(code);

		/* test */
		assertThat(asciidoctorScriptModel).hasFunction("Usage").hasFunction("Msg").hasFunction("Fatal").hasFunctions(3);
	}

	@Test
	public void semicolon_function_xy_is_recognized_as_function_xy() throws Exception {
		/* prepare */
		String code = ";function xy{}";

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(code);

		/* test */
		assertThat(asciidoctorScriptModel).hasFunctions(1).hasFunction("xy").hasNoErrors();
	}

	@Test
	public void method_Usage_space_open_close_brackets__is_recognized_as_function_Usage() throws Exception {
		/* prepare */
		String code = "Usage () {}";

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(code);

		/* test */
		assertThat(asciidoctorScriptModel).hasFunctions(1).hasFunction("Usage");
	}

	@Test
	public void space_semicolon_function_xy_is_recognized_as_function_xy() throws Exception {
		/* prepare */
		String code = " ;function xy{}";

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(code);

		/* test */
		assertThat(asciidoctorScriptModel).hasFunctions(1).hasFunction("xy").hasNoErrors();
	}

	@Test
	public void space_semicolon_space_function_xy_is_recognized_as_function_xy() throws Exception {
		/* prepare */
		String code = " ; function xy{}";

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(code);

		/* test */
		assertThat(asciidoctorScriptModel).hasFunctions(1).hasFunction("xy").hasNoErrors();
	}

	@Test
	public void semicolon_space_function_xy_is_recognized_as_function_xy() throws Exception {
		/* prepare */
		String code = "; function xy{}";

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(code);

		/* test */
		assertThat(asciidoctorScriptModel).hasFunctions(1).hasFunction("xy").hasNoErrors();
	}

	@Test
	public void a_comments_with_function_is_not_handled_as_function() throws Exception {
		/* prepare */
		String code = "#\n# this function displays...\nfunction display {\n}";

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(code);

		/* test */
		assertThat(asciidoctorScriptModel).hasFunction("display").hasFunctions(1).hasNoErrors();
	}

	@Test
	/**
	 * AsciiDoctorWrapper does not support headlines inside headlines - so if somebody such
	 * things it's not allowed
	 */
	public void function_f1_has_only_open_bracket__must_have_no_function_but_two_error() throws Exception {
		/* prepare */
		String code = "function f1(){";

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(code);

		/* test */
		assertThat(asciidoctorScriptModel).hasNoFunctions().hasErrors(2); // function
																	// build has
																	// one error
																	// and one
																	// of the
																	// valdiators
																	// too
	}

	@Test
	/**
	 * AsciiDoctorWrapper does not support headlines inside headlines - so if somebody such
	 * things it's not allowed
	 */
	public void function_f1_containing_illegal_child_function_f1b__followed_by_function_f2__results_in_functions_f1_f2__only()
			throws Exception {
		/* prepare */
		String code = "function f1(){function f1b() {}} function f2 {}";

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(code);

		/* test */
		assertThat(asciidoctorScriptModel).hasFunctions(2).hasFunction("f1").hasFunction("f2").hasNoFunction("f1b");
	}

	@Test
	public void function_xyz_no_curly_brackets_is_not_recognized_as_function_and_has_an_error() throws Exception {
		/* prepare */
		String code = "function xy";

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(code);

		/* test */
		assertThat(asciidoctorScriptModel).hasNoFunctions().hasErrors(1);
	}

	@Test
	public void function_read_hyphen_file__curlyBrackets_open_close__is_recognized_as_function_read_hyphen_file__and_has_no_errors()
			throws Exception {
		/* prepare */
		String code = "function read-file{}";

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(code);

		/* test */
		assertThat(asciidoctorScriptModel).hasFunctions(1).hasFunction("read-file").hasNoErrors();
	}

	@Test
	public void function_read_hyphen_file_curlyBraceOpen_NewLine__content_NewLine_curlybraceClose_is_recognized_as_function_read_hyphen_file()
			throws Exception {
		/* prepare */
		String code = "function read-file{\n#something\n}";

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(code);

		/* test */
		assertThat(asciidoctorScriptModel).hasFunctions(1).hasFunction("read-file");
	}

	@Test
	public void function_read_hyphen_file_hypen_format_followed_with_brackets_is_recognized_as_function_read_hyphen_file_hypen_format()
			throws Exception {
		/* prepare */
		String code = "function read-file-format()\n{\n}";

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(code);

		/* test */
		assertThat(asciidoctorScriptModel).hasFunctions(1).hasFunction("read-file-format");
	}

	@Test
	public void function_read_hyphen_file_hypen_format_space_followed_with_brackets_is_recognized_as_function_read_hyphen_file_hypen_format()
			throws Exception {
		/* prepare */
		String code = "function read-file-format (){}";

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(code);

		/* test */
		assertThat(asciidoctorScriptModel).hasFunctions(1).hasFunction("read-file-format");
	}

	@Test
	public void an_empty_line_returns_not_null_AST() throws Exception {
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build("");
		assertNotNull(asciidoctorScriptModel);
	}

	@Test
	public void a_line_with_Xfunction_test_is_NOT_recognized() throws Exception {
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build("Xfunction test {}");
		/* test */
		assertThat(asciidoctorScriptModel).hasNoFunctions();

	}

	@Test
	public void a_line_with_method_having_underscores_is_correct_parsed() throws Exception {
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build("function show_something_else{}");
		/* test */
		assertThat(asciidoctorScriptModel).hasFunctions(1).hasFunction("show_something_else");
	}

	@Test
	public void a_line_with_function_test_is_recognized_and_returns_function_with_name_test() throws Exception {
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build("function test {}");
		/* test */
		assertThat(asciidoctorScriptModel).hasFunctions(1).hasFunction("test");
	}

	@Test
	public void two_lines_with_functions_test1_and_test2_are_recognized_and_returns_2_function_with_name_test1_and_teset2()
			throws Exception {
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest
				.build("function test1 {\n#something\n}\n #other line\n\nfunction test2 {\n#something else\n}\n");
		/* test */
		assertThat(asciidoctorScriptModel).hasFunction("test1").hasFunction("test2").hasFunctions(2);
	}

	@Test
	public void two_lines_with_functions_test1_and_test2_are_recognized_and_returns_2_function_with_name_test1_and_teset2__but_with_backslash_r()
			throws Exception {
		/* prepare */
		String asciidoctorScript = "function test1 {\n#something\n}\n #other line\n\nfunction test2 {\r\n#something else\r\n}\r\n";

		/* execute */
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build(asciidoctorScript);
		/* test */
		assertThat(asciidoctorScriptModel).hasNoErrors().hasFunction("test1").hasFunction("test2").hasFunctions(2);
	}

	@Test
	public void a_line_with_function_test_is_recognized_and_returns_function_with_pos_0_and_has_no_errors()
			throws Exception {
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build("function test {}");
		assertNotNull(asciidoctorScriptModel);

		/* test */
		assertThat(asciidoctorScriptModel).hasFunctions(1).hasFunctionWithPosition("test", 0).hasNoErrors();
	}

	@Test
	public void a_line_with_function_test_is_recognized_and_returns_function_with_pos_1_when_first_line_empty()
			throws Exception {
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build("\nfunction test {}");
		/* test */
		assertThat(asciidoctorScriptModel).hasFunctions(1).hasFunctionWithPosition("test", 1);
	}

	@Test
	public void a_line_with_5_spaces_and_function_test_is_recognized_and_returns_function_with_pos_5()
			throws Exception {
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build("     function test {}");
		/* test */
		assertThat(asciidoctorScriptModel).hasFunctions(1).hasFunctionWithPosition("test", 5);
	}

	@Test
	public void a_line_with_5_spaces_and_Xfunction_test_is_NOT_recognized() throws Exception {
		AsciiDoctorScriptModel asciidoctorScriptModel = builderToTest.build("     Xfunction test {}");
		/* test */
		assertThat(asciidoctorScriptModel).hasNoFunctions();

	}

}
