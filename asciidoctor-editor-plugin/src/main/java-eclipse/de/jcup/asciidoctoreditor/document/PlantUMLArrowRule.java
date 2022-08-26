/*
 * Copyright 2022 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
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

public class PlantUMLArrowRule implements IPredicateRule {

    private static final char[] lineCharacters = { '.', '-' };
    private static final char[] startCharacters = { '.', '-', '#', '<', '~', 'o', 'x', '*', '[', '0', ')', '/', '\\' };
    private static final char[] endCharacters = { '.', '-', '>', 'x', 'o', ']', '0', '(', '\\', '/' };
    
    /* @formatter:off */
    private static final char[] acceptedCharacters = { 
            '-',
            '0',
            '#' ,
           '/' ,
            '~' ,
            '\\',
            '.' ,
            '|' ,
            '(' ,
            ')' ,
            ',' ,
            '=' ,
            '>' ,
            '<' ,
            '[' ,
            ']'};
    /* @formatter:on */
    
    private IToken token;

    public PlantUMLArrowRule(IToken token) {
        this.token = token;
    }

    @Override
    public IToken getSuccessToken() {
        return token;
    }

    @Override
    public IToken evaluate(ICharacterScanner scanner) {
        return evaluate(scanner, false);
    }
    
    private class ScanContext{
        int counter;
        int openingBrackets;
        StringBuilder sb = new StringBuilder();
        ICharacterScanner scanner;
    }

    @Override
    public IToken evaluate(ICharacterScanner scanner, boolean resume) {
        char start = (char) scanner.read();
        char last = (char) -1;
        if (!isWordStart(start)) {
            scanner.unread();
            return Token.UNDEFINED;
        }
        ScanContext context = new ScanContext();
        context.counter=1; // we count the first character as well
        context.scanner=scanner;
        context.sb.append(start);
        do {
            int read = scanner.read(); // use int for EOF detection, char makes problems here!
            context.counter++;
            if (context.counter > 80) {
                return revertScannerAndReturnUndefined(context);
            }
            char c = (char) read;
            if (c=='[') {
                context.openingBrackets++;
            }
            if (c==']') {
                context.openingBrackets--;
            }
            if (context.openingBrackets>1) {
                revertScanner(context);;
                return Token.UNDEFINED;
            }
            if (ICharacterScanner.EOF == read || (!isWordPart(c,context))) {
                if (context.counter > 2) {
                    String str = context.sb.toString();

                    if (context.counter == 3) {
                        /* @formatter:off */
                        switch(str) {
                        case "<-":
                        case "->":
                        case ".>":
                        case "-/":
                        case "-\\":
                        case "..":
                        case "--":
                            return getSuccessToken(); 
                        default:
                            return revertScannerAndReturnUndefined(context);
                        }
                    }
                    /* @formatter:on */

                    /* @formatter:off */
                    int indexOfLineChar=-1; 
                    boolean atLeastOnelineCharacterFound = false;
                    for (char x: lineCharacters) {
                        indexOfLineChar = str.indexOf(x);
                        if (indexOfLineChar!= -1) {
                            atLeastOnelineCharacterFound=true;
                            break;
                        }
                    }
                    
                    if (!atLeastOnelineCharacterFound) {
                        return revertScannerAndReturnUndefined(context);
                    }
                    /* @formatter:off */
                    for (char x : endCharacters) {
                        if (last == x) {
                            return getSuccessToken();
                        }
                        /* @formatter:on */
                    }
                }
                return revertScannerAndReturnUndefined(context);
            }
            context.sb.append(c);
            last = c;
        } while (true);
    }

    private IToken revertScannerAndReturnUndefined(ScanContext context) {
        revertScanner(context);
        return Token.UNDEFINED;
    }

    private void revertScanner(ScanContext context) {
        for (int i = 0; i < context.counter; i++) {
            context.scanner.unread();
        }
    }

    private boolean isWordStart(char c) {
        for (char x : startCharacters) {
            if (x == c) {
                return true;
            }
        }
        return false;
    }

    private boolean isWordPart(char c, ScanContext context) {
        if (context.openingBrackets==1) {
            if (Character.isAlphabetic(c)) {
                return true;
            }
            if (Character.isDigit(c)) {
                return true;
            }
        }
        for (char x : acceptedCharacters) {
            if (c == x) {
                return true;
            }
        }
        return false;
    }

}