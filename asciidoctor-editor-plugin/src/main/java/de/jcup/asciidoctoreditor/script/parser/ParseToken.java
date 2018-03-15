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

public class ParseToken {

	private static final String EQUAL_OPERAND = "=";
	String text;
	int start;
	int end;

	ParseToken() {

	}

	ParseToken(String text) {
		this(text, 0, 0);
	}

	ParseToken(String text, int start, int end) {
		if (text == null) {
			text = "";
		}
		this.text = text;
		this.start = start;
		this.end = end;
	}


	public String getText() {
		return text;
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(createTypeDescription());
		sb.append(":'");
		sb.append(text);
		sb.append('\'');
		
		return sb.toString();
	}

	public String createTypeDescription() {
		StringBuilder sb = new StringBuilder();
		
		if (isComment()){
			sb.append("COMMENT");
		}
		if (isVariable()){
			sb.append("VARIABLE");
		}
		if (isString()){
			sb.append("STRING");
		}
		if (isIf()){
			sb.append("IF");
		}
		if (isFi()){
			sb.append("FI");
		}
		if (isCase()){
			sb.append("CASE");
		}
		if (isEsac()){
			sb.append("ESAC");
		}
		if (isDo()){
			sb.append("DO");
		}
		if (isDone()){
			sb.append("DONE");
		}
		if (isCloseBlock()){
			sb.append("BLOCK-CLOSE");
		}
		if (isOpenBlock()){
			sb.append("BLOCK-OPEN");
		}
		if (sb.length()==0){
			sb.append("EXPRESSION");
		}
		return sb.toString();
	}

	private boolean isVariable() {
		return getSafeText().startsWith("$");
	}

	public boolean isComment() {
		return getSafeText().startsWith("#");
	}

	public boolean isSingleString() {
		return getSafeText().startsWith("'");
	}

	public boolean isDoubleString() {
		return getSafeText().startsWith("\"");
	}

	public boolean isDoubleTickedString() {
		return getSafeText().startsWith("`");
	}

	private String getSafeText() {
		return text==null ? "":text;
	}

	public boolean isString() {
		boolean isString = isSingleString() || isDoubleString() || isDoubleTickedString();
		return isString;
	}

	public boolean isFunctionKeyword() {
		return "function".equals(text);
	}

	/**
	 * @return <code>true</code> when token ends with function brackets and contains no illegal states (e.g. string, comment)
	 * and also no illegal characters in name
	 */ 
	public boolean isFunction() {
		boolean isFunctionName = endsWithFunctionBrackets();
		isFunctionName = isFunctionName && isLegalFunctionName();
		isFunctionName = isFunctionName && !isComment();
		isFunctionName = isFunctionName && text.length() > 2;
		isFunctionName = isFunctionName && !isString();
		
		return  isFunctionName;
	}

	public boolean isLegalFunctionName() {
		return ! getSafeText().contains(EQUAL_OPERAND);
	}

	public boolean endsWithFunctionBrackets() {
		return getSafeText().endsWith("()");
	}

	public boolean hasLength(int length) {
		return getSafeText().length() == length;
	}

	public String getTextAsFunctionName() {
		// String name = token.text;
		if (getSafeText().endsWith("()")) {
			return text.substring(0, text.length() - 2);
		}
		return text;
	}

	public boolean isOpenBlock() {
		return getSafeText().length() == 1 && text.endsWith("{");
	}

	public boolean isCloseBlock() {
		return getSafeText().length() == 1 && text.endsWith("}");
	}

	public boolean isDo() {
		return getSafeText().equals("do");
	}

	public boolean isDone() {
		return getSafeText().equals("done");
	}

	public boolean isIf() {
		return getSafeText().equals("if");
	}

	public boolean isFi() {
		return getSafeText().equals("fi");
	}

	public boolean isHereDoc() {
		return ! isHereString() && getSafeText().startsWith("<<");
	}

	public boolean isHereString() {
		return getSafeText().startsWith("<<<");
	}

	public boolean isCase() {
		return getSafeText().equals("case");
	}
	
	public boolean isEsac() {
		return getSafeText().equals("esac");
	}
}
