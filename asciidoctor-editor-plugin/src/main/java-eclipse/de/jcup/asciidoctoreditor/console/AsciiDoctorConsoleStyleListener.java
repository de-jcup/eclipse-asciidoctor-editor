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
package de.jcup.asciidoctoreditor.console;

import static de.jcup.asciidoctoreditor.console.AsciiDoctorConsoleColorsConstants.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;

/**
 * Inspired from EGradleConsoleStyleListener
 * @author Albert Tregnaghi
 *
 */
public class AsciiDoctorConsoleStyleListener implements LineStyleListener {
	private final static Collection<ParseData> SHARED_PARSE_DATA = new ArrayList<>();
	static {
		addParseDataByIndex("asciidoctor:", GRAY);
		addParseDataByIndex("WARNING:", ORANGE);
		addParseDataByIndex("FAILED:", BRIGHT_RED);
		addParseDataByIndex("invalid option:", BRIGHT_RED);

	}

	static final void addParseDataByIndex(String substring, RGB color) {
		addParseDataByIndex(substring, color, false);
	}

	static final void addParseDataByIndex(String substring, RGB color, boolean bold) {
		ParseData data = new ParseData();
		data.subString = substring;
		data.color = color;
		data.bold = bold;
		SHARED_PARSE_DATA.add(data);
	}

	int lastRangeEnd = 0;

	@Override
	public void lineGetStyle(LineStyleEvent event) {
		if (event == null) {
			return;
		}
		String lineText = event.lineText;
		if (lineText==null || lineText.isEmpty()) {
			return;
		}
		/* styling */
		StyleRange defStyle;

		boolean atLeastOneStyle = event.styles != null && event.styles.length > 0;
		if (atLeastOneStyle) {
			defStyle = (StyleRange) event.styles[0].clone();
			if (defStyle.background == null) {
				defStyle.background = getColor(BLACK);
			}
		} else {
			defStyle = new StyleRange(1, lastRangeEnd, getColor(BLACK), getColor(WHITE), SWT.NORMAL);
		}

		lastRangeEnd = 0;

		List<StyleRange> ranges = new ArrayList<StyleRange>();
		boolean handled = false;
		/* index parts and other */
		if (!handled) {
			for (ParseData data : SHARED_PARSE_DATA) {
				parse(event, defStyle, lineText, ranges, data);
			}
		}

		if (!ranges.isEmpty()) {
			event.styles = ranges.toArray(new StyleRange[ranges.size()]);
		}
	}

	private void parse(LineStyleEvent event, StyleRange defStyle, String currentText, List<StyleRange> ranges,
			ParseData data) {
		if (data.isSearchingSimpleSubstring()) {
			parseByIndex(event, defStyle, currentText, ranges, data);
		} else {
			throw new UnsupportedOperationException("Unsupported/unimplemented");
		}

	}

	private void parseByIndex(LineStyleEvent event, StyleRange startStyle, String currentText, List<StyleRange> ranges,
			ParseData data) {
		int fromIndex = 0;
		int pos = 0;
		int length = currentText.length();
		do {
			if (fromIndex >= length) {
				break;
			}
			pos = currentText.indexOf(data.subString, fromIndex);
			fromIndex = pos + 1;

			if (pos != -1) {
				addRange(ranges, event.lineOffset + pos, data.subString.length(), getColor(data.color), data.bold);
			}
		} while (pos != -1);
	}

	private Color getColor(RGB rgb) {
		return AsciiDoctorEditorActivator.getDefault().getColorManager().getColor(rgb);
	}

	private static class ParseData {
		public boolean bold;
		private String subString;
		private RGB color;

		private boolean isSearchingSimpleSubstring() {
			return subString != null;
		}
	}

	private void addRange(List<StyleRange> ranges, int start, int length, Color foreground, boolean bold) {
		addRange(ranges, start, length, foreground, null, bold);
	}

	private void addRange(List<StyleRange> ranges, int start, int length, Color foreground, Color background,
			boolean bold) {
		StyleRange range = new StyleRange(start, length, foreground, background);
		if (bold) {
			range.fontStyle = SWT.BOLD;
		}
		ranges.add(range);
		lastRangeEnd = lastRangeEnd + range.length;
	}
}