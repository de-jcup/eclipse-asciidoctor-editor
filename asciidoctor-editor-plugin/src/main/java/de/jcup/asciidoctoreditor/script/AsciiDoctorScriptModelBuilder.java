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
package de.jcup.asciidoctoreditor.script;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.jcup.asciidoctoreditor.script.parser.ParseToken;
import de.jcup.asciidoctoreditor.script.parser.SimpleHeadlineParser;
import de.jcup.asciidoctoreditor.script.parser.validator.CaseEndsWithEsacValidator;
import de.jcup.asciidoctoreditor.script.parser.validator.ClosedBlocksValidator;
import de.jcup.asciidoctoreditor.script.parser.validator.DoEndsWithDoneValidator;
import de.jcup.asciidoctoreditor.script.parser.validator.IfEndsWithFiValidator;

/**
 * A asciidoc file model builder
 * 
 * @author Albert Tregnaghi
 *
 */
public class AsciiDoctorScriptModelBuilder {
	private boolean ignoreDoValidation;
	private boolean ignoreBlockValidation;
	private boolean ignoreIfValidation;
	private boolean ignoreFunctionValidation;
	private boolean debugMode;

	/**
	 * Parses given script and creates a asciidoc file model
	 * 
	 * @param asciidoctorScript
	 * @return a simple model with some information about asciidoc file
	 * @throws AsciiDoctorScriptModelException 
	 */
	public AsciiDoctorScriptModel build(String asciidoctorScript) throws AsciiDoctorScriptModelException{
		AsciiDoctorScriptModel model = new AsciiDoctorScriptModel();

		SimpleHeadlineParser parser = new SimpleHeadlineParser();
		Collection<AsciiDoctorHeadline> headlines=parser.parse(asciidoctorScript);
		
		model.getHeadlines().addAll(headlines);
		
		return model;
	}

	public void setIgnoreBlockValidation(boolean ignoreBlockValidation) {
		this.ignoreBlockValidation = ignoreBlockValidation;
	}

	public void setIgnoreDoValidation(boolean ignoreDoValidation) {
		this.ignoreDoValidation = ignoreDoValidation;
	}

	public void setIgnoreIfValidation(boolean ignoreIfValidation) {
		this.ignoreIfValidation = ignoreIfValidation;
	}
	
	public void setIgnoreFunctionValidation(boolean ignoreFunctionValidation) {
		this.ignoreFunctionValidation = ignoreFunctionValidation;
	}
	

	private List<AsciiDoctorScriptValidator<List<ParseToken>>> createParseTokenValidators() {
		List<AsciiDoctorScriptValidator<List<ParseToken>>> validators = new ArrayList<>();
		if (!ignoreDoValidation) {
			validators.add(new DoEndsWithDoneValidator());
		}
		if (!ignoreBlockValidation) {
			validators.add(new ClosedBlocksValidator());
		}
		if (!ignoreIfValidation) {
			validators.add(new IfEndsWithFiValidator());
			validators.add(new CaseEndsWithEsacValidator());
		}
		return validators;
	}

	private void buildFunctionsByTokens(AsciiDoctorScriptModel model, List<ParseToken> tokens) {
//
//		for (int tokenNr = 0; tokenNr < tokens.size(); tokenNr++) {
//			int currentTokenNr = tokenNr;
//			ParseToken token = tokens.get(currentTokenNr++);
//			boolean isFunction = false;
//			Integer functionStart = null;
//			int functionEnd = 0;
//			/* ++++++++++++++++++++++ */
//			/* + Scan for headlines + */
//			/* ++++++++++++++++++++++ */
//			/* could be 'function MethodName()' or 'function MethodName()' */
//			if (token.isFunctionKeyword() && hasPos(currentTokenNr, tokens)) {
//				isFunction = true;
//				functionStart = Integer.valueOf(token.getStart());
//				token = tokens.get(currentTokenNr++);
//			}
//			/* could be 'MethodName()' */
//			isFunction = isFunction || token.isFunction();
//			if (!isFunction) {
//				/* could be 'MethodName ()' but NOT something like params=()*/
//				if (token.isLegalFunctionName() && hasPos(currentTokenNr, tokens)) {
//					ParseToken followToken = tokens.get(currentTokenNr++);
//					isFunction = followToken.hasLength(2) && followToken.endsWithFunctionBrackets();
//				}
//			}
//			if (isFunction) {
//				if (functionStart == null) {
//					functionStart = Integer.valueOf(token.getStart());
//				}
//				String functionName = token.getTextAsFunctionName();
//				functionEnd = token.getEnd();
//				/* ++++++++++++++++++++++++++++++ */
//				/* + Scan for curly braces open + */
//				/* ++++++++++++++++++++++++++++++ */
//
//				if (!hasPos(currentTokenNr, tokens)) {
//					if (!ignoreFunctionValidation){
//						model.errors.add(createAsciiDoctorErrorFunctionMissingCurlyBrace(token, functionName));
//					}
//					break;
//				}
//				ParseToken openCurlyBraceToken = tokens.get(currentTokenNr++);
//				if (!openCurlyBraceToken.isOpenBlock()) {
//					if (!ignoreFunctionValidation){
//						model.errors.add(createAsciiDoctorErrorFunctionMissingCurlyBrace(token, functionName));
//					}
//					continue;
//				}
//				/* +++++++++++++++++++++++++++++++ */
//				/* + Scan for curly braces close + */
//				/* +++++++++++++++++++++++++++++++ */
//
//				AsciiDoctorHeadline function = new AsciiDoctorHeadline();
//				function.lengthToNameEnd = functionEnd - functionStart.intValue();
//				function.position = functionStart.intValue();
//				function.name = functionName;
//				function.end = -1;
//
//				while (hasPos(currentTokenNr, tokens)) {
//					ParseToken closeCurlyBraceToken = tokens.get(currentTokenNr++);
//					if (closeCurlyBraceToken.isCloseBlock()) {
//						function.end = closeCurlyBraceToken.getEnd();
//						break;
//					}
//				}
//				if (function.end == -1) {
//					/* no close block found - mark this as an error */
//					if (!ignoreFunctionValidation){
//						model.errors.add(createAsciiDoctorErrorCloseFunctionCurlyBraceMissing(functionName, openCurlyBraceToken));
//					}
//					break;
//				}
//
//				model.headlines.add(function);
//				/*
//				 * function created - last currentTokenNr++ was too much because
//				 * it will be done by loop to- so reduce with 1
//				 */
//				tokenNr = currentTokenNr - 1;
//			}
//		}
	}

	private AsciiDoctorError createAsciiDoctorErrorCloseFunctionCurlyBraceMissing(String functionName,
			ParseToken openCurlyBraceToken) {
		return new AsciiDoctorError(openCurlyBraceToken.getStart(), openCurlyBraceToken.getEnd(),
				"This curly brace is not closed. So function '" + functionName + "' is not valid.");
	}

	private AsciiDoctorError createAsciiDoctorErrorFunctionMissingCurlyBrace(ParseToken token, String functionName) {
		return new AsciiDoctorError(token.getStart(), token.getEnd(),
				"The function '" + functionName + "' is not valid because no opening curly brace found.");
	}

	private boolean hasPos(int pos, List<?> elements) {
		if (elements == null) {
			return false;
		}
		return pos < elements.size();
	}

	public void setDebug(boolean debugMode) {
		this.debugMode=debugMode;
	}

}
