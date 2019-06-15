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

import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;
import de.jcup.asp.api.ServerLogSeverity;

/**
 * Inspired from EGradleConsoleStyleListener
 * 
 * @author Albert Tregnaghi
 *
 */
public class AsciiDoctorConsoleStyleListener implements LineStyleListener {
    private final static Collection<ParseData> SHARED_PARSE_DATA = new ArrayList<>();
    static {
		addParseDataByIndex("asciidoctor:", GRAY);
		addParseDataByIndex("ASP:", GRAY);

		addParseDataByIndex(ServerLogSeverity.INFO, GREEN);
		addParseDataByIndex(ServerLogSeverity.DEBUG, GREEN);
		
		addParseDataByIndex("WARNING:", ORANGE);
		addParseDataByIndex(ServerLogSeverity.WARN, ORANGE);
		
		addParseDataByIndex(ServerLogSeverity.UNKNOWN, MAGENTA);
		
		addParseDataByIndex("FAILED:", BRIGHT_RED);
		addParseDataByIndex(ServerLogSeverity.FATAL, BRIGHT_RED);
		addParseDataByIndex(ServerLogSeverity.ERROR, BRIGHT_RED);
		
		addParseDataByIndex("invalid option:", BRIGHT_RED);
		
		addParseDataStartEndPattern("file:",".adoc",null,false,true);

	}

    static final void addParseDataByIndex(ServerLogSeverity severity, RGB color) {
        addParseDataByIndex(severity.name() + ":", color);
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

    static final void addParseDataStartEndPattern(String startString, String endString, RGB color, boolean bold, boolean useHyperlinkColor) {
        ParseData data = new ParseData();
        data.startString = startString;
        data.endString = endString;
        data.color = color;
        data.bold = bold;
        data.useHyperLinkColor = useHyperlinkColor;
        if (data.useHyperLinkColor) {
            data.underline=true;
        }
        SHARED_PARSE_DATA.add(data);
    }

    int lastRangeEnd = 0;

    @Override
    public void lineGetStyle(LineStyleEvent event) {
        if (event == null) {
            return;
        }
        String lineText = event.lineText;
        if (lineText == null || lineText.isEmpty()) {
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

    private void parse(LineStyleEvent event, StyleRange defStyle, String currentText, List<StyleRange> ranges, ParseData data) {
        if (data.isSearchingByStartEndString()) {
            parseBySubstringStartEnd(event, defStyle, currentText, ranges, data);
        } else if (data.isSearchingSimpleSubstring()) {
            parseByIndexOf(event, defStyle, currentText, ranges, data);
        } else {
            throw new UnsupportedOperationException("Unsupported/unimplemented");
        }

    }

    private void parseByIndexOf(LineStyleEvent event, StyleRange startStyle, String currentText, List<StyleRange> ranges, ParseData data) {
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
                addRange(ranges, event.lineOffset + pos, data.subString.length(),data);
            }
        } while (pos != -1);
    }

    private void parseBySubstringStartEnd(LineStyleEvent event, StyleRange startStyle, String currentText, List<StyleRange> ranges, ParseData data) {
        int fromIndex = 0;
        int pos = 0;
        int length = currentText.length();
        do {
            if (fromIndex >= length) {
                break;
            }
            pos = currentText.indexOf(data.startString, fromIndex);
            fromIndex = pos + 1;

            if (pos != -1) {
                int endPos = currentText.indexOf(data.endString);
                if (endPos != -1) {
                    addRange(ranges, event.lineOffset + pos, endPos - pos+data.endString.length(), data);
                }
            }
        } while (pos != -1);
    }


    private Color getForegroundColor(ParseData data) {
        RGB color = data.color;
        if (data.useHyperLinkColor) {
            color = JFaceResources.getColorRegistry().getRGB(JFacePreferences.HYPERLINK_COLOR);
        }
        return getColor(color);
    }

    private Color getColor(RGB rgb) {
        if (rgb==null) {
            return null;
        }
        return AsciiDoctorEditorActivator.getDefault().getColorManager().getColor(rgb);
    }

    private static class ParseData {
        private String endString;
        private String startString;
        private boolean bold;
        private String subString;
        private RGB color;
        private boolean useHyperLinkColor;
        private boolean underline;
        private RGB background;

        private boolean isSearchingSimpleSubstring() {
            return subString != null;
        }

        private boolean isSearchingByStartEndString() {
            return startString != null && endString != null;
        }
    }

    private void addRange(List<StyleRange> ranges, int start, int length, ParseData data) {
        Color foreGround = getForegroundColor(data);
        Color background = getColor(data.background);;
        StyleRange range = new StyleRange(start, length, foreGround, background);
        if (data.bold) {
            range.fontStyle = SWT.BOLD;
        }
        range.underline=data.underline;
        ranges.add(range);
        lastRangeEnd = lastRangeEnd + range.length;
    }
}