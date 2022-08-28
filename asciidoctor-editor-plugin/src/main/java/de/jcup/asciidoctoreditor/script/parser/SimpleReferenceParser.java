/*
 * Copyright 2019 Albert Tregnaghi
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
import java.util.Objects;

import de.jcup.asciidoctoreditor.script.AsciiDoctorFileReference;

public class SimpleReferenceParser {

    public static final SimpleReferenceParser INCLUDE_PARSER = new SimpleReferenceParser("include::", false);
    public static final SimpleReferenceParser PLANTUML_PARSER = new SimpleReferenceParser("plantuml::", false);
    public static final SimpleReferenceParser DITAA_PARSER = new SimpleReferenceParser("ditaa::", false);
    public static final SimpleReferenceParser IMAGE_PARSER = new SimpleReferenceParser("image::", true);

    protected String identifier;
    protected char firstChar;
    private boolean inline;

    /**
     * Creates an include parser, where identifier can be another one than
     * "include::" - e.g. "plantuml::"
     * 
     * @param identifier
     */
    public SimpleReferenceParser(String identifier, boolean inline) {
        Objects.nonNull(identifier);
        if (identifier.isEmpty()) {
            throw new IllegalArgumentException("Identifier may not be empty");
        }
        this.inline = inline;
        this.identifier = identifier;
        this.firstChar = identifier.charAt(0);
    }

    public List<AsciiDoctorFileReference> parse(String asciidoctorScript) {
        List<AsciiDoctorFileReference> list = new ArrayList<AsciiDoctorFileReference>();
        if (noIdentifierFound(asciidoctorScript)) {
            return list;
        }
        StringBuilder current = new StringBuilder();
        int start = 0;
        int currentPos = -1;
        for (char c : asciidoctorScript.toCharArray()) {
            currentPos++;
            if (inline && c == ']') {
                int end = currentPos - 1;// we do not count \n ...
                addWhenCurrentNotEmptyAndStartsWithIdentifier(asciidoctorScript, list, current, start, end);
                /* start next */
                current = new StringBuilder();
                start = currentPos + 1; // next word (will not be interpreted when current is empty...)
                continue;
            } else if (c == '\n') {
                int end = currentPos - 1;// we do not count \n ...
                addWhenCurrentNotEmptyAndStartsWithIdentifier(asciidoctorScript, list, current, start, end);
                /* start next */
                current = new StringBuilder();
                start = currentPos + 1; // next word (will not be interpreted when current is empty...)
                continue;
            }
            if (current != null) {
                if (inline) {
                    if (Character.isWhitespace(c) && !Character.isSpaceChar(c)) {
                        continue;
                    }
                }
                current.append(c);
                /*
                 * check if this is the first char, if so check if its is starting point ...
                 * otherwise leaf
                 */
                if (current.length() == 1 && current.charAt(0) != firstChar) {
                    // short break - line must start with i, otherwise no include
                    current = null;
                }
                if (current != null && current.length() == identifier.length()) {
                    if (current.indexOf(identifier) != 0) {
                        // another short break
                        current = null;
                    }
                }
            }
        }
        addWhenCurrentNotEmptyAndStartsWithIdentifier(asciidoctorScript, list, current, start, currentPos);

        return list;
    }

    private boolean noIdentifierFound(String asciidoctorScript) {
        /* no script, or identifier does not exist */
        return asciidoctorScript == null || asciidoctorScript.indexOf(identifier) == -1;
    }

    protected void addWhenCurrentNotEmptyAndStartsWithIdentifier(String asciidoctorScript, List<AsciiDoctorFileReference> list, StringBuilder current, int start, int end) {
        if (current == null || current.length() == 0) {
            return;
        }
        String text = current.toString().trim();
        if (!text.startsWith(identifier)) {
            return;
        }
        list.add(createInclude(asciidoctorScript, text, start, end));
    }

    private AsciiDoctorFileReference createInclude(String asciidoctorScript, String fullExpression, int start, int end) {
        AsciiDoctorFileReference include = new AsciiDoctorFileReference(fullExpression, start, end, fullExpression.length());
        return include;
    }

}