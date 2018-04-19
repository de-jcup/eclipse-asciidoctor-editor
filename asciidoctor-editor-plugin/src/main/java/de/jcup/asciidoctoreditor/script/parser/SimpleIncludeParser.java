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

import java.util.ArrayList;
import java.util.List;

import de.jcup.asciidoctoreditor.script.AsciiDoctorInclude;

public class SimpleIncludeParser {

	private static final String INCLUDE_IDENTIFIER = "include::";

	public List<AsciiDoctorInclude> parse(String asciidoctorScript) {
		List<AsciiDoctorInclude> list = new ArrayList<AsciiDoctorInclude>();
		if (asciidoctorScript == null) {
			return list;
		}
		StringBuilder current = new StringBuilder();
		int start=0;
		int currentPos = -1;
		for (char c : asciidoctorScript.toCharArray()) {
			currentPos++;
			if (c == '\n') {
				int end = currentPos-1;// we do not count \n ...
				addWhenCurrentNotEmptyAndStartsWithInclude(asciidoctorScript, list, current, start, end); 
				/* start next */
				current = new StringBuilder();
				start =currentPos+1; // next word (will not be interpreted when current is empty...)
				continue;
			}
			if (current != null) {
				current.append(c);
				if (current.charAt(0) != 'i') {
					// short break - line must start with i, otherwise no include
					current = null;
				}
				if (current!=null && current.length()==INCLUDE_IDENTIFIER.length()){
					if (current.indexOf(INCLUDE_IDENTIFIER)!=0){
						// another short break
						current=null;
					}
				}
			}
		}
		addWhenCurrentNotEmptyAndStartsWithInclude(asciidoctorScript, list, current, start, currentPos);

		return list;
	}

	protected void addWhenCurrentNotEmptyAndStartsWithInclude(String asciidoctorScript, List<AsciiDoctorInclude> list,
			StringBuilder current, int start, int end) {
		if (current==null || current.length()==0){
			return;
		}
		String text = current.toString().trim();
		if (!text.startsWith(INCLUDE_IDENTIFIER)) {
			return;
		}
		list.add(createHeadline(asciidoctorScript, text, start, end));
	}

	private AsciiDoctorInclude createHeadline(String asciidoctorScript, String identifiedInclude, int start, int end) {
		String name = calculateName(identifiedInclude);
		AsciiDoctorInclude include = new AsciiDoctorInclude(identifiedInclude, name, start, end, identifiedInclude.length());
		return include;
	}

	private String calculateName(String identifiedHeadline) {
		if (identifiedHeadline == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (char charAt : identifiedHeadline.toCharArray()) {
			if (charAt=='[' || Character.isWhitespace(charAt)){
				break;
			}
			sb.append(charAt);
		}
		return sb.toString().trim();
	}

}
