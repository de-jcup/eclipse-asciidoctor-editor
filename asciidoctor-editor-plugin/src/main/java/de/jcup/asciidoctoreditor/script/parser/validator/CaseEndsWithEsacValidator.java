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

import de.jcup.asciidoctoreditor.script.parser.ParseToken;

public class CaseEndsWithEsacValidator extends AbstractFindMissingEndStatementsValidator{
	
	protected String createMissingCloseTokenMessage() {
		return "This 'case' is not correct closed. A 'esac' is missing";
	}

	protected boolean isStartToken(ParseToken token) {
		return token.isCase();
	}

	protected boolean isCloseToken(ParseToken token) {
		return token.isEsac();
	}

}
