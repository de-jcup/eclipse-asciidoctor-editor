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

public class AsciiDoctorLineStartsWithRule implements IPredicateRule {

    private IToken successToken;
    private char[] startsWith;
    private char[] endsWith;
    private boolean multiLines;
    private boolean mustHaveWhitespaceAfter;

    public AsciiDoctorLineStartsWithRule(String startsWith, IToken token) {
        this(startsWith, false, token);
    }

    public AsciiDoctorLineStartsWithRule(String startsWith, boolean multiLines, IToken token) {
        this(startsWith, null, multiLines, token, false);
    }

    public AsciiDoctorLineStartsWithRule(String startsWith, String endsWith, boolean multiLines, IToken token) {
        this(startsWith, endsWith, multiLines, token, false);
    }

    public AsciiDoctorLineStartsWithRule(String startsWith, String endsWith, boolean multiLines, IToken token, boolean mustHaveWhitespaceAfter) {
        this.successToken = token;
        this.multiLines = multiLines;
        this.startsWith = startsWith.toCharArray();
        this.endsWith = endsWith == null ? new char[0] : endsWith.toCharArray();
        this.mustHaveWhitespaceAfter = mustHaveWhitespaceAfter;
    }

    @Override
    public IToken evaluate(ICharacterScanner scanner) {
        return evaluate(scanner, false);
    }

    @Override
    public IToken getSuccessToken() {
        return successToken;
    }

    private boolean isTerminatedByWhitespaceOrEOF(ICharacterScanner scanner) {
        int n = scanner.read();
        if (n != ICharacterScanner.EOF) {
            scanner.unread();
        }
        char c = (char) n;
        boolean isOkay = (n == ICharacterScanner.EOF || Character.isWhitespace(c));
        return isOkay;
    }

    @Override
    public IToken evaluate(ICharacterScanner scanner, boolean resume) {
        boolean startOfDocument = scanner.getColumn() == 0;
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
        int count = 0;
        for (int i = 0; i < startsWith.length; i++) {
            int c = scanner.read();
            count++;
            if (startsWith[i] != c) {
                return resetScannerAndReturnUndefined(scanner, count);
            }
        }
        /* found start word */
        if (mustHaveWhitespaceAfter) {
            if (!isTerminatedByWhitespaceOrEOF(scanner)) {
                return resetScannerAndReturnUndefined(scanner, count);
            }
        }
        EndlessLoopPreventer preventer = new EndlessLoopPreventer(100000);

        boolean noEndsWithScanNecessary = endsWith.length == 0;
        int c = -1;
        int endsWithPos = 0;
        while (true) {
            preventer.assertNoEndlessLoop();

            c = scanner.read();
            count++;
            if (c == ICharacterScanner.EOF) {
                /* end of file */
                if (noEndsWithScanNecessary) {
                    return getSuccessToken();
                }
                /* token not found currently */
                return resetScannerAndReturnUndefined(scanner, count);
            }
            if (c == '\n') {
                /* new line */
                if (multiLines) {
                    /* keep on going but start from fresh */
                    endsWithPos = 0;
                    continue;
                } else {
                    if (noEndsWithScanNecessary) {
                        return getSuccessToken();
                    }
                    /* only one line, so token not found currently */
                    return resetScannerAndReturnUndefined(scanner, count);
                }
            }
            if (noEndsWithScanNecessary) {
                continue;
            }
            /* ENDSWITH is set */

            if (multiLines) {
                if (endsWithPos < endsWith.length) {
                    if (c == endsWith[endsWithPos]) {
                        endsWithPos++;
                    } else {
                        // car not does not match on position so this line fails
                        // we set the position 1 behind last one so it goes to continue
                        // until next new line
                        endsWithPos = endsWith.length + 1;
                        continue;
                    }
                    if (endsWithPos == endsWith.length) {
                        /* found exact so success */
                        if (mustHaveWhitespaceAfter) {
                            if (!isTerminatedByWhitespaceOrEOF(scanner)) {
                                /* not multi line end - so keep on searching... */
                                endsWithPos = endsWith.length + 1;
                                continue;
                            }
                        }
                        return getSuccessToken();
                    }
                } else {
                    // just continue ...
                    continue;
                }
            } else {
                /* single line - just scan until end of line */
                if (endsWithPos < endsWith.length) {
                    if (c == endsWith[endsWithPos]) {
                        endsWithPos++;
                    }
                    if (endsWithPos == endsWith.length) {
                        if (mustHaveWhitespaceAfter) {
                            if (!isTerminatedByWhitespaceOrEOF(scanner)) {
                                /* not single line end - so keep on searching... */
                                endsWithPos = endsWith.length + 1;
                                continue;
                            }
                        }
                        /* found so success */
                        return getSuccessToken();
                    }
                } else {
                    return resetScannerAndReturnUndefined(scanner, count);
                }
            }
        }
    }

    private IToken resetScannerAndReturnUndefined(ICharacterScanner scanner, int count) {
        while (count > 0) {
            scanner.unread();
            count--;
        }
        return Token.UNDEFINED;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(":starsWith='");
        sb.append(startsWith);
        sb.append("', endsWith='");
        sb.append(endsWith);
        sb.append("', multiLines=");
        sb.append(multiLines);
        return sb.toString();
    }

}
