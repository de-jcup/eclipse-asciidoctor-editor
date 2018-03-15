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

public class HereDocParserSupport {

	public boolean isHereDocStateHandled(CodePosSupport codePosSupport) {

		HereDocContext context= createContext(codePosSupport);
		
		if (context.isNoHereDocFound()){
			return false;
		}
		
		step1_scanForLiteral(context);
		if (context.hasNoLiteral()){
			return false;
		}

		step2_scanForContent(context);
		if (! context.isHereDocValid()){
			return false;
		}
		context.moveToNewEndPosition(context.hereDocPos);
		
		if (codePosSupport instanceof ParseContext){
			/* when the support is a parse context we additional add 
			 * new tokens into
			 */
			ParseContext parseContext = (ParseContext) codePosSupport;
			addHereDocTokens(parseContext, context);
		}
		return true;
	}

	private void addHereDocTokens(ParseContext parseContext, HereDocContext context) {
		ParseToken hereDocToken = new ParseToken();
		hereDocToken.start = context.hereDocTokenStart;
		hereDocToken.end = context.hereDocTokenEnd;
		hereDocToken.text = "<<" + context.getLiteral();

		parseContext.addToken(hereDocToken);

		ParseToken contentToken = new ParseToken();
		contentToken.start = context.contentTokenStart;
		contentToken.end = context.contentTokenEnd;
		contentToken.text=context.getContent();

		parseContext.addToken(contentToken);

		ParseToken closingLiteralToken = new ParseToken();
		closingLiteralToken.start = context.closingLiteralTokenStart;
		closingLiteralToken.end = context.closingLiteralTokenEnd;
		closingLiteralToken.text = context.partScan.toString();

		parseContext.addToken(closingLiteralToken);
	}
	
	private void step2_scanForContent(HereDocContext context) {
		/* CHECKPOINT 3: <<literal now defined */
		context.endliteralFound = false;

		// scan for content
		context.partScan = new StringBuilder();
		context.content = new StringBuilder();
		context.contentTokenStart = context.hereDocPos;
		
		do {
			if (isEndLiteralFound(context.getLiteral(), context.partScan)) {
				context.endliteralFound = true;
				context.closingLiteralTokenEnd = context.hereDocPos;
				break;
			}
			Character contentChar = context.getCharacterAtPosOrNull(context.hereDocPos++);
			if (contentChar == null) {
				break;
			}
			if (Character.isWhitespace(contentChar.charValue())) {
				/* not found - so add part scan to content */
				context.content.append(context.partScan);
				if (context.content.length() > 0) {
					context.content.append(contentChar
							.charValue()); /*
											 * add current whitespace too, when
											 * not at start
											 */
				}
				context.contentTokenEnd = context.hereDocPos - 1;

				/* reset part scan */
				context.closingLiteralTokenStart = context.hereDocPos;
				context.partScan = new StringBuilder();
			} else {
				context.partScan.append(contentChar.charValue());
			}

		} while (true);

	}

	private HereDocContext createContext(CodePosSupport codePosSupport) {
		
		HereDocContext context = new HereDocContext(codePosSupport);
		int hereDocTokenStart = context.getHereDocPos();
		
		Character init = context.getCharacterAtPosOrNull(hereDocTokenStart);
		if (init ==null){
			return context;
		}
		char c = init.charValue();
		if (c != '<') {
			return context;
		}
		/*
		 * CHECKPOINT 0: check if next is "<" as well. If so this is a
		 * here-doc...
		 */
		context.hereDocPos = hereDocTokenStart + 1;
		Character ca = context.getCharacterAtPosOrNull(context.hereDocPos++);
		if (ca == null) {
			return context;
		}
		if (ca.charValue() != '<') {
			return context;
		}
		/* CHECKPOINT 1:<< found */
		ca = context.getCharacterAtPosOrNull(context.hereDocPos++);
		if (ca == null) {
			return context;
		}
		if (ca.charValue() == '<') {
			/* no here-doc but here-string - so break */
			return context;
		}
		// next line will mark also as initialized!
		context.hereDocTokenStart=hereDocTokenStart; 
		context.lastCharacter=ca;
		return context;
		
	}
	
	private void step1_scanForLiteral(HereDocContext context) {
		Character ca = context.lastCharacter;
		if (ca==null){
			return;
		}
		StringBuilder literal = new StringBuilder();
		if (Character.isWhitespace(ca.charValue())) {
			/* CHECKPOINT 2a:<< .. found so ignore and keep on getting literal */
		} else {
			/* CHECKPOINT 2b:<<.. found so get literal */
			literal.append(ca.charValue());
		}
		do {
			Character literalChar = context.getCharacterAtPosOrNull(context.hereDocPos++);
			if (literalChar == null) {
				/* end reached but no literal - so ignore */
				return;
			}
			if (Character.isWhitespace(literalChar.charValue())) {
				break;
			}
			literal.append(literalChar.charValue());

		} while (true);
		context.literal=literal;
		context.hereDocTokenEnd = context.hereDocPos - 1;
		return;
	}

	private boolean isEndLiteralFound(String originLiteralToFind, StringBuilder partScan) {
		if (partScan == null || partScan.length() == 0) {
			return false;
		}
		String literalToFind= originLiteralToFind;
		if (originLiteralToFind.startsWith("-")) {
			literalToFind=originLiteralToFind.substring(1);
		}
		String partScanString = partScan.toString();
		if (partScanString.equals(literalToFind)) {
			return true;
		}
		
		/* handle Parameter substitution turned off */
		if (partScanString.length() < 3) {
			/* no possibility for 'a' or "a" ... */
			return false;
		}
		if (literalToFind.indexOf('\'') == 0) {
			if (!literalToFind.endsWith("'")) {
				return false;
			}
			return isLiteralWhenFirstAndLastCharsRemoved(literalToFind, partScanString);
		}
		if (literalToFind.indexOf('\"') == 0) {
			if (!literalToFind.endsWith("\"")) {
				return false;
			}
			return isLiteralWhenFirstAndLastCharsRemoved(literalToFind, partScanString);
		}
		return false;

	}

	private boolean isLiteralWhenFirstAndLastCharsRemoved(String literalToFind, String partScanString) {
		int beginIndex = 1;
		int endIndex = literalToFind.length() - beginIndex;

		if (endIndex<=beginIndex){
			return false;
		}
		
		String literalShrinked = literalToFind.substring(beginIndex, endIndex);
		boolean isLiteral = partScanString.equals(literalShrinked);
		return isLiteral;
	}
}
