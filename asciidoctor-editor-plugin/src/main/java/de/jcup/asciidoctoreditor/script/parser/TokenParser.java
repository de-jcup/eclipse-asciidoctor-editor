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

import static de.jcup.asciidoctoreditor.script.parser.ParserState.*;

import java.util.ArrayList;
import java.util.List;

import de.jcup.asciidoctoreditor.script.parser.ParseContext.VariableContext;
import de.jcup.asciidoctoreditor.script.parser.ParseContext.VariableType;

public class TokenParser {

	static final char CHAR_STRING_SINGLE_APOSTROPHE = '\'';
	static final char CHAR_STRING_DOUBLE_APOSTROPHE = '\"';
	static final char CHAR_STRING_DOUBLE_TICKED = '`';
	private HereDocParserSupport hereDocParserSupport;
	private HereStringParserSupport hereStringParserSupport;

	public TokenParser() {
		hereDocParserSupport = new HereDocParserSupport();
		hereStringParserSupport = new HereStringParserSupport();
	}

	public List<ParseToken> parse(String asciidoctorScript) throws TokenParserException {
		if (asciidoctorScript == null) {
			return new ArrayList<>();
		}
		ParseContext context = new ParseContext();
		context.chars = asciidoctorScript.toCharArray();
		try {
			for (; context.hasValidPos(); context.moveForward()) {

				if (isVariableStateHandled(context)) {
					continue;
				}
				if (isCommentStateHandled(context)) {
					continue;
				}
				if (isStringStateHandled(context)) {
					continue;
				}
				if (isHereStringStateHandled(context)) {
					continue;
				}
				if (isHereDocStateHandled(context)) {
					continue;
				}

				handleNotVariableNorCommentOrString(context);
			}
			// add last token if existing
			context.addTokenAndResetText();
		} catch (RuntimeException e) {
			throw new TokenParserException("Was not able to parse script because of runtime error", e);
		}

		return context.tokens;
	}

	private boolean isHereStringStateHandled(ParseContext context) {
		return hereStringParserSupport.isHereStringStateHandled(context);
	}

	private boolean isHereDocStateHandled(ParseContext context) {
		return hereDocParserSupport.isHereDocStateHandled(context);
	}

	private boolean isStringStateHandled(ParseContext context) {
		char c = context.getCharAtPos();

		if (c == CHAR_STRING_SINGLE_APOSTROPHE) {
			return handleString(INSIDE_SINGLE_STRING, context, INSIDE_DOUBLE_TICKED, INSIDE_DOUBLE_STRING);
		}
		/* handle double string */
		if (c == CHAR_STRING_DOUBLE_APOSTROPHE) {
			return handleString(INSIDE_DOUBLE_STRING, context, INSIDE_DOUBLE_TICKED, INSIDE_SINGLE_STRING);
		}
		/* handle double ticked string */
		if (c == CHAR_STRING_DOUBLE_TICKED) {
			return handleString(INSIDE_DOUBLE_TICKED, context, INSIDE_SINGLE_STRING, INSIDE_DOUBLE_STRING);
		}
		if (context.insideString()) {
			context.appendCharToText();
			return true;
		}
		return false;
	}

	private boolean isCommentStateHandled(ParseContext context) {
		char c = context.getCharAtPos();

		if (context.inState(INSIDE_COMMENT)) {
			/* in comment state */
			if (c == '\n') {
				context.addTokenAndResetText();
				context.switchTo(CODE);
			} else {
				context.appendCharToText();
			}
			return true;
		}
		return false;
	}

	private boolean handleNotVariableNorCommentOrString(ParseContext context) {
		char c = context.getCharAtPos();
		if (c == '\r') {
			/*
			 * ignore - we only use \n inside the data parsed so we will handle
			 * easy \r\n and \n
			 */
			context.moveCurrentTokenPosWhenEmptyText();
			return true;
		}
		if (c == '\n') {
			context.addTokenAndResetText();
			if (context.inState(INSIDE_COMMENT)) {
				context.switchTo(CODE);
			}
			return true;
		}

		if (c == ';') {
			// special asciidoctor semicolon operator, separates only commands so
			// handle like a whitespace
			context.addTokenAndResetText();
			context.switchTo(CODE);
			return true;
		}
		if (c == '=') {
			// special assign operator
			context.appendCharToText();
			if (!context.inState(VARIABLE)) {
				context.addTokenAndResetText();
			}
			return true;
		}

		if (c == '{' || c == '}') {
			// block start/ end found, add as own token
			context.addTokenAndResetText();
			context.appendCharToText();
			context.addTokenAndResetText();
			context.switchTo(CODE);
			return true;
		}

		if (c == '#') {
			context.addTokenAndResetText();
			context.switchTo(INSIDE_COMMENT);
			context.appendCharToText();
			return true;
		}
		/*
		 * not inside a comment build token nor in string, so whitespaces are
		 * not necessary!
		 */
		if (Character.isWhitespace(c)) {
			context.addTokenAndResetText();
			return true;
		}
		/* otherwise simply add text */
		context.appendCharToText();
		return false;
	}

