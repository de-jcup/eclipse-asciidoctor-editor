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
package de.jcup.asciidoctoreditor;

public class SimpleStringUtils {
	private static final String EMPTY = "";

	public static boolean equals(String text1, String text2) {
		if (text1 == null) {
			if (text2 == null) {
				return true;
			}
			return false;
		}
		if (text2 == null) {
			return false;
		}
		return text2.equals(text1);
	}

	public static String shortString(String string, int max) {
		if (max == 0) {
			return EMPTY;
		}
		if (string == null) {
			return EMPTY;
		}
		if (string.length() <= max) {
			return string;
		}
		/* length > max */
		if (max == 1) {
			return ".";
		}
		if (max == 2) {
			return "..";
		}
		if (max == 3) {
			return "...";
		}
		StringBuilder sb = new StringBuilder();
		sb.append(string.substring(0, max - 3));
		sb.append("...");
		return sb.toString();
	}
	
	/**
	 * Returns next reduced variable from given offset.
	 * Reduced means a variable array like $ASCIIDOCTOR_VERSIN[0] will be reduced to $ASCIIDOCTOR_VERSIN!
	 * 
	 * @param string
	 * @param offset
	 * @return word, or empty string, never <code>null</code>
	 */
	public static String nextReducedVariableWord(String string, int offset) {
		return nextWord(string, offset, new ReducedVariableWordEndDetector());
	}

	/**
	 * Returns next word until word end detected from given offset
	 * 
	 * @param string
	 * @param offset
	 * @param wordEndDetector - if null {@link WhitespaceWordEndDetector} will be used automatically
	 * @return word, or empty string, never <code>null</code>
	 */
	public static String nextWord(String string, int offset, WordEndDetector wordEndDetector) {
		if (string == null) {
			return EMPTY;
		}
		if (offset < 0) {
			return EMPTY;
		}
		if (offset >= string.length()) {
			return EMPTY;
		}
		char c2 = string.charAt(offset);
		if (Character.isWhitespace(c2)){
			return EMPTY;
		}
		if (wordEndDetector==null){
			/* back to fall back impl */
			wordEndDetector=new WhitespaceWordEndDetector();
		}
		/* go to word start (offset == 0 or whitespace)*/
		int start=offset;
		for (;start>0;start--){
			char c = string.charAt(start);
			if (wordEndDetector.isWordEnd(c)){
				start+=1;
				break;
			}
		}
		/* start defined so scan for word */
		StringBuilder sb = new StringBuilder();
		for (int i=start;i<string.length();i++){
			char c = string.charAt(i);
			if (wordEndDetector.isWordEnd(c)){
				break;
			}
			sb.append(c);
		}
		return sb.toString();
	}
}
