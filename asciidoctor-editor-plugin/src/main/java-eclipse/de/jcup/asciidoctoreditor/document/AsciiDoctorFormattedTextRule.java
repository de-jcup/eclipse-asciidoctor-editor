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

/**
 * Special rule to provide
 * https://asciidoctor.org/docs/asciidoc-syntax-quick-reference/#formatted-text
 * The rule will found $abcd$ where $ is the markup and abcd is the content -
 * but only when $ is followed by something not being a white space. For
 * example: *bold text*, _italic text_ will be found, * not bold will not be
 * found
 * 
 * Examples:
 * 
 * <pre>
 * $abc$     -> found
 * x $abc$   -> found 
 * x$abc$    -> not found
 * x$abc$y   -> not found
 * x $abc$y  -> not found
 * x $abc$ y -> found
 * x $abc    -> not found
 * </pre>
 * 
 * @author Albert Tregnaghi
 *
 */
public class AsciiDoctorFormattedTextRule implements IPredicateRule {

    private IToken token;

    private FormattedTextFinder finder;

    public AsciiDoctorFormattedTextRule(String start, String end, IToken token) {
        this.token = token;
        finder = new FormattedTextFinder(start, end);
    }

    @Override
    public IToken evaluate(ICharacterScanner scanner) {
        return evaluate(scanner, false);
    }

    @Override
    public IToken evaluate(ICharacterScanner scanner, boolean resume) {
        EclipseToPlainJavaCharacterScannerAdapter adapter = new EclipseToPlainJavaCharacterScannerAdapter(scanner);
        boolean found = finder.isFound(adapter);
        if (found) {
            return getSuccessToken();
        }
        return Token.UNDEFINED;
    }

    @Override
    public IToken getSuccessToken() {
        return token;
    }

}
