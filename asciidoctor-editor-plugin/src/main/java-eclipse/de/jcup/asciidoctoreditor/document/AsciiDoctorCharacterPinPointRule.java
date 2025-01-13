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

public class AsciiDoctorCharacterPinPointRule implements IPredicateRule {

    private IToken token;
    private char[] start;
    private char end;

    public AsciiDoctorCharacterPinPointRule(char start, IToken token) {
        this(new char[] { start }, ' ', token);
    }

    public AsciiDoctorCharacterPinPointRule(char[] start, IToken token) {
        this(start, ' ', token);
    }

    public AsciiDoctorCharacterPinPointRule(char[] start, char end, IToken token) {
        this.start = start;
        this.end = end;
        this.token = token;
    }

    @Override
    public IToken evaluate(ICharacterScanner scanner) {
        return evaluate(scanner, false);
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
        int c = -1;
        Counter counter = new Counter();
        for (int pos = 0; pos < start.length; pos++) {
            counter.count++;
            c=scanner.read();
            if (c != start[pos]) {
                counter.cleanup(scanner);
                return Token.UNDEFINED;
            }

        }
        int after = scanner.read();
        counter.count++;
        if (after == end) {
            return token;
        }
        counter.cleanup(scanner);
        return Token.UNDEFINED;
    }

    @Override
    public IToken getSuccessToken() {
        return token;
    }

}
