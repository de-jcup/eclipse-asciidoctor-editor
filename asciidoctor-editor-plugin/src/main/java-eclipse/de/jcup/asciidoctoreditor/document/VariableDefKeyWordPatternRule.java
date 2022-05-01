/*
 * Copyright 2017 Albert Tregnaghi
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
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.WordPatternRule;

public class VariableDefKeyWordPatternRule extends WordPatternRule {

    private String toStringValue;
    StringBuilder traceSb;
    boolean trace = false;

    public VariableDefKeyWordPatternRule(IWordDetector detector, String exactWord, IToken token) {
        this(detector, exactWord, token, true);
    }

    public VariableDefKeyWordPatternRule(IWordDetector detector, String exactWord, IToken token, boolean breaksOnEOF) {
        super(detector, exactWord, null, token);
        toStringValue = getClass().getSimpleName() + ":" + exactWord + "=";
        this.fBreaksOnEOF = breaksOnEOF;
    }

    protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed) {
        /*
         * sequence is not the word found by word detector but the start sequence!!!!!
         * (in this case always the exact word)
         */

        // -------------------------------------------------
        // example: exactWord='test'
        //
        // subjects: atest,test,testa
        // ^----------------only result!
        Counter counter = new Counter();
        if (trace) {
            // trace contains NOT first character, this is done at PatternRule
            traceSb = new StringBuilder();
        }
        int column = scanner.getColumn();
        boolean wordHasPrefix;
        if (column == 1) {
            wordHasPrefix = false;
        } else {
            scannerUnread(scanner, counter);
            scannerUnread(scanner, counter);
            char charBefore = (char) scannerRead(scanner, counter);
            scannerRead(scanner, counter);
            wordHasPrefix = isPrefixCharacter(charBefore);
        }
        if (wordHasPrefix) {
            scannerRead(scanner, counter);
            return counter.cleanupAndReturn(scanner, false);
        }
        for (int i = 1; i < sequence.length; i++) {
            int c = scannerRead(scanner, counter);
            if (c == ICharacterScanner.EOF) {
                if (eofAllowed) {
                    return counter.cleanupAndReturn(scanner, true);
                } else {
                    return counter.cleanupAndReturn(scanner, false);
                }
            } else if (c != sequence[i]) {
                scannerUnread(scanner, counter);
                for (int j = i - 1; j > 0; j--) {
                    scannerUnread(scanner, counter);
                }
                return counter.cleanupAndReturn(scanner, false);
            }
        }
        int read = scannerRead(scanner, counter);
        char charAfter = (char) read;
        scannerUnread(scanner, counter);

        /* when not a = at end- do cleanup */
        if (charAfter != '=') {
            /*
             * the word is more than the exact one - e.g. instead of 'test' 'testx' ... so
             * not correct
             */
            return counter.cleanupAndReturn(scanner, false);
        }
        return counter.cleanupAndReturn(scanner, true);
    }

    private boolean isPrefixCharacter(char charBefore) {
        boolean isPrefix = !Character.isWhitespace(charBefore);
        return isPrefix;
    }

    private int scannerRead(ICharacterScanner scanner, Counter counter) {
        int c = scanner.read();
        if (c == ICharacterScanner.EOF) {
            return c;
        }
        counter.count++;
        if (trace) {
            traceSb.append((char) c);
        }
        return c;

    }

    private void scannerUnread(ICharacterScanner scanner, Counter counter) {
        scanner.unread();
        counter.count--;
        if (trace) {
            int length = traceSb.length();
            if (length < 1) {
                traceSb.append("[(-1)]");
            } else {
                length = length - 1;
                traceSb.setLength(length);
            }
        }
    }

    @Override
    public String toString() {
        return toStringValue;
    }

}