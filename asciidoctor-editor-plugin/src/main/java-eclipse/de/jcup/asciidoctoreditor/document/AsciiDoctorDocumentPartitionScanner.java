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
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

import de.jcup.asciidoctoreditor.document.keywords.AsciiDoctorAdmonitionParagraphKeyWords;
import de.jcup.asciidoctoreditor.document.keywords.AsciiDoctorCommandKeyWords;
import de.jcup.asciidoctoreditor.document.keywords.AsciiDoctorIncludeKeywords;
import de.jcup.eclipse.commons.keyword.DocumentKeyWord;

public class AsciiDoctorDocumentPartitionScanner extends RuleBasedPartitionScanner {

	public AsciiDoctorDocumentPartitionScanner() {
		IToken boldText = createToken(TEXT_BOLD);
		IToken italicText = createToken(TEXT_ITALIC);
		IToken monospacedText = createToken(TEXT_MONOSPACED);
		IToken hyperlink = createToken(HYPERLINK);
		IToken comment = createToken(COMMENT);
		IToken textBlock = createToken(TEXT_BLOCK);

		IToken knownVariables = createToken(KNOWN_VARIABLES);
		IToken variables = createToken(VARIABLES);
		IToken includeKeyword = createToken(INCLUDE_KEYWORD);
		IToken asciidoctorCommand = createToken(ASCIIDOCTOR_COMMAND);
		IToken headline = createToken(HEADLINE);
		IToken delimiters = createToken(DELIMITERS);

		List<IPredicateRule> rules = new ArrayList<>();
		
		rules.add(new AsciiDoctorVariableRule(variables));
		rules.add(new AsciiDoctorURLHyperlinkRule(hyperlink));
		
		aLineStartsWith("= ",rules,headline);
		aLineStartsWith("== ",rules,headline);
		aLineStartsWith("=== ",rules,headline);
		aLineStartsWith("==== ",rules,headline);
		aLineStartsWith("===== ",rules,headline);
		aLineStartsWith("====== ",rules,headline);
		
		aLineStartsWith("|===",rules,asciidoctorCommand);

		for (AsciiDoctorAdmonitionParagraphKeyWords admonitionKeyword: AsciiDoctorAdmonitionParagraphKeyWords.values()){
			aLineStartsWith(admonitionKeyword.getText(),rules,asciidoctorCommand,true);
		}
		
		rules.add(new AsciiDoctorLineStartsWithRule("[[", "]]",false, asciidoctorCommand));
		rules.add(new AsciiDoctorLineStartsWithRule("[", "]",false, asciidoctorCommand));
		
		rules.add(new AsciiDoctorLineStartsWithRule("////", "////",true, comment));
		rules.add(new AsciiDoctorLineStartsWithRule("//", null,false, comment));
		
		rules.add(new AsciiDoctorLineStartsWithRule("----", "----", true, textBlock,true));
		
		rules.add(new AsciiDoctorFormattedTextRule("`", "`", monospacedText)); 
		rules.add(new AsciiDoctorFormattedTextRule("**", "**", boldText));
		rules.add(new AsciiDoctorFormattedTextRule("*", "*", boldText));
		rules.add(new AsciiDoctorFormattedTextRule("_", "_", italicText));
		
		rules.add(new SingleLineRule("<<", ">>", hyperlink, (char) -1, true));
		
		buildLineStartsWithRule(rules, asciidoctorCommand, "]", AsciiDoctorCommandKeyWords.values());
		buildLineStartsWithRule(rules, includeKeyword, "]", AsciiDoctorIncludeKeywords.values());

		rules.add(new AsciiDoctorLineStartsWithRule(":", ":", false, knownVariables));
		rules.add(new SingleLineRule("{", "}", knownVariables, (char) -1, true));
		
		
		rules.add(new AsciiDoctorLineContainsOnlyRule("....", delimiters)); // delimitor ,delimited block
		rules.add(new AsciiDoctorLineContainsOnlyRule("====", delimiters)); // delimitor , admonition block syntax
		rules.add(new AsciiDoctorLineContainsOnlyRule("****", delimiters)); // delimitor ,delimited example

		rules.add(new AsciiDoctorLineStartsWithRule(".", headline)); // title
		rules.add(new AsciiDoctorLineStartsWithRule(" ", delimiters)); // literal paragraphs starting with a space...

		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}
	
	private void buildLineStartsWithRule(List<IPredicateRule> rules, IToken token, String ending, DocumentKeyWord[] values) {
		for (DocumentKeyWord keyWord : values) {
			String text = keyWord.getText();
			rules.add(new AsciiDoctorLineStartsWithRule(text, ending, false, token));
		}
	}
	private void aLineStartsWith(String startsWith, List<IPredicateRule> rules, IToken token){
		aLineStartsWith(startsWith, rules, token,false);
	}

	private void aLineStartsWith(String startsWith, List<IPredicateRule> rules, IToken token, boolean endOnSpace){
		String endsWith=null;
		if (endOnSpace){
			endsWith=" ";
		}
		rules.add(new AsciiDoctorLineStartsWithRule(startsWith, endsWith,false, token));
	}

	private IToken createToken(AsciiDoctorDocumentIdentifier identifier) {
		return new Token(identifier.getId());
	}
}
