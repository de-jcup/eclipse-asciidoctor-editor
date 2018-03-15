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
package de.jcup.asciidoctoreditor.script.parser;

import java.util.ArrayList;
import java.util.List;

class ParseContext implements CodePosSupport{

	char[] chars;
	int pos;
	StringBuilder sb;
	List<ParseToken> tokens = new ArrayList<ParseToken>();
	ParseToken currentToken;
	private ParserState parserState = ParserState.INIT;
	private ParserState stateBeforeString;
	private VariableContext variableContext;

	ParseContext() {
		currentToken = createToken();
	}

	public enum VariableType {
		/**
		 * Initial - no type defined
		 */
		INITIAL,

		/**
		 * Standard variable definition, maybe with array usage
		 */
		STANDARD,

		/**
		 * Something like $(....), so everything inside is part of token,
		 * termination is recognized by corresponding ). Check for balance
		 * necessary
		 */
		GROUPED,

		/**
		 * Something like ${....}, so everything inside is part of token,
		 * termination is recognized by corresponding }, * Check for balance
		 * necessary
		 */
		CURLY_BRACED
	}

	public class VariableContext {

		private VariableState variableState = VariableState.NO_ARRAY;
		private VariableType type;
		private int variableOpenCurlyBraces;
		private int variableCloseCurlyBraces;
		private int variableGroupOpen;
		private int variableGroupClosed;

		public void incrementVariableOpenCurlyBraces() {
			variableOpenCurlyBraces++;

		}

		public VariableType getType() {
			return type;
		}

		public void setType(VariableType type) {
			this.type = type;
		}

		public void incrementVariableCloseCurlyBraces() {
			variableCloseCurlyBraces++;

		}

		public boolean areVariableCurlyBracesBalanced() {
			return variableOpenCurlyBraces == variableCloseCurlyBraces;
		}

		public void variableArrayOpened() {
			variableState = VariableState.ARRAY_OPENED;
		}

		public void variableArrayClosed() {
			variableState = VariableState.ARRAY_CLOSED;
		}

		public boolean isInsideVariableArray() {
			boolean isInside = inState(ParserState.VARIABLE);
			isInside = isInside && VariableState.ARRAY_OPENED.equals(variableState);
			return isInside;
		}

		public void variableGroupOpened() {
			variableGroupOpen++;
		}

		public void variableGroupClosed() {
			variableGroupClosed++;
		}

		public boolean areVariableGroupsBalanced() {
			return variableGroupOpen == variableGroupClosed;
		}

		public boolean hasNoOpenedCurlyBraces() {
			return variableOpenCurlyBraces==0;
		}
	}
	
	void addTokenAndResetText() {
		if (moveCurrentTokenPosWhenEmptyText()) {
			return;
		}
		
		currentToken.text = sb.toString();
		currentToken.end = pos;
		tokens.add(currentToken);

		/* new token on next position */
		currentToken = createToken();
		currentToken.start = pos + 1;

		resetText();
	}
	
	void addToken(ParseToken token){
		tokens.add(token);
	}

	void appendCharToText() {
		getSb().append(getCharAtPos());
	}

	char getCharAtPos() {
		return chars[pos];
	}
	
	public int getInitialStartPos() {
		return pos;
	}
	
	char getCharBefore() {
		Character c = getCharacterAtPosOrNull(pos-1);
		if (c==null){
			return 0;
		}
		return c.charValue();
	}
	
	/**
	 * Get character from wanted position
	 * @param wantedPos
	 * @return character or <code>null</code> if not available
	 */
	public Character getCharacterAtPosOrNull(int wantedPos) {
		if (wantedPos >= 0) {
			int length = chars.length;
			if (length > wantedPos) {
				return chars[wantedPos];
			}
		}
		return null;
	}

	boolean insideString() {
		boolean inString = false;
		inString = inString || inState(ParserState.INSIDE_DOUBLE_STRING);
		inString = inString || inState(ParserState.INSIDE_DOUBLE_TICKED);
		inString = inString || inState(ParserState.INSIDE_SINGLE_STRING);
		return inString;
	}

	boolean inState(ParserState parserState) {
		return getState().equals(parserState);
	}

	boolean moveCurrentTokenPosWhenEmptyText() {
		if (getSb().length() == 0) {
			currentToken.start++;
			return true;
		}
		return false;
	}

	void restoreStateBeforeString() {
		switchTo(stateBeforeString);
	}

	void switchTo(ParserState parserState) {
		this.parserState = parserState;
		if (ParserState.VARIABLE.equals(parserState)) {
			getVariableContext().variableState = VariableState.NO_ARRAY;
			getVariableContext().setType(VariableType.INITIAL);
		} else {
			variableContext = null;
		}
	}

	void switchToStringState(ParserState newStringState) {
		this.stateBeforeString = getState();
		switchTo(newStringState);
	}

	private ParseToken createToken() {
		ParseToken token = new ParseToken();
		token.start = pos;
		return token;
	}

	private StringBuilder getSb() {
		if (sb == null) {
			sb = new StringBuilder();
		}
		return sb;
	}

	private ParserState getState() {
		if (parserState == null) {
			parserState = ParserState.UNKNOWN;
		}
		return parserState;
	}

	private void resetText() {
		sb = null;
	}

	public VariableContext getVariableContext() {
		if (variableContext == null) {
			variableContext = new VariableContext();
		}
		return variableContext;
	}

	@Override
	public String toString() {
		return "ParseContext:" + getSb().toString() + "\nTokens:" + tokens;
	}

	public boolean hasValidPos() {
		return pos < chars.length;
	}

	public void moveForward() {
		pos++;
	}

	public boolean canMoveForward() {
		return pos < chars.length-1;
	}

	public boolean isCharBeforeEscapeSign() {
		return getCharBefore() == '\\';
	}

	public void moveBackWard() {
		pos--;
		
	}

	public void moveToPos(int pos) {
		this.pos=pos;
	}

	

}