	/**
	 * A very simple variable handling: <br>
	 * We differt between:
	 * 
	 * <html>
	 * <table border='1'>
	 * <tr>
	 * <th>Expression</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * <tr>
	 * <td>$a</td>
	 * <td>STANDARD</td>
	 * <td>Termination by followed whitespace, string start, or special variable
	 * ending e.g. for $$</td>
	 * </tr>
	 * <tr>
	 * <td>$a['arrayname']</td>
	 * <td>STANDARD</td>
	 * <td>Termination by last balanced ']'</td>
	 * </tr>
	 * <tr>
	 * <td>${...}</td>
	 * <td>CURLY_BRACED</td>
	 * <td>Termination by last balanced '}'</td>
	 * </tr>
	 * <tr>
	 * <td>$(...)</td>
	 * <td>GROUPED</td>
	 * <td>Termination by last balanced ')'</td>
	 * </tr>
	 * </table>
	 * </html>
	 * 
	 * @param context
	 * @return
	 */
	private boolean isVariableStateHandled(ParseContext context) {
		char c = context.getCharAtPos();
		/*
		 * handle change to VARIABLE state when from CODE - comments and strings
		 * are ignored
		 */
		if (context.inState(CODE) || context.inState(INIT)) {
			if (c == '$') {
				context.addTokenAndResetText(); // $ is NOT appended at this
												// moment, so only stuff before
												// is inside new token
				// current token is now at wrong position because $ was ignored
				// (necessary but ugly)
				// so fix this now:
				context.currentToken.start--;
				context.appendCharToText();
				context.switchTo(VARIABLE);
				return true;
			}
			return false;
		}
		/* okay, other state */
		if (!context.inState(VARIABLE)) {
			/* not in variable state */
			return false;
		}

		/* in variable state */
		VariableContext variableContext = context.getVariableContext();
		VariableType type = variableContext.getType();
		if (type == null || type == VariableType.INITIAL) {
			return handleInitialVariableTypeDetermination(context, c);
		} else if (type == VariableType.GROUPED) {
			return handleGroupedVariable(context);
		} else if (type == VariableType.CURLY_BRACED) {
			return handleCurlyBracedVariable(context, c, variableContext);
		} else if (type == VariableType.STANDARD) {
			return handleStandardVariables(context, c, variableContext);
		} else {
			return false;
		}

	}

	private boolean handleInitialVariableTypeDetermination(ParseContext context, char c) {
		/*
		 * the state setting to VARIABLE is done on another method - this does
		 * only handle initial way
		 */
		if (!context.inState(ParserState.VARIABLE)) {
			return false;
		}
		VariableContext variableContext = context.getVariableContext();
		if (variableContext.getType() != VariableType.INITIAL) {
			return false;
		}
		
		/* at this point we are at INITIAL variable state - so if we got a string identifier as next 
		 * this means we must end the "variable" here (see bug 105)
		 */
		if (isStringChar(c)){
			context.addTokenAndResetText();
			context.switchTo(ParserState.CODE);
			return false;
		}
		
		context.appendCharToText();
		if (c == '$' || c == '?') {
			/*
			 * c is the NEXT char after the $ was recognized! as described at
			 * http://tldp.org/LDP/abs/html/special-chars.html "$$" is a special
			 * variable holding the process id so in this case it terminates the
			 * variable!
			 */
			context.addTokenAndResetText();
			context.switchTo(CODE);
			return true;
		} else if (c == '{') {
			variableContext.setType(VariableType.CURLY_BRACED);
			variableContext.incrementVariableOpenCurlyBraces();
			return true;
		} else if (c == '(') {
			variableContext.setType(VariableType.GROUPED);
			variableContext.variableGroupOpened();
			return true;
		} else {
			variableContext.setType(VariableType.STANDARD);
			return true;
		}
	}

	private boolean handleStandardVariables(ParseContext context, char c, VariableContext variableContext) {
		if (variableContext.getType() != VariableType.STANDARD) {
			return false;
		}
		if (c == '[') {
			variableContext.variableArrayOpened();
			context.appendCharToText();
			if (context.canMoveForward()) {
				context.moveForward();
			}
			moveUntilNextCharWillBeNoStringContent(context);
			return true;
		}
		if (c == ']') {
			variableContext.variableArrayClosed();
			context.appendCharToText();
			return true;
		}

		/* array variant */
		if (variableContext.isInsideVariableArray()) {
			if (isStringChar(c)) {
				context.appendCharToText();
				return true;
			}
			context.appendCharToText();
			return true;
		}
		/* normal variable or array closed */
		boolean balanced = isBalanced(variableContext);
		
		if (c == '}') {
			if (balanced || variableContext.hasNoOpenedCurlyBraces()) {
				/* this is a var separator - means end of variable def */
				context.addTokenAndResetText();
				context.switchTo(ParserState.CODE);
				/* no return, handle normal! */
				return false;
			}else{
				context.appendCharToText();
				return true;
			}
		}else if (Character.isWhitespace(c) || c == ';') {
			if (balanced) {
				context.addTokenAndResetText();
				context.switchTo(ParserState.CODE);
				return true;
			} else {
				context.appendCharToText();
				return true;
			}
		} else if (balanced && isStringChar(c)) {
			/* this is a string char - means end of variable def */
			context.addTokenAndResetText();
			context.switchTo(ParserState.CODE);
			/* no return, handle normal! */
			return false;

		} else if (balanced && isVarSeparator(c)) {
			/* this is a var separator - means end of variable def */
			context.addTokenAndResetText();
			context.switchTo(ParserState.CODE);
			/* no return, handle normal! */
			return false;

		} else {
			context.appendCharToText();
			return true;
		}

	}

