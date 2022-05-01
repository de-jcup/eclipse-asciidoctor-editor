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

import static org.junit.Assert.*;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.junit.Before;
import org.junit.Test;

/**
 * Sorrowly not executable by gradle because of eclipse dependencies. But at
 * least executable in eclipse environment. Tests exact word pattern rule works
 * 
 * @author Albert Tregnaghi
 *
 */
public class ExactWordPatternRuleTest {

    private IToken token;
    private SimpleTestCharacterScanner scanner;
    private IWordDetector detector;

    @Before
    public void before() {
        token = new Token("mocked");
        detector = new OnlyLettersKeyWordDetector();
    }

    @Test
    public void interface_is_found() {
        /* prepare */
        scanner = new SimpleTestCharacterScanner("interface");
        ExactWordPatternRule rule = new ExactWordPatternRule(detector, "interface", token);
//		rule.trace=true;
        /* execute */
        rule.evaluate(scanner);

        /* test */
        assertEquals(8, scanner.column);

    }

    @Test
    public void function_with_keyword_help_inside_is_not_found_as_help() {
        scanner = new SimpleTestCharacterScanner("function show_help_and_exit(){}");
        ExactWordPatternRule rule = new ExactWordPatternRule(detector, "help", token);
//		rule.trace=true;
        /* execute */
        rule.evaluate(scanner);

        /* test */
        assertEquals(0, scanner.column);

    }

    @Test
    public void function_with_keyword_help_inside_is_found_as_funtion() {
        scanner = new SimpleTestCharacterScanner("function show_help_and_exit(){}");
        ExactWordPatternRule rule = new ExactWordPatternRule(detector, "function", token);

        /* execute */
        IToken result = rule.evaluate(scanner);

        /* test */
        assertEquals(token, result);
        assertEquals(8, scanner.column);

    }

    @Test
    public void interface_is_NOT_found_scanner_column_is_0__something() {
        scanner = new SimpleTestCharacterScanner("something");
        ExactWordPatternRule rule = new ExactWordPatternRule(detector, "interface", token);
        rule.trace = true;
        rule.evaluate(scanner);

        assertEquals(0, scanner.column);

    }

    @Test
    public void interface_is_NOT_found_scanner_column_is_0__xinterface() {
        scanner = new SimpleTestCharacterScanner("xinterface");
        ExactWordPatternRule rule = new ExactWordPatternRule(detector, "interface", token);
        rule.trace = true;
        rule.evaluate(scanner);

        assertEquals(0, scanner.column);

    }

    @Test
    public void interface_is_NOT_found_scanner_column_is_0__int() {
        scanner = new SimpleTestCharacterScanner("int");
        ExactWordPatternRule rule = new ExactWordPatternRule(detector, "interface", token);
        rule.trace = true;
        rule.evaluate(scanner);

        assertEquals(0, scanner.column);

    }

    @Test
    public void interface_is_NOT_found_scanner_column_is_0__in() {
        scanner = new SimpleTestCharacterScanner("in");
        ExactWordPatternRule rule = new ExactWordPatternRule(detector, "interface", token);
        rule.trace = true;
        rule.evaluate(scanner);

        assertEquals(0, scanner.column);

    }

}
