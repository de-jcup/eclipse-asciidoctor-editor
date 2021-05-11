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

public class AsciiDoctorLineContainsOnlyRule implements IPredicateRule {

    private IToken successToken;
    private char[] contains;

    public AsciiDoctorLineContainsOnlyRule(String startsWith, IToken token) {
        this.contains = startsWith.toCharArray();
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
        for (int i = 0; i < contains.length; i++) {
            int c = scanner.read();
            count++;
            if (contains[i] != c) {
                return resetScannerAndReturnUndefined(scanner, count);
            }
        }
        EndlessLoopPreventer preventer = new EndlessLoopPreventer(100000);

        int c = -1;
        preventer.assertNoEndlessLoop();

        c = scanner.read();
        count++;
        if (c == ICharacterScanner.EOF) {
            /* end of file */
            return getSuccessToken();
        }
        if (c == '\n') {
            /* new line */
            return getSuccessToken();
        }
        return resetScannerAndReturnUndefined(scanner, count);
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
        sb.append(":contains='");
        sb.append(contains);
        return sb.toString();
    }

}
