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
package de.jcup.asciidoctoreditor.asciidoc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class AsciiDocStringUtils {

    private static final String IMAGE_PREFIX = "image::";
    private static final String INCLUDE_PREFIX = "include::";
    private static final String UTF_8 = "UTF-8";
    private static final int MAX_CROSSREFERENCE_SIZE = 128;

    public static String resolveFilenameOfDiagramMacroOrNull(String potentialFileNameReference) {
        if (potentialFileNameReference == null) {
            return null;
        }
        String fileName = resolveFilenameOfMacroOrNull(potentialFileNameReference, "ditaa");
        if (fileName != null) {
            return fileName;
        }
        return resolveFilenameOfMacroOrNull(potentialFileNameReference, "plantuml");
    }

    public static String resolveFilenameOfMacroOrNull(String potentialInclude, String macroName) {
        if (potentialInclude == null) {
            return null;
        }
        String prefix = macroName + "::";
        return resolveSubstringFromPrefixToStartOfLastOpeningBracket(potentialInclude, prefix);
    }

    public static String resolveFilenameOfImageOrNull(String potentialImage) {
        return resolveSubstringFromPrefixToStartOfLastOpeningBracket(potentialImage, IMAGE_PREFIX);
    }

    /**
     * Resolves filenames from fullstrings of an potential include.<br>
     * <br>
     * Example:<br>
     * <code>include::src/xyz/filenamexyz.adoc[]</code><br>
     * will be resolved to <br>
     * <code>src/xyz/filenamexyz.adoc</code>
     * 
     * @param potentialInclude
     * @return resolved filename of include or <code>null</code>
     */
    public static String resolveFilenameOfIncludeOrNull(String potentialInclude) {
        return resolveSubstringFromPrefixToStartOfLastOpeningBracket(potentialInclude, INCLUDE_PREFIX);
    }

    static String resolveSubstringFromPrefixToStartOfLastOpeningBracket(String potentialInclude, String prefix) {
        if (potentialInclude == null) {
            return null;
        }
        if (potentialInclude.startsWith(prefix)) {
            if (potentialInclude.endsWith("]")) {
                int lastOpening = potentialInclude.lastIndexOf('[');
                if (lastOpening == -1) {
                    return null;
                }
                int length = prefix.length();
                int endIndex = lastOpening - length;
                String fileName = potentialInclude.substring(length);
                fileName = fileName.substring(0, endIndex);
                return fileName;
            }
        }
        return null;
    }

    public static class LinkTextData {
        LinkTextData() {
            this.text = "";
            this.offsetLeft = 0;
        }

        public String text;
        public int offsetLeft;
    }

    /**
     * Resolves a text from cursor position to left start (white space is
     * terminator) and to right with ending square bracket ("]").
     * 
     * @param line
     * @param offset       (offset in complet document)
     * @param offsetInLine (offset inside the line)
     * @return link text data object
     */
    public static LinkTextData resolveTextFromStartToBracketsEnd(String line, int offset, int offsetInLine) {
        LinkTextData data = new LinkTextData();

        String lineLeftChars = line.substring(0, offsetInLine);
        String lineRightChars = line.substring(offsetInLine);
        StringBuilder sb = new StringBuilder();

        int offsetLeft = offset;
        /* build right part */
        boolean foundEndingBracket = false;
        for (char c : lineRightChars.toCharArray()) {
            foundEndingBracket = (c == ']');
            sb.append(c);
            if (foundEndingBracket) {
                break;
            }
        }
        if (!foundEndingBracket) {
            return data;
        }
        /* build left part */
        char[] left = lineLeftChars.toCharArray();
        for (int i = left.length - 1; i >= 0; i--) {
            char c = left[i];
            if (Character.isWhitespace(c)) {
                break;
            }
            offsetLeft--;
            sb.insert(0, c);
        }

        data.text = sb.toString();
        data.offsetLeft = offsetLeft;
        return data;
    }

    /**
     * Resolves a text from cursor position to left start (white space is
     * terminator) and to right with ending square bracket ("]").
     * 
     * @param line
     * @param offset       (offset in complet document)
     * @param offsetInLine (offset inside the line)
     * @return link text data object
     */
    public static LinkTextData resolveComparisionSignsBorderedAreaFromStartToBracketsEnd(String line, int offset, int offsetInLine) {
        LinkTextData data = new LinkTextData();

        if (line.indexOf('<') == -1) {
            return data;
        }
        if (line.indexOf('>') == -1) {
            return data;
        }

        String lineLeftChars = line.substring(0, offsetInLine);
        String lineRightChars = line.substring(offsetInLine);
        StringBuilder sb = new StringBuilder();

        int offsetLeft = offset;
        /* build right part */
        boolean foundEndingBracket = false;
        int endingBracketsCount = 0;
        int inspectedCharacters = 0;
        for (char c : lineRightChars.toCharArray()) {
            inspectedCharacters++;
            if (inspectedCharacters > MAX_CROSSREFERENCE_SIZE) {
                return data;
            }
            if (c == '>') {
                endingBracketsCount++;
            }
            foundEndingBracket = endingBracketsCount == 2;
            sb.append(c);
            if (foundEndingBracket) {
                break;
            }
        }
        if (!foundEndingBracket) {
            return data;
        }
        inspectedCharacters = 0;

        /* build left part */
        int startBracketsCount = 0;
        boolean foundStartBracket = false;

        char[] left = lineLeftChars.toCharArray();
        for (int i = left.length - 1; i >= 0; i--) {
            char c = left[i];
            inspectedCharacters++;
            if (inspectedCharacters > MAX_CROSSREFERENCE_SIZE) {
                return data;
            }
            sb.insert(0, c);
            if (c == '<') {
                startBracketsCount++;
            }
            if (startBracketsCount == 2) {
                foundStartBracket = true;
            }
            if (foundStartBracket) {
                break;
            }
            offsetLeft--;
        }
        if (!foundStartBracket) {
            return data;
        }

        data.text = sb.toString();
        data.offsetLeft = offsetLeft;
        return data;
    }

    public static String readUTF8FileToString(File fileToRead) throws IOException {
        String originText = null;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileToRead), UTF_8))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            br.close();
            originText = sb.toString();
        }
        return originText;
    }

    public static File writeTextToUTF8File(String transformed, File newTempFile) throws IOException {
        if (newTempFile == null) {
            throw new IllegalArgumentException("file may not be null!");
        }
        if (newTempFile.isDirectory()) {
            throw new IllegalArgumentException("file may not be a directory!");
        }

        File parentFile = newTempFile.getParentFile();
        Path parentFilePath = parentFile.toPath();

        Files.createDirectories(parentFilePath);

        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newTempFile), UTF_8))) {
            bw.write(transformed);
            bw.close();
            return newTempFile;
        }
    }

    /**
     * Resolves cross reference id if possible. See
     * https://docs.asciidoctor.org/asciidoc/latest/macros/xref/ for details. <br>
     * <br>
     * Examples:
     * <ul>
     * <li>xref:test-target-link-internal-2[linked with xref].</li>
     * <li><<test-target-link-internal-2,Link to
     * "test-target-link-internal-2">></li>
     * </ul>
     * 
     * @param text
     * @return id or <code>null</code>
     */
    public static String resolveCrossReferenceIdOrNull(String text) {
        if (text == null) {
            return null;
        }
        String inspect = text.trim();
        if (inspect.startsWith("xref:")) {
            String link = inspect.substring("xref:".length());
            int attributeDefIndex = link.indexOf("[");
            if (attributeDefIndex == -1) {
                return null;
            }
            link = link.substring(0, attributeDefIndex);
            return link.trim();
        } else if (inspect.startsWith("<<")) {
            String link = inspect.substring("<<".length());
            if (!link.endsWith(">>")) {
                return null;
            }
            int commaIndex = link.indexOf(',');
            if (commaIndex == -1) {
                return null;
            }
            link = link.substring(0, commaIndex);
            return link.trim();
        }

        return null;
    }
}
