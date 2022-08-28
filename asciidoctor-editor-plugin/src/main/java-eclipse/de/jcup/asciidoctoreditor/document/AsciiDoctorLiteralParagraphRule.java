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
package de.jcup.asciidoctoreditor.document;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import de.jcup.asciidoctoreditor.EndlessLoopPreventer;

/**
 * Rules:
 * <ol>
 * <li>The literal starts not when text before has not space before - or has
 * space but starts with</li>
 * <li>As long as the next line is also starting with a space the literal
 * paragraph has not ended</li>
 * </ol>
 * 
 * @author albert
 *
 */
public class AsciiDoctorLiteralParagraphRule implements IPredicateRule {

    private IToken successToken;

    public AsciiDoctorLiteralParagraphRule(IToken token) {
        this.successToken = token;
    }

    @Override
    public IToken evaluate(ICharacterScanner scanner) {
        return evaluate(scanner, false);
    }

    @Override
    public IToken getSuccessToken() {
        return successToken;
    }

    @Override
    public IToken evaluate(ICharacterScanner scanner, boolean resume) {
        if (!(scanner instanceof AsciiDoctorDocumentPartitionScanner)) {
            return Token.UNDEFINED;
        }
        AsciiDoctorDocumentPartitionScanner partitionScanner = (AsciiDoctorDocumentPartitionScanner) scanner;
        Counter counter = new Counter();
        boolean startOfDocument = partitionScanner.getOffset() == 0;
        boolean newLine = startOfDocument;
        if (!startOfDocument) {
            scanner.unread();
            int cbefore = scanner.read();
            newLine = newLine || cbefore == '\n';
            newLine = newLine || cbefore == '\r';
        }

        if (!newLine) {
            return Token.UNDEFINED;
        }

        /* now check if this line starts with a space */
        int firstChar = scanner.read();
        scanner.unread();
        if (firstChar != ' ') {
            return Token.UNDEFINED;
        }

        /*
         * this line is starting with at least one space - but could be still a list
         * entry!
         */
        counter = new Counter();
        boolean foundPotentialList = false;

        /*
         * check if we must break - e.g. when we have only leading spaces for list
         * entries
         */
        do {
            int c = scanner.read();
            counter.count++;

            if (foundPotentialList) {
                if (c == ' ') {
                    /* it is list - so cleanup scanner and return not found/undefined */
                    counter.cleanup(scanner);
                    return Token.UNDEFINED;
                }
                /* it must be something else - its not a list so exit */
                break;

            } else {

                if (c == ' ') {
                    /* we accept as many spaces as possible here */
                    continue;
                }
                /* not a space ... */
                foundPotentialList = (c == '*' || c == '.' || c == '-');
                if (!foundPotentialList) {
                    /* character is something else, so cannot be a list */
                    break;
                }
            }

        } while (true);

        String lineWithoutWhitespaces = null;
        /* okay its not a list entry - so check before */
        boolean checkLineBefore = partitionScanner.getOffset() > 0;
        if (checkLineBefore) {

            /* we must read the complete line before to decide if it possible here or not */
            StringBuilder lineBeforeWithoutWhitespaces = null;
            EndlessLoopPreventer preventer = new EndlessLoopPreventer(100000);

            boolean firstNewLine = true;
            do {
                if (partitionScanner.getOffset() <= 0) {
                    break;
                }
                scanner.unread(); // one left A
                int c = scanner.read(); // one right B - so position before A

                if (c == ICharacterScanner.EOF) {
                    break;
                }
                if (c == '\n') {
                    if (firstNewLine) {
                        firstNewLine = false;
                        lineBeforeWithoutWhitespaces = new StringBuilder();
                    } else {
                        break;
                    }
                } else {
                    if (lineBeforeWithoutWhitespaces != null && !Character.isWhitespace(c)) {
                        lineBeforeWithoutWhitespaces.insert(0, (char) c);
                    }
                }
                scanner.unread(); // one left more, so one= before A
                counter.count--; // remember one step left

                preventer.assertNoEndlessLoop();

            } while (true);

            boolean accepted = false;
            lineWithoutWhitespaces = (lineBeforeWithoutWhitespaces == null ? "" : lineBeforeWithoutWhitespaces.toString());
            accepted = accepted || lineWithoutWhitespaces.isEmpty();
            accepted = accepted || lineWithoutWhitespaces.startsWith("=");
            accepted = accepted || lineWithoutWhitespaces.startsWith(".");
            accepted = accepted || lineWithoutWhitespaces.startsWith("****");

            counter.cleanup(scanner); // we move every time complete back to start

            if (!accepted) {
                return Token.UNDEFINED;
            }
        } else {
            lineWithoutWhitespaces = "";
        }

        /*
         * we accept this as literal, now lets iterate until no longer literal paragraph
         */
        EndlessLoopPreventer preventer = new EndlessLoopPreventer(100000);
        StringBuilder lineBuilder = new StringBuilder();
        do {
            if (lineWithoutWhitespaces.isEmpty() && lineBuilder.toString().trim().startsWith("1. ")) {
                counter.cleanup(scanner);
                return Token.UNDEFINED;
            }
            int follow = scanner.read();
            counter.count++;

            if (follow == ICharacterScanner.EOF) {
                /* we accept this as last line being a literal paragraph */
                return successToken;
            }
            if (follow == '\n' || follow == '\r') {
                boolean fetchedLineBeforeIsEmpty = lineBuilder.length() == 0;

                if (fetchedLineBeforeIsEmpty) {
                    /* end of literal detected */
                    return successToken;// return directly access token
                } else {
                    /* start new line building */
                    lineBuilder = new StringBuilder();
                }
            } else {
                if (lineBuilder.length() > 0 || !Character.isWhitespace(follow)) {
                    lineBuilder.append((char) follow);
                }
            }
            preventer.assertNoEndlessLoop();
        } while (true);

    }

}
