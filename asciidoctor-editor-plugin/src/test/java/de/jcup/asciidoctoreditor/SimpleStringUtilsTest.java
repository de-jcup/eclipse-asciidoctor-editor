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
 package de.jcup.asciidoctoreditor;

import static org.junit.Assert.*;

import org.junit.Test;

import de.jcup.asciidoctoreditor.SimpleStringUtils;

public class SimpleStringUtilsTest {

	@Test
	public void nextReducedVariable_from_string_alpha_beta_gamma_offset_0_is_alpha() {
		assertEquals("alpha", SimpleStringUtils.nextReducedVariableWord("alpha beta gamma", 0));
	}
	
	@Test
	public void nextReducedVariable_from_string_alpha_beta_gamma_offset_n1_is_empty() {
		assertEquals("", SimpleStringUtils.nextReducedVariableWord("alpha beta gamma",-1));
	}
	
	@Test
	public void nextReducedVariable_from_null_is_empty() {
		assertEquals("", SimpleStringUtils.nextReducedVariableWord(null,0));
	}
	
	@Test
	public void nextReducedVariable_from_string_alpha_beta_gamma_offset_1_is_alpha() {
		assertEquals("alpha", SimpleStringUtils.nextReducedVariableWord("alpha beta gamma", 1));
	}

	@Test
	public void nextReducedVariable_from_string_alpha_beta_gamma_offset_4_is_alpha() {
		//--------------------------------------------------------------0123456789012345
		assertEquals("alpha", SimpleStringUtils.nextReducedVariableWord("alpha beta gamma", 4));
	}
	
	@Test
	public void nextReducedVariable_from_string_alpha_beta_gamma_offset_6_is_beta() {
		//--------------------------------------------------------------0123456789012345
		assertEquals("beta", SimpleStringUtils.nextReducedVariableWord("alpha beta gamma", 6));
	}
	
	@Test
	public void nextReducedVariable_from_string_alpha_beta_gamma_offset_5_is_empty() {
		//--------------------------------------------------------------0123456789012345
		assertEquals("", SimpleStringUtils.nextReducedVariableWord("alpha beta gamma", 5));
	}
	
	@Test
	public void nextReducedVariable_from_string_alpha_beta_gamma_offset_9_is_beta() {
		//--------------------------------------------------------------0123456789012345
		assertEquals("beta", SimpleStringUtils.nextReducedVariableWord("alpha beta gamma", 9));
	}
	
	@Test
	public void nextReducedVariable_from_string_alpha_beta_gamma_offset_10_is_empty() {
		//----------------------------------------------------------0123456789012345
		assertEquals("", SimpleStringUtils.nextReducedVariableWord("alpha beta gamma", 10));
	}
	
	@Test
	public void nextReducedVariable_from_string_alpha_beta_gamma_offset_13_is_gamma() {
		assertEquals("gamma", SimpleStringUtils.nextReducedVariableWord("alpha beta gamma", 13));
	}
	
	@Test
	public void nextReducedVariable_from_string_alpha_beta_gamma_offset_15_is_gamma() {
		assertEquals("gamma", SimpleStringUtils.nextReducedVariableWord("alpha beta gamma", 15));
	}
	
	@Test
	public void nextReducedVariable_from_string_alpha_beta_gamma_offset_16_is_empty() {
		assertEquals("", SimpleStringUtils.nextReducedVariableWord("alpha beta gamma", 10));
	}
	
	@Test
	public void _ith_reducedVariableWordEndDetector_from_string_$PS1_is_double_quote_x_double_quote_offset_0_returns_$PS1() {
		assertEquals("$PS1", SimpleStringUtils.nextReducedVariableWord("$PS1=\"x\"", 0));
	}
	
	@Test
	public void _ith_reducedVariableWordEndDetector_from_string_$ASCIIDOCTOR_VERSIN_ARRAY_returns_$ASCIIDOCTOR_VERSIN() {
		assertEquals("$ASCIIDOCTOR_VERSIN", SimpleStringUtils.nextReducedVariableWord("$ASCIIDOCTOR_VERSIN[0]", 0));
	}
	
	@Test
	public void short_string_null_1_returns_empty_string() {
		assertEquals("", SimpleStringUtils.shortString(null, 1));
	}
	
	@Test
	public void short_string_a_0_returns_empty_string() {
		assertEquals("", SimpleStringUtils.shortString("a", 0));
	}
	
	@Test
	public void short_string_a_1_returns_a_string() {
		assertEquals("a", SimpleStringUtils.shortString("a", 1));
	}
	
	@Test
	public void short_string_a_2_returns_a_string() {
		assertEquals("a", SimpleStringUtils.shortString("a", 2));
	}
	
	@Test
	public void short_string_12345678901_10_returns_1234567_dot_dot_dot_string() {
		assertEquals("1234567...", SimpleStringUtils.shortString("12345678901", 10));
	}
	
	@Test
	public void short_string_1234567890_10_returns_1234567890_string() {
		assertEquals("1234567890", SimpleStringUtils.shortString("1234567890", 10));
	}

	@Test
	public void null_equals_null__is_true() {
		assertTrue(SimpleStringUtils.equals(null, null));
	}
	
	@Test
	public void a_equals_a__is_true() {
		assertTrue(SimpleStringUtils.equals("a", "a"));
	}
	
	@Test
	public void a_equals_null__is_false() {
		assertFalse(SimpleStringUtils.equals("a", null));
	}
	
	@Test
	public void a_equals_b__is_false() {
		assertFalse(SimpleStringUtils.equals("a","b"));
	}
	
	@Test
	public void b_equals_a__is_false() {
		assertFalse(SimpleStringUtils.equals("b","a"));
	}
	
	@Test
	public void null_equals_a__is_false() {
		assertFalse(SimpleStringUtils.equals(null,"a"));
	}

}
