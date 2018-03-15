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

import static de.jcup.asciidoctoreditor.document.AsciiDoctorDocumentIdentifiers.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

import de.jcup.asciidoctoreditor.document.keywords.AsciiDoctorCommandKeyWords;
import de.jcup.asciidoctoreditor.document.keywords.AsciiDoctorLanguageKeyWords;
import de.jcup.asciidoctoreditor.document.keywords.AsciiDoctorSpecialVariableKeyWords;
import de.jcup.asciidoctoreditor.document.keywords.DocumentKeyWord;

public class AsciiDoctorDocumentPartitionScanner extends RuleBasedPartitionScanner {

	private OnlyLettersKeyWordDetector onlyLettersWordDetector = new OnlyLettersKeyWordDetector();
	private VariableDefKeyWordDetector variableDefKeyWordDetector = new VariableDefKeyWordDetector();

	public AsciiDoctorDocumentPartitionScanner() {
		IToken boldText = createToken(TEXT_BOLD);
		IToken hyperlink = createToken(HYPERLINK);
		IToken comment = createToken(COMMENT);
		IToken backtickString = createToken(BACKTICK_STRING);

		IToken knownVariables = createToken(KNOWN_VARIABLES);
		IToken variables = createToken(VARIABLES);
		IToken includeKeyword = createToken(INCLUDE_KEYWORD);
		IToken asciidoctorCommand = createToken(ASCIIDOCTOR_COMMAND);

		List<IPredicateRule> rules = new ArrayList<>();
		
		rules.add(new AsciiDoctorVariableRule(variables));

		rules.add(new SingleLineRule("http://", " ", hyperlink, (char) -1, true));
		rules.add(new SingleLineRule("https://", " ", hyperlink, (char) -1, true));
		
		rules.add(new SingleLineRule("//", "", comment, (char) -1, true));
		rules.add(new MultiLineRule("/*", "*/", comment));
		
		rules.add(new AsciiDoctorStringRule("`", "`", backtickString));
		rules.add(new MultiLineRule("----", "----", backtickString));
		
		rules.add(new SingleLineRule("**", "**", boldText, (char) -1, true));
		rules.add(new SingleLineRule("*", "*", boldText, (char) -1, true));
		
		rules.add(new SingleLineRule("include::*", "[]", includeKeyword, (char) -1, true));

		buildWordRules(rules, asciidoctorCommand, AsciiDoctorCommandKeyWords.values());

		buildWordRules(rules, knownVariables, AsciiDoctorSpecialVariableKeyWords.values());

		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}

	private void buildWordRules(List<IPredicateRule> rules, IToken token, DocumentKeyWord[] values) {
		for (DocumentKeyWord keyWord : values) {
			rules.add(new ExactWordPatternRule(onlyLettersWordDetector, createWordStart(keyWord), token,
					keyWord.isBreakingOnEof()));
		}
	}

	private void buildVarDefRules(List<IPredicateRule> rules, IToken token, DocumentKeyWord[] values) {
		for (DocumentKeyWord keyWord : values) {
			rules.add(new VariableDefKeyWordPatternRule(variableDefKeyWordDetector, createWordStart(keyWord), token,
					keyWord.isBreakingOnEof()));
		}
	}

	private String createWordStart(DocumentKeyWord keyWord) {
		return keyWord.getText();
	}

	private IToken createToken(AsciiDoctorDocumentIdentifier identifier) {
		return new Token(identifier.getId());
	}
}
