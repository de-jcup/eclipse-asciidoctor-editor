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

import java.util.List;

import de.jcup.asciidoctoreditor.script.AsciiDoctorError;
import de.jcup.asciidoctoreditor.script.ValidationResult;
import de.jcup.asciidoctoreditor.script.parser.ParseToken;

/**
 * An abstract valdidator suitable as base for validation of start statements
 * needing a closing one
 * 
 * @author Albert Tregnaghi
 *
 */
public abstract class AbstractFindMissingEndStatementsValidator extends AbstractParseTokenListValidator {

	@Override
	protected final void doValidation(List<ParseToken> tokens, List<ValidationResult> result) {
		ParseToken inspectedUnchainedStartToken = null;
		int countOfStartTokens = 0;
		int countOfCloseTokens = 0;
		for (ParseToken token : tokens) {
			if (token == null) {
				continue;
			}
			if (inspectedUnchainedStartToken == null) {
				inspectedUnchainedStartToken = token;
			}
			if (isStartToken(token)) {
				if (countOfStartTokens == countOfCloseTokens) {
					/*
					 * former start statement was closed - so set this token as
					 * last inspected unchained token
					 */
					inspectedUnchainedStartToken = token;
				}
				countOfStartTokens++;
			} else if (isCloseToken(token)) {
				if (countOfStartTokens > 0) {
					countOfCloseTokens++;
				}
			}
		}
		if (countOfStartTokens != countOfCloseTokens) {
			if (inspectedUnchainedStartToken != null) {
				String message = createMissingCloseTokenMessage();
				AsciiDoctorError error = new AsciiDoctorError(inspectedUnchainedStartToken.getStart(),
						inspectedUnchainedStartToken.getEnd(), message);
				result.add(error);
			}
		}
	}

	/**
	 * Implementation checks if given token is a start token.
	 * 
	 * @param token
	 * @return <code>true</code> when the token is a start token
	 */
	protected abstract boolean isStartToken(ParseToken token);

	/**
	 * Implementation checks if given token is a closing token for start token.
	 * 
	 * @param token
	 * @return <code>true</code> when the token is a closing token of start
	 *         token
	 */
	protected abstract boolean isCloseToken(ParseToken token);

	/**
	 * Implementation creates a string for message about missing close token
	 * @return message
	 */
	protected abstract String createMissingCloseTokenMessage();
}
