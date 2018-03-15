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
package de.jcup.asciidoctoreditor.script.parser.validator;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptValidator;
import de.jcup.asciidoctoreditor.script.ValidationResult;
import de.jcup.asciidoctoreditor.script.parser.ParseToken;
import de.jcup.asciidoctoreditor.script.parser.TestParseToken;

public class AssertTokenValidator {

	private AsciiDoctorScriptValidator<List<ParseToken>> validator;
	private List<ParseToken> tokens;
	
	public static AssertTokenValidator assertThat(AsciiDoctorScriptValidator<List<ParseToken>> validator){
		return new AssertTokenValidator(validator);
	}

	private AssertTokenValidator(AsciiDoctorScriptValidator<List<ParseToken>> validator){
		assertNotNull(validator);
		this.validator=validator;
		this.tokens = new ArrayList<>();
	}
	
	public AssertTokenValidator withTokens(String ... tokens){
		for (String token: tokens){
			this.tokens.add(new TestParseToken(token));
		}
		return this;
	}
	
	public AssertTokenValidator isValid(){
		List<ValidationResult> results = validator.validate(tokens);
		if (! results.isEmpty()){
			StringBuilder sb = new StringBuilder();
			for (ValidationResult result: results){
				sb.append("\n");
				sb.append(result.getMessage());
			}
			fail("Expected NO validation failures/results but there are some:"+sb.toString());
		}
		return this;
	}
	
	public AssertTokenValidator isNotValid(){
		List<ValidationResult> results = validator.validate(tokens);
		if (results.isEmpty()){
			fail("Expected validation failures/results but there are none!");
		}
		return this;
	}

	public AssertTokenValidator hasValidationErrors(int expectedValidationResults) {
		List<ValidationResult> results = validator.validate(tokens);
		assertEquals("Validation amount not as expected!", expectedValidationResults, results.size());
		return this;
	}
	
}
