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
			list.add(createHeadline(asciidoctorScript, current.toString(), start, end));
		}
	}

	private AsciiDoctorHeadline createHeadline(String asciidoctorScript, String identifiedHeadline, int start, int end) {
		int deep = calculateDeep(identifiedHeadline);
		String name = calculateName(identifiedHeadline);
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