	private boolean isVarSeparator(char c) {
		return c=='/' || c=='=';
	}

	private boolean handleCurlyBracedVariable(ParseContext context, char c, VariableContext variableContext) {
		if (variableContext.getType() != VariableType.CURLY_BRACED) {
			return false;
		}

		moveUntilNextCharWillBeNoStringContent(context);

		if (c == '{' || c == '}') {
			if (c == '{') {
				variableContext.incrementVariableOpenCurlyBraces();
			}
			if (c == '}') {
				variableContext.incrementVariableCloseCurlyBraces();
			}
			if (c == '}' && variableContext.areVariableCurlyBracesBalanced()) {
				context.addTokenAndResetText();
				context.switchTo(CODE);
			}
			return true;
		}
		return true;
	}

	private boolean handleGroupedVariable(ParseContext context) {
		VariableContext variableContext = context.getVariableContext();
		if (variableContext.getType() != VariableType.GROUPED) {
			return false;
		}
		moveUntilNextCharWillBeNoStringContent(context);
		char c = context.getCharAtPos();

		if (c == '(') {
			variableContext.variableGroupOpened();
			return true;
		} else if (c == ')') {
			variableContext.variableGroupClosed();
			if (variableContext.areVariableGroupsBalanced()) {
				context.addTokenAndResetText();
				context.switchTo(CODE);
			}
			return true;
		}
		return true;
	}

	private boolean isBalanced(VariableContext variableContext) {
		return variableContext.areVariableCurlyBracesBalanced() && variableContext.areVariableGroupsBalanced();
	}

	private boolean isStringChar(char c) {
		boolean isStringChar = c == '\"';
		isStringChar = isStringChar || c == '\'';
		isStringChar = isStringChar || c == '`';
		return isStringChar;
	}

	private boolean handleString(ParserState stringState, ParseContext context, ParserState... otherStringStates) {
		for (ParserState otherStringState : otherStringStates) {
			if (context.inState(otherStringState)) {
				/* inside other string - ignore */
				context.appendCharToText();
				return true;
			}

		}
		if (context.isCharBeforeEscapeSign()) {
			/* escaped */
			context.appendCharToText();
			return true;
		}
		if (context.inState(stringState)) {
			/* close single string */
			context.appendCharToText();
			context.restoreStateBeforeString();
			return true;
		}
		if (context.inState(ParserState.VARIABLE)) {
			context.appendCharToText();
			return true;
		}
		context.switchToStringState(stringState);
		context.appendCharToText();
		return true;

	}

	/**
	 * Situation: <br>
	 * 
	 * <pre>
	 * $('hello' a'x')
	     ^------  
	 * 012345678
	 * </pre>
	 * 
	 * Cursor is at a position after "$(". means index:2.<br>
	 * <br>
	 * 
	 * The method will now check if this is a string start, if so the complete
	 * content of string will be fetched and appended and pos changed. In the
	 * example above the postion will be 8 after execution and string of context
	 * will be <code>"$('hello'"</code>.
	 * 
	 */
	void moveUntilNextCharWillBeNoStringContent(ParseContext context) {
		context.appendCharToText();

		char c = context.getCharAtPos();
		if (!isStringChar(c)) {
			/*
			 * no string - do nothing, pos increment/move forward is done
			 * outside in for next loop!
			 */
			return;
		}
		if (context.isCharBeforeEscapeSign()) {
			return;
		}
		char stringCharToScan = c;
		moveToNextCharNotInStringAndAppendMovements(context, stringCharToScan);

	}

	private void moveToNextCharNotInStringAndAppendMovements(ParseContext context, char stringCharToScan) {
		if (!context.canMoveForward()) {
			return;
		}
		context.moveForward();
		context.appendCharToText();

		char c = context.getCharAtPos();
		if (c == stringCharToScan) {
			if (!context.isCharBeforeEscapeSign()) {
				/* found ending of string - so simply return */
				return;
			}
		}
		moveToNextCharNotInStringAndAppendMovements(context, stringCharToScan);
	}

}
