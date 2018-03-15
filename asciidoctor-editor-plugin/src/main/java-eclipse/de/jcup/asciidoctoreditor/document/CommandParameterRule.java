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
package de.jcup.asciidoctoreditor.document;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class CommandParameterRule implements IPredicateRule {

	private IToken token;

	public CommandParameterRule(IToken token) {
		this.token = token;
	}

	@Override
	public IToken getSuccessToken() {
		return token;
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner, false);
	}

	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		char start = (char) scanner.read();
		if ('-' !=start) {
			scanner.unread();
			return Token.UNDEFINED;
		}

		/* check if the former character is a space - if not this is not an argument */
		scanner.unread();
		scanner.unread();
		char beforeStart = (char) scanner.read();
		if (' ' !=beforeStart){
			/* no unread necessary, scanner is at start start*/
			return Token.UNDEFINED;
		}
		/* go after start again*/
		scanner.read();
		
		/* okay could be a parameter*/
		do {
			char c = (char) scanner.read();
			if (! isPartOfParameter(c)) {
				scanner.unread();
				break;
			}
		} while (true);
		return getSuccessToken();
	}

	private boolean isPartOfParameter(char c) {
		if (Character.isLetterOrDigit(c)){
			return true;
		}
		if ('-' == c){
			return true;
		}
		return false;
	}

}
