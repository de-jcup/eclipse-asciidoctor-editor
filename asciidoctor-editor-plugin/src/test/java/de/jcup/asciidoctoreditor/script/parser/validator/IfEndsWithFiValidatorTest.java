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
import de.jcup.asciidoctoreditor.script.parser.validator.IfEndsWithFiValidator;

public class IfEndsWithFiValidatorTest {

	private IfEndsWithFiValidator validatorToTest;
	private ArrayList<ParseToken> tokens;

	@Before
	public void before() {
		validatorToTest = new IfEndsWithFiValidator();
		tokens = new ArrayList<>();
	}
	

	@Test
	public void if_something_fi_has_no_problems() {
		/* prepare */
		tokens.add(new TestParseToken("if"));
		tokens.add(new TestParseToken("something"));
		tokens.add(new TestParseToken("fi"));

		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);

		/* test */
		assertEquals(0, results.size());
	}

	@Test
	public void if_something_fine_has_problem() {
		/* prepare */
		tokens.add(new TestParseToken("if"));
		tokens.add(new TestParseToken("something"));
		tokens.add(new TestParseToken("fine"));

		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);

		/* test */
		assertEquals(1, results.size());
		ValidationResult validationResult = results.iterator().next();
		assertEquals(ValidationResult.Type.ERROR, validationResult.getType());
	}

	@Test
	public void if_something_if_something2_fine_has_problem() {
		/* prepare */
		tokens.add(new TestParseToken("if"));
		tokens.add(new TestParseToken("something"));
		tokens.add(new TestParseToken("if"));
		tokens.add(new TestParseToken("fi"));	

		/* execute */
		List<ValidationResult> results = validatorToTest.validate(tokens);

		/* test */
		assertEquals(1, results.size());
		ValidationResult validationResult = results.iterator().next();
		assertEquals(ValidationResult.Type.ERROR, validationResult.getType());
	}
}
