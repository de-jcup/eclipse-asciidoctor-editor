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
package de.jcup.asciidoctoreditor;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class SimpleWordListBuilderTest {

	private SimpleWordListBuilder builderToTest;
	private List<String> listExpected;

	@Before
	public void before(){
		builderToTest = new SimpleWordListBuilder();
		listExpected=new ArrayList<>();
	}
	
	@Test
	public void a_colon_b_results_in_a_b() {
		assertEquals(expect("a","b").listExpected, builderToTest.build("a:b"));
	}
	
	@Test
	public void comment_results_in_empty_result() {
		assertEquals(expect().listExpected, builderToTest.build("#"));
	}
	
	@Test
	public void comment_alpha_is_reduced_to_alpha() {
		assertEquals(expect("alpha").listExpected, builderToTest.build("#alpha'"));
	}
	
	@Test
	public void single_quote_single_quote_results_in_empty_result() {
		assertEquals(expect().listExpected, builderToTest.build("''"));
	}
	
	@Test
	public void double_quote_double_quote_results_in_empty_result() {
		assertEquals(expect().listExpected, builderToTest.build("\"\""));
	}
	
	@Test
	public void double_quote_a_double_quote_results_in_a() {
		assertEquals(expect("a").listExpected, builderToTest.build("\"a\""));
	}
	
	@Test
	public void single_quote_a_single_quote_results_in_a() {
		assertEquals(expect("a").listExpected, builderToTest.build("'a'"));
	}
	
	@Test
	public void double_quote_a_space_b_double_quote_results_in_a_b() {
		assertEquals(expect("a","b").listExpected, builderToTest.build("\"a b\""));
	}
	
	@Test
	public void single_quote_a_space_b_single_quote_results_in_a_b() {
		assertEquals(expect("a","b").listExpected, builderToTest.build("'a b'"));
	}
	
	@Test
	public void a_space_666_results_in_a_666() {
		assertEquals(expect("a","666").listExpected, builderToTest.build("a 666"));
	}
	
	@Test
	public void var_equals_1__results_in_var_1() {
		assertEquals(expect("var","1").listExpected, builderToTest.build("var=1"));
	}
	
	@Test
	public void var_space_equals_1__results_in_var_1() {
		assertEquals(expect("var","1").listExpected, builderToTest.build("var =1"));
	}
	
	@Test
	public void var_equals_space_1__results_in_var_1() {
		assertEquals(expect("var","1").listExpected, builderToTest.build("var= 1"));
	}
	
	@Test
	public void var_space_equals_space_1__results_in_var_1() {
		assertEquals(expect("var","1").listExpected, builderToTest.build("var = 1"));
	}
	
	@Test
	public void albert_space_sarah__results_in_albert_sarah_in_list() {
		assertEquals(expect("albert","sarah").listExpected, builderToTest.build("albert sarah"));
	}
	
	@Test
	public void albert_qestion_sarah__results_in_albert_sarah_in_list() {
		assertEquals(expect("albert","sarah").listExpected, builderToTest.build("albert?sarah"));
	}
	
	@Test
	public void albert_space_space_tab_space_sarah__results_in_albert_sarah_in_list() {
		assertEquals(expect("albert","sarah").listExpected, builderToTest.build("albert  \t sarah"));
	}
	
	@Test
	public void test_open_backet_close_bracket_break__results_in_test_break() {
		assertEquals(expect("test","break").listExpected, builderToTest.build("test() break"));
	}
	
	@Test
	public void test_open_backet_xx_close_bracket_break__results_in_test_xx_break() {
		assertEquals(expect("test","xx","break").listExpected, builderToTest.build("test(xx) break"));
	}
	
	@Test
	public void albert_dot_sarah__results_in_albert_sarah_in_list() {
		assertEquals(expect("albert","sarah").listExpected, builderToTest.build("albert.sarah"));
	}
	
	@Test
	public void albert_commata_sarah__results_in_albert_sarah_in_list() {
		assertEquals(expect("albert","sarah").listExpected, builderToTest.build("albert,sarah"));
	}
	
	@Test
	public void albert_commata_space_sarah__results_in_albert_sarah_in_list() {
		assertEquals(expect("albert","sarah").listExpected, builderToTest.build("albert, sarah"));
	}
	
	@Test
	public void albert_dot_space_sarah__results_in_albert_sarah_in_list() {
		assertEquals(expect("albert","sarah").listExpected, builderToTest.build("albert. sarah"));
	}
	
	@Test
	public void albert_semicolon_space_sarah__results_in_albert_sarah_in_list() {
		assertEquals(expect("albert","sarah").listExpected, builderToTest.build("albert; sarah"));
	}

	private SimpleWordListBuilderTest expect(String ... strings){
		for (String string: strings){
			listExpected.add(string);
		}
		return this;
	}
}
