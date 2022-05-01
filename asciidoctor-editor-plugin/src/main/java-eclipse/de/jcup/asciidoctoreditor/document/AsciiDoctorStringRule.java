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
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.Token;

public class AsciiDoctorStringRule extends PatternRule {

    private static final boolean BREAKS_ON_EOL = false; // support multi line strings!
    private static final boolean BREAKS_ON_EOF = true;
    private static final boolean ESCAPE_CONTINUES_LINE = false;

    public AsciiDoctorStringRule(String startSequence, String endSequence, IToken token) {
        super(startSequence, endSequence, token, '\\', BREAKS_ON_EOL, BREAKS_ON_EOF, ESCAPE_CONTINUES_LINE);
    }

    protected IToken doEvaluate(ICharacterScanner scanner, boolean resume) {

        if (resume) {

            if (endSequenceDetected(scanner))
                return fToken;

        } else {
            int c = scanner.read();
            if (c == fStartSequence[0]) {
                scanner.unread();
                scanner.unread();
                int before = scanner.read();

                if (before == '\\') {
                    return Token.UNDEFINED;
                }
                scanner.read();

                if (sequenceDetected(scanner, fStartSequence, false)) {
                    if (endSequenceDetected(scanner))
                        return fToken;
                }
            }
        }

        scanner.unread();
        return Token.UNDEFINED;
    }

}
