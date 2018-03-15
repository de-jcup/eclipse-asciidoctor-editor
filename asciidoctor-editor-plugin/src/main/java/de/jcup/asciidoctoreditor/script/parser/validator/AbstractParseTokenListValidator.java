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

import java.util.ArrayList;
import java.util.List;

import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptValidator;
import de.jcup.asciidoctoreditor.script.ValidationResult;
import de.jcup.asciidoctoreditor.script.parser.ParseToken;

public abstract class AbstractParseTokenListValidator implements AsciiDoctorScriptValidator<List<ParseToken>>{

	@Override
	public final List<ValidationResult> validate(List<ParseToken> toValidate) {
		List<ValidationResult> result = new ArrayList<ValidationResult>();
		if (toValidate==null || toValidate.size()==0){
			return result;
		}
		doValidation(toValidate, result);
		return result;
	}

	/**
	 * Do validation
	 * @param tokens - not <code>null</code> and not empty
	 * @param result - not <b>null</b>
	 */
	protected abstract void doValidation(List<ParseToken> tokens, List<ValidationResult> result);

}
