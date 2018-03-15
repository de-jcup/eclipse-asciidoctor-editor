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


/**
 * A special rule to scan asciidoctor variables
 * @author Albert Tregnaghi
 *
 */
public class AsciiDoctorVariableRule implements IPredicateRule {
	private enum State{
		INITIAL,
		NORMAL,
		CURLY_OPENED,
		GROUP_OPENED
	}
	
	private class ScanContext{
		State state;
		int curlyBracesOpened = 0;
		int groupOpened = 0;
		int curlyBracesClosed;
		int groupClosed;
		
		public boolean hasEndReached() {
			if (state==State.CURLY_OPENED){
				if (curlyBracesClosed==curlyBracesOpened){
					return true;
				}
				return false;
			}
			if (state==State.GROUP_OPENED){
				if (groupClosed==groupOpened){
					return true;
				}
				return false;
			}
			return false;
		}
	}
	private IToken token;

	public AsciiDoctorVariableRule(IToken token) {
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
		if (!isWordStart(start)) {
			scanner.unread();
			return Token.UNDEFINED;
		}
		ScanContext context = new ScanContext();
		context.state=State.INITIAL;
		
		/* okay is a variable, so read until end reached */
		do {
			int read = scanner.read(); // use int for EOF detection, char makes problems here!
			char c = (char) read;
			if (ICharacterScanner.EOF == read || (!isWordPart(c, context))) {
				scanner.unread();
				break;
			}
			if (context.hasEndReached()){
				break;
			}
			if (context.state==State.INITIAL){
				context.state=State.NORMAL;
			}
		} while (true);
		return getSuccessToken();
	}

	private boolean isWordStart(char c) {
		return c == '$';
	}

	// see http://tldp.org/LDP/abs/html/string-manipulation.html
	private boolean isWordPart(char c, ScanContext context) {
		if (c=='\n'){
			return false;
		}
		if (c == '{') {
			if (context.state==State.NORMAL){
				return false;
			}
			if (context.state==State.INITIAL){
				context.state=State.CURLY_OPENED;
			}
			context.curlyBracesOpened++;
			return true;
		}
		if (c=='}'){
			context.curlyBracesClosed++;
		}
		if (c == '(') {
			if (context.state==State.NORMAL){
				return false;
			}
			if (context.state==State.INITIAL){
				context.state=State.GROUP_OPENED;
			}
			context.groupOpened++;
			return true;
		}
		if (c==')'){
			context.groupClosed++;
		}
		if (context.state==State.GROUP_OPENED){
			return true;
		}
		if (context.state==State.CURLY_OPENED){
			return true;
		}
		/* curly braces/groups not opened! so we allow all except whitespaces */
		if (Character.isWhitespace(c)){
			return false;
		}
		if (c=='\'' || c=='\"' || c=='`'){
			/* e.g. on a $PID"-is interesting" */
			
			return false;
		}
		if (context.state==State.NORMAL && c=='/' ){
			/* e.g. on a $package/var/... */
			return false;
		}
		
		return true;
	}
}
