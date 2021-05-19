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
        if (! (scanner instanceof AsciiDoctorDocumentPartitionScanner)) {
            return Token.UNDEFINED;
        }
        AsciiDoctorDocumentPartitionScanner partitionScanner = (AsciiDoctorDocumentPartitionScanner) scanner;
        Counter counter = new Counter();
        boolean startOfDocument = partitionScanner.getOffset()==0;
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
        do {
            int c = scanner.read();
            counter.count++;

            if (c == ' ') {
                continue;
            }
            counter.cleanup(scanner);

            boolean isJustAListEntryWithSpacedsBefore = c == '*' || c == '.' || c == '-';
            if (isJustAListEntryWithSpacedsBefore) {
                return Token.UNDEFINED;
            }
            break;

        } while (true);

        /* okay its not a list entry - so check before */

        boolean checkLineBefore = partitionScanner.getOffset()>0;
        if (checkLineBefore) {

            /* we must read the complete line before to decide if it possible here or not */
            StringBuilder lineBefore = new StringBuilder();
            EndlessLoopPreventer preventer = new EndlessLoopPreventer(100000);

            boolean firstNewLine=true;
            do {
                if (partitionScanner.getOffset()<=0) {
                    break;
                }
                scanner.unread(); // one left A
                int c = scanner.read(); // one right B - so position before A

                if (c == ICharacterScanner.EOF) {
                    break;
                }
                if (c == '\n') {
                    if (firstNewLine) {
                        firstNewLine=false;
                    }else {
                        break;
                    }
                }else {
                    if (! Character.isWhitespace(c)) {
                        lineBefore.insert(0, (char)c);
                    }
                }
                scanner.unread(); // one left more, so one= before A
                counter.count--; // remember one step left

                preventer.assertNoEndlessLoop();

            } while (true);

            String line = lineBefore.toString();
            boolean accepted = false;
            accepted = accepted || line.isEmpty();
            accepted = accepted || line.startsWith("=");
            accepted = accepted || line.startsWith(".");
            accepted = accepted || line.startsWith("****");

            counter.cleanup(scanner); // we move every time complete back to start

            if (!accepted) {
                return Token.UNDEFINED;
            }
        }

        /*
         * we accept this as literal, now lets iterate until no longer literal paragraph
         */
        EndlessLoopPreventer preventer = new EndlessLoopPreventer(100000);
        StringBuilder lineBuilder = new StringBuilder();
        do {
            int follow = scanner.read();
            if (follow == ICharacterScanner.EOF) {
                /* we accept this as last line being a literal paragraph */
                return successToken;
            }
            if (follow == '\n' || follow == '\r') {
                boolean fetchedLineBeforeIsEmpty = lineBuilder.length() == 0;

                if (fetchedLineBeforeIsEmpty) {
                    return successToken;// return directly access token
                } else {
                    /* start new line building */
                    lineBuilder = new StringBuilder();
                }
            } else {
                if (!Character.isWhitespace(follow)) {
                    lineBuilder.append(follow);
                }
            }
            preventer.assertNoEndlessLoop();
        } while (true);

    }

}
