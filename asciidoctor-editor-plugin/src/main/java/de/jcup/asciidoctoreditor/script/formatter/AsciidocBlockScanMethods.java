/*
 * Copyright 2020 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.script.formatter;

public class AsciidocBlockScanMethods {

    public static Boolean isNewLine(String line) {
        return line.isEmpty() || line.startsWith("\n");
    }

    public static Boolean isBlockMarker(String line) {
        return line.startsWith("----");
    }
    
    public static Boolean isMetaInfoMarker(String line) {
        return line.startsWith("[");
    }

    public static Boolean isTableMarker(String line) {
        return line.startsWith("|==");
    }

    public static Boolean isSingleLineComment(String line) {
        return line.trim().startsWith("//");
    }

    public static Boolean isHeadlineMarker(String line) {
        return line.indexOf("= ") != -1;
    }

    public static Boolean isVariableMarker(String line) {
        return line.startsWith(":");
    }

    public static Boolean isCommandMarker(String line) {
        if (line.indexOf("::") == -1) {
            return false;
        }
        /* could be command - e.g. include:: or plantuml:: etc. */
        char before = 'a';
        for (char c : line.toCharArray()) {
            if (c == ' ' || c == '"' || c == '\'') {
                return false;
            }
            if (c == ':') {
                if (before == ':') {
                    return true;
                }
            }
            before = c;
        }
        return false;
    }

    public static Boolean isTextOnly(String line) {
        boolean textOnly = true;
        textOnly = textOnly && !isNewLine(line);
        textOnly = textOnly && !isTableMarker(line);
        textOnly = textOnly && !isBlockMarker(line);
        textOnly = textOnly && !isHeadlineMarker(line);
        textOnly = textOnly && !isHeadlineMarker(line);
        textOnly = textOnly && !isVariableMarker(line);
        textOnly = textOnly && !isCommandMarker(line);

        return textOnly;

    }
}
