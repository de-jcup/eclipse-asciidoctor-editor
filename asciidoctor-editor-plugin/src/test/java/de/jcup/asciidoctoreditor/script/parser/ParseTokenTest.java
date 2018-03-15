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

import static org.junit.Assert.*;

import org.junit.Test;

import de.jcup.asciidoctoreditor.script.parser.ParseToken;

public class ParseTokenTest {
	
	@Test
	public void token_do_is_do() {
		assertTrue(new ParseToken("do").isDo());
	}

	@Test
	public void token_done_is_done() {
		assertTrue(new ParseToken("done").isDone());
	}

	@Test
	public void token_if_is_if() {
		assertTrue(new ParseToken("if").isIf());
	}

	@Test
	public void token_fi_is_fi() {
		assertTrue(new ParseToken("fi").isFi());
	}

	@Test
	public void token_starts_with_heredoc_is_heredoc() {
		assertTrue(new ParseToken("<< xyz").isHereDoc());
	}

	@Test
	public void token_starts_with_heredoc_is_no_herestring() {
		assertFalse(new ParseToken("<< xyz").isHereString());
	}

	@Test
	public void token_starts_with_herestring_is_no_heredoc() {
		assertFalse(new ParseToken("<<< xyz").isHereDoc());
	}

	@Test
	public void token_starts_with_herestring_is_herestring() {
		assertTrue(new ParseToken("<<< xyz").isHereString());
	}

	@Test
	public void token_starts_with_hash_space_xyz_is_comment() {
		assertTrue(new ParseToken("# xyz").isComment());
	}

	@Test
	public void token_starts_with_hash_xyz_is_comment() {
		assertTrue(new ParseToken("#xyz").isComment());
	}

	@Test
	public void token_starts_with_a_is_no_comment() {
		assertFalse(new ParseToken("axyz").isComment());
	}

	@Test
	public void single_string_xxx_is_string() {
		assertTrue(new ParseToken("'xxx'").isString());
	}

	@Test
	public void double_ticked_string_xxx_is_string() {
		assertTrue(new ParseToken("`xxx`").isString());
	}

	@Test
	public void double_string_xxx_is_string() {
		assertTrue(new ParseToken("\"xxx\"").isString());
	}

	@Test
	public void function_is_functionKeyword() {
		assertTrue(new ParseToken("function").isFunctionKeyword());
	}

	@Test
	public void functions_is_NOT_functionKeyword() {
		assertFalse(new ParseToken("headlines").isFunctionKeyword());
	}

	@Test
	public void function_is_NOT_functionName() {
		assertFalse(new ParseToken("function").isFunction());
	}

	@Test
	public void xyz_is_NOT_functionName() {
		assertFalse(new ParseToken("xyz").isFunction());
	}

	@Test
	public void xyz_followed_by_open_and_close_bracket_is_functionName() {
		assertTrue(new ParseToken("xyz()").isFunction());
	}

	@Test
	public void only_open_and_close_bracket_is_NOT_functionName() {
		assertFalse(new ParseToken("()").isFunction());
	}

	@Test
	public void function_params_equal_open_and_close_bracket_is_NOT_functionName() {
		assertFalse(new ParseToken("params=()").isFunction());
	}

	@Test
	public void xyz_getTextAsFunctionName_returns_xyz() {
		assertEquals("xyz", new ParseToken("xyz").getTextAsFunctionName());
	}

	@Test
	public void xyz_open_close_bracketgetTextAsFunctionName_returns_xyz() {
		assertEquals("xyz", new ParseToken("xyz()").getTextAsFunctionName());
	}
}