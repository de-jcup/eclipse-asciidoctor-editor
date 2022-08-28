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

import static de.jcup.asciidoctoreditor.document.AsciiDoctorPlantUMLDocumentIdentifiers.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

import de.jcup.asciidoctoreditor.document.keywords.PlantUMLColorDocumentKeywords;
import de.jcup.asciidoctoreditor.document.keywords.PlantUMLKeywordDocumentKeywords;
import de.jcup.asciidoctoreditor.document.keywords.PlantUMLMissingKeywordDocumentKeywords;
import de.jcup.asciidoctoreditor.document.keywords.PlantUMLPreprocessorDocumentKeywords;
import de.jcup.asciidoctoreditor.document.keywords.PlantUMLSkinparameterDocumentKeywords;
import de.jcup.asciidoctoreditor.document.keywords.PlantUMLTypeDocumentKeywords;
import de.jcup.eclipse.commons.keyword.DocumentKeyWord;

public class AsciiDoctorPlantUMLDocumentPartitionScanner extends RuleBasedPartitionScanner {

    private OnlyLettersKeyWordDetector onlyLettersWordDetector = new OnlyLettersKeyWordDetector();

    public AsciiDoctorPlantUMLDocumentPartitionScanner() {
        IToken note = createToken(PLANTUML_NOTE);
        IToken comment = createToken(PLANTUML_COMMENT);
        IToken divider = createToken(PLANTUML_DIVIDER);

        IToken color = createToken(PLANTUML_COLOR);
        IToken skinparameter = createToken(PLANTUML_SKINPARAMETER);
        IToken type = createToken(PLANTUML_TYPE);

        IToken string = createToken(PLANTUML_DOUBLE_STRING);

        IToken preprocessor = createToken(PLANTUML_PREPROCESSOR);
        IToken keyword = createToken(PLANTUML_KEYWORD);
        IToken arrow = createToken(PLANTUML_ARROW);
        IToken label = createToken(PLANTUML_LABEL);

        List<IPredicateRule> rules = new ArrayList<>();
        rules.add(new SingleLineRule("'", "", comment, (char) -1, true));
        rules.add(new MultiLineRule("/'", "'/", comment, (char) -1, true));

        rules.add(new SingleLineRule("\"", "\"", string, (char) -1, true));
        rules.add(new SingleLineRule("note", " ", note, (char) -1, true));
        rules.add(new SingleLineRule("end note", " ", note, (char) -1, true));

        rules.add(new SingleLineRule("===", "===", divider, (char) -1, true));
        rules.add(new SingleLineRule("==", "==", divider, (char) -1, true));

        rules.add(new SingleLineRule("@startuml", "", preprocessor, (char) -1, true));
        rules.add(new SingleLineRule("@enduml", "", preprocessor, (char) -1, true));
        rules.add(new SingleLineRule(":", "", label, (char) -1, true));

        buildWordRules(rules, color, PlantUMLColorDocumentKeywords.values());
        rules.add(new HashColorRule(color));
        buildWordRules(rules, keyword, PlantUMLKeywordDocumentKeywords.values());
        buildWordRules(rules, keyword, PlantUMLMissingKeywordDocumentKeywords.values());
        buildWordRules(rules, preprocessor, PlantUMLPreprocessorDocumentKeywords.values());
        buildWordRules(rules, skinparameter, PlantUMLSkinparameterDocumentKeywords.values());
        buildWordRules(rules, type, PlantUMLTypeDocumentKeywords.values());

        rules.add(new PlantUMLArrowRule(arrow));

        setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
    }

    private void buildWordRules(List<IPredicateRule> rules, IToken token, DocumentKeyWord[] values) {
        for (DocumentKeyWord keyWord : values) {
            rules.add(new ExactWordPatternRule(onlyLettersWordDetector, createWordStart(keyWord), token, keyWord.isBreakingOnEof()));
        }
    }

    private String createWordStart(DocumentKeyWord keyWord) {
        return keyWord.getText();
    }

    private IToken createToken(AsciiDoctorDocumentIdentifier identifier) {
        return new Token(identifier.getId());
    }

    private class HashColorRule implements IPredicateRule {

        private IToken successToken;

        public HashColorRule(IToken token) {
            this.successToken = token;
        }

        @Override
        public IToken evaluate(ICharacterScanner scanner) {
            return evaluate(scanner, true);
        }

        @Override
        public IToken getSuccessToken() {
            return successToken;
        }

        @Override
        public IToken evaluate(ICharacterScanner scanner, boolean resume) {
            char first = (char) scanner.read();
            if ('#' != first) {
                scanner.unread();
                return Token.UNDEFINED;
            }
            // okay we start with #
            int countOfScans = 1;

            char c;
            do {
                c = (char) scanner.read();
                countOfScans++;
                if (Character.isWhitespace(c)) {
                    /* a space terminates */
                    return successToken;
                }
            } while (Character.isDigit(c) || Character.isAlphabetic(c));

            /* scanner roll back */
            for (int i = 0; i < countOfScans; i++) {
                scanner.unread();
            }
            return Token.UNDEFINED;
        }

    }
}
