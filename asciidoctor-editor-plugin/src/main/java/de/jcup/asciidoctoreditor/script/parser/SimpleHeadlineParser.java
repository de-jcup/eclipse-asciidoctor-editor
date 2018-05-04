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

import de.jcup.asciidoctoreditor.script.AsciiDoctorHeadline;

public class SimpleHeadlineParser {

	public List<AsciiDoctorHeadline> parse(String asciidoctorScript) {
		List<AsciiDoctorHeadline> list = new ArrayList<AsciiDoctorHeadline>();
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
				addHeadlineWhenCurrentNotEmpty(asciidoctorScript, list, current, start, end); 
				/* start next */
				current = new StringBuilder();
				start =currentPos+1; // next word (will not be interpreted when current is empty...)
				continue;
			}
			if (current != null) {
				current.append(c);
				if (current.charAt(0) != '=') {
					// short break - line must start with = otherwise no headline
					current = null;
				}
			}
		}
		addHeadlineWhenCurrentNotEmpty(asciidoctorScript, list, current, start, currentPos);

		return list;
	}

	protected void addHeadlineWhenCurrentNotEmpty(String asciidoctorScript, List<AsciiDoctorHeadline> list,
			StringBuilder current, int start, int end) {
		if (current != null && current.length() > 0) {
			AsciiDoctorHeadline headline = createHeadlineOrNull(asciidoctorScript, current.toString(), start, end);
			if (headline!=null){
				list.add(headline);
			}
		}
	}

	private AsciiDoctorHeadline createHeadlineOrNull(String asciidoctorScript, String identifiedHeadline, int start, int end) {
		String name = calculateName(identifiedHeadline);
		if (name == null || name.trim().length()==0){
			return null;
		}
		int deep = calculateDeep(identifiedHeadline);
		AsciiDoctorHeadline headline = new AsciiDoctorHeadline(deep, name, start, end, identifiedHeadline.length());
		return headline;
	}

	private String calculateName(String identifiedHeadline) {
		if (identifiedHeadline == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (char charAt : identifiedHeadline.toCharArray()) {
			if (sb.length() == 0) {
				if (charAt == '=') {
					continue;
				}
			}
			sb.append(charAt);
		}
		return sb.toString().trim();
	}

	private int calculateDeep(String word) {
		if (word == null) {
			return 0;
		}
		int deep = 0;
		for (char charAt : word.toCharArray()) {
			if (charAt != '=') {
				break;
			}
			deep++;
		}
		return deep;
	}

}
