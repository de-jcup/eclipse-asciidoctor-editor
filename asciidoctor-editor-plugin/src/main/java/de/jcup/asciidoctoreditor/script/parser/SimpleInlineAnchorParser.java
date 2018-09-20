package de.jcup.asciidoctoreditor.script.parser;

import java.util.ArrayList;
import java.util.List;

import de.jcup.asciidoctoreditor.script.AsciiDoctorInlineAnchor;

public class SimpleInlineAnchorParser {

	public List<AsciiDoctorInlineAnchor> parse(String asciidoctorScript) {
		List<AsciiDoctorInlineAnchor> list = new ArrayList<AsciiDoctorInlineAnchor>();
		if (asciidoctorScript == null || asciidoctorScript
				.length() < 5) {/* at least a [[x]] necessary.. */
			return list;
		}
		StringBuilder current = new StringBuilder();
		int start = 0;
		int currentPos = -1;
		for (char c : asciidoctorScript.toCharArray()) {
			currentPos++;
			if (c == '\n') {
				int end = currentPos - 1;// we do not count \n ...
				addWhenCurrentWhenValidAnchorText(asciidoctorScript, list, current, start, end);
				/* start next */
				current = new StringBuilder();
				start = currentPos + 1; // next word (will not be interpreted
										// when current is empty...)
				continue;
			}
			if (current != null) {
				current.append(c);
				if (current.length() == 1 && current.charAt(0) != '[') {
					// short break - line must start with [[, otherwise no
					// include
					current = null;
				} else if (current.length() == 2 && current.charAt(1) != '[') {
					// short break - line must start with [[, otherwise no
					// include
					current = null;
				}
			}
		}
		addWhenCurrentWhenValidAnchorText(asciidoctorScript, list, current, start, currentPos);

		return list;
	}

	protected void addWhenCurrentWhenValidAnchorText(String asciidoctorScript, List<AsciiDoctorInlineAnchor> list,
			StringBuilder current, int start, int end) {
		if (current == null || current.length() == 0) {
			return;
		}
		AsciiDoctorInlineAnchor anchor = createAnchor(asciidoctorScript, current.toString(), start, end);
		if (anchor == null) {
			return;
		}
		list.add(anchor);
	}

	private AsciiDoctorInlineAnchor createAnchor(String asciidoctorScript, String identifiedInclude, int start,
			int end) {
		String text = calculateAnchorText(identifiedInclude);
		if (text == null) {
			return null;
		}
		AsciiDoctorInlineAnchor anchor = new AsciiDoctorInlineAnchor(text, start, end);
		return anchor;
	}

	private String calculateAnchorText(String identified) {
		if (identified == null) {
			return null;
		}
		String anchor = identified.trim();
		if (anchor.startsWith("[[") && anchor.endsWith("]]")) {
			return anchor;
		}
		return null;
	}
}
