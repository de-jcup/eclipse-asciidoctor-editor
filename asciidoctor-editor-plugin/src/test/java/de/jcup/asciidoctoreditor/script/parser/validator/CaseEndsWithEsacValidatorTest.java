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

import static de.jcup.asciidoctoreditor.script.parser.validator.AssertTokenValidator.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctoreditor.script.ValidationResult;
import de.jcup.asciidoctoreditor.script.parser.ParseToken;
import de.jcup.asciidoctoreditor.script.parser.TestParseToken;
import de.jcup.asciidoctoreditor.script.parser.validator.CaseEndsWithEsacValidator;
public class CaseEndsWithEsacValidatorTest {

	private CaseEndsWithEsacValidator validatorToTest;
	
	@Before
	public void before(){
		validatorToTest = new CaseEndsWithEsacValidator();
	}
	
	@Test
	public void esac_case_has_problem() {
		/* @formatter:off*/
		assertThat(validatorToTest).
			withTokens("esac","case").
		isNotValid().
		hasValidationErrors(1);
		/* @formatter:on*/
	}
	
	@Test
	public void case_esac_esac_has_problem() {
		/* @formatter:off*/
		assertThat(validatorToTest).
			withTokens("case","esac","case").
		isNotValid().
		hasValidationErrors(1);
		/* @formatter:on*/
	}
	
	@Test
	public void case_something_esac__has_no_problems() {
		/* @formatter:off*/
		assertThat(validatorToTest).
			withTokens("case","something","esac").
		isValid();
		/* @formatter:on*/
	}
	
	@Test
	public void case_something_casexne__has_problem() {
		/* @formatter:off*/
		assertThat(validatorToTest).
			withTokens("case","something","casexne").
		isNotValid().
		hasValidationErrors(1);
		/* @formatter:on*/
	}

	
	@Test
	public void case_something_has_problem() {
		/* @formatter:off*/
		assertThat(validatorToTest).
			withTokens("case","something").
		isNotValid().
		hasValidationErrors(1);
		/* @formatter:on*/
	}
	
	
	@Test
	public void case_something_case_something2_esac_has_problem() {
		/* @formatter:off*/
		assertThat(validatorToTest).
			withTokens("case","something","case","something2","esac").
		isNotValid().
		hasValidationErrors(1);
		/* @formatter:on*/
	}
	
	@Test
	public void case_something_esac_something2_esac_has_problem() {
		/* @formatter:off*/
		assertThat(validatorToTest).
			withTokens("case","something","esac","something2","esac").
		isNotValid().
		hasValidationErrors(1);
		/* @formatter:on*/
	}
	
	@Test
	public void case_x_esac_case_y_esac_case_esac_esac_case__has_problem() {
		/* @formatter:off*/
		assertThat(validatorToTest).
			withTokens("case","x","esac","case","y","esac","case","esac","case").
		isNotValid().
		hasValidationErrors(1);
		/* @formatter:on*/
	}
	
	@Test
	public void case_something_case_something2_esac_esac_has_no_problems() {
		/* @formatter:off*/
		assertThat(validatorToTest).
			withTokens("case","something","case","something2","esac","esac").
		isValid();
		/* @formatter:on*/
	}
	
	@Test
	public void case_something_simple_string_with_case_something2_esac_esac_has_problems() {
		/* @formatter:off*/
		assertThat(validatorToTest).
			withTokens("case","something","'case'","something2","esac","esac").
		isNotValid().
			hasValidationErrors(1);
		/* @formatter:on*/
	}
	
}
