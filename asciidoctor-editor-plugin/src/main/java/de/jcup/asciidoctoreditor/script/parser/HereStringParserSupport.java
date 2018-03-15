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
package de.jcup.asciidoctoreditor.script.parser;

public class HereStringParserSupport {

	public boolean isHereStringStateHandled(CodePosSupport codePosSupport) {

		HereStringContext context = createContext(codePosSupport);

		if (context.isNoHereStringFound()) {
			return false;
		}

		scanForContent(context);

		context.moveToNewEndPosition(context.hereStringPos);
		

		if (codePosSupport instanceof ParseContext) {
			/*
			 * when the support is a parse context we additional add new tokens
			 * into
			 */
			ParseContext parseContext = (ParseContext) codePosSupport;
			addHereStringToken(parseContext, context);
		}
		return true;
	}

	private void addHereStringToken(ParseContext parseContext, HereStringContext context) {
		ParseToken hereStringToken = new ParseToken();
		hereStringToken.start = context.hereStringTokenStart;
		hereStringToken.end = context.hereStringTokenEnd;
		hereStringToken.text = "<<<" + context.getContent();

		parseContext.addToken(hereStringToken);
	}

	private HereStringContext createContext(CodePosSupport codePosSupport) {

		HereStringContext context = new HereStringContext(codePosSupport);
		int hereStringTokenStart = context.getHereStringPos();

		Character init = context.getCharacterAtPosOrNull(hereStringTokenStart);
		if (init == null) {
			return context;
		}
		char c = init.charValue();
		if (c != '<') {
			return context;
		}
		/*
		 * CHECKPOINT 0: check if next is "<" as well.
		 */
		context.hereStringPos = hereStringTokenStart + 1;
		Character ca = context.getCharacterAtPosOrNull(context.hereStringPos++);
		if (ca == null) {
			return context;
		}
		if (ca.charValue() != '<') {
			return context;
		}
		/* CHECKPOINT 1:<< found */
		ca = context.getCharacterAtPosOrNull(context.hereStringPos++);
		if (ca == null) {
			return context;
		}
		if (ca.charValue() != '<') {
			return context;
		}
		/*
		 * CHECKPOINT 2:<<< found so this is a here-doc...
		 */
		// next line will mark also as initialized!
		context.hereStringTokenStart = hereStringTokenStart;
		context.setLastCharacter(ca);
		return context;

	}
	
	private void scanForContent(HereStringContext context) {
		/* CHECKPOINT 3: <<l now defined */
		context.stringIdentifier=null;
		context.firstCharExceptWhitespacesCheck=true;

		// scan for content
		context.partScan = new StringBuilder();
		context.content = new StringBuilder();
		
		do {
			Character contentChar = context.getCharacterAtPosOrNull(context.hereStringPos++);
			context.setLastCharacter(contentChar);
			if (contentChar == null) {
				break;
			}
			char charValue = contentChar.charValue();
			if (isHereStringTerminated(context)) {
				if (! Character.isWhitespace(charValue)){
					/* only add ending when no whitespace - e.g. " */
					context.partScan.append(charValue);
					
				}
				break;
			}
			if (Character.isWhitespace(charValue)) {
				/* not found - so add part scan to content */
				context.content.append(context.partScan);
				if (context.content.length() > 0) {
					context.content.append(charValue); /*
											 * add current whitespace too, when
											 * not at start
											 */
				}

				/* reset part scan */
				context.partScan = new StringBuilder();
			} else {
				context.partScan.append(charValue);
			}

		} while (true);
		
		/* Add last parts*/
		context.content.append(context.partScan);
		
		context.hereStringPos--;
		context.hereStringTokenEnd=context.hereStringPos;

	}

	private boolean isHereStringTerminated(HereStringContext context) {
		Character lastChar = context.getLastCharacter();
		if (lastChar==null){
			return true;
		}
		char charValue = lastChar.charValue();
		if (context.firstCharExceptWhitespacesCheck){
			/* initialize when no whitespace*/
			if (Character.isWhitespace(charValue)){
				return false;
			}
			context.firstCharExceptWhitespacesCheck=false;
			if (isStringIdentifier(charValue)){
				context.stringIdentifier=lastChar;
				/* first string identifier found - so this is a string part which is started now */
				return false;
			}
		}
		if (context.stringIdentifier==null){
			/* not inside string so just look at a whitespace...*/
			if (Character.isWhitespace(charValue)){
				return true;
			}
		}else if (charValue==context.stringIdentifier.charValue()){
			if (! context.isEscaped()){
				return true;
			}
			return false;
		}
			
		return false;
	}

	private boolean isStringIdentifier(char charValue) {
		/* @formatter:off*/
		return charValue==TokenParser.CHAR_STRING_DOUBLE_APOSTROPHE ||
			   charValue==TokenParser.CHAR_STRING_SINGLE_APOSTROPHE ||
			   charValue==TokenParser.CHAR_STRING_DOUBLE_TICKED
			   ;
		/* @formatter:on*/
	}


}
