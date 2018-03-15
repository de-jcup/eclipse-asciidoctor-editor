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
 package de.jcup.asciidoctoreditor.script.parser.validator;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctoreditor.script.ValidationResult;
import de.jcup.asciidoctoreditor.script.parser.ParseToken;
import de.jcup.asciidoctoreditor.script.parser.TestParseToken;
import de.jcup.asciidoctoreditor.script.parser.validator.ClosedBlocksValidator;

public class ClosedBlocksValidatorTest {
	private ClosedBlocksValidator validatorToTest;
	private List<ParseToken> tokens;

	@Before
	public void before(){
		validatorToTest = new ClosedBlocksValidator();
		tokens = new ArrayList<>();
	}
	
	@Test
	public void missing_close_part_detected() {
		/* prepare */
		tokens.add(new TestParseToken("{"));
		tokens.add(new TestParseToken("{"));
		tokens.add(new TestParseToken("}"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(1,results.size());
	}
	
	@Test
	public void missing_open_part_detected() {
		/* prepare */
		tokens.add(new TestParseToken("{"));
		tokens.add(new TestParseToken("}"));
		tokens.add(new TestParseToken("}"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(1,results.size());
	}
	
	@Test
	public void no_missing_close_parts_have_no_error() {
		/* prepare */
		tokens.add(new TestParseToken("{"));
		tokens.add(new TestParseToken("{"));
		tokens.add(new TestParseToken("}"));
		tokens.add(new TestParseToken("}"));
		
		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);
		
		/* test */
		assertEquals(0,results.size());
	}

}
