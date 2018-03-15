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
package de.jcup.asciidoctoreditor;

import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferenceConstants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.BoldStylerProvider;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension7;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import de.jcup.asciidoctoreditor.document.keywords.AsciiDoctorCommandKeyWords;
import de.jcup.asciidoctoreditor.document.keywords.AsciiDoctorLanguageKeyWords;
import de.jcup.asciidoctoreditor.document.keywords.AsciiDoctorSpecialVariableKeyWords;
import de.jcup.asciidoctoreditor.document.keywords.DocumentKeyWord;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;

public class AsciiDoctorEditorSimpleWordContentAssistProcessor implements IContentAssistProcessor, ICompletionListener {

	private static final SimpleWordListBuilder WORD_LIST_BUILDER = new SimpleWordListBuilder();
	private static final NoWordListBuilder NO_WORD_BUILDER = new NoWordListBuilder();

	private String errorMessage;

	private SimpleWordCodeCompletion simpleWordCompletion = new SimpleWordCodeCompletion();

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		IDocument document = viewer.getDocument();
		if (document == null) {
			return null;
		}
		String source = document.get();

		Set<String> words = simpleWordCompletion.calculate(source, offset);

		ICompletionProposal[] result = new ICompletionProposal[words.size()];
		int i = 0;
		for (String word : words) {
			result[i++] = new SimpleWordProposal(document, offset, word);
		}

		return result;
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return null;
	}

	private class SimpleWordProposal implements ICompletionProposal, ICompletionProposalExtension7 {

		private int offset;
		private String word;
		private int nextSelection;
		private StyledString styledString;
		private String textBefore;

		SimpleWordProposal(IDocument document, int offset, String word) {
			this.offset = offset;
			this.word = word;

			String source = document.get();
			textBefore = simpleWordCompletion.getTextbefore(source, offset);
		}

		@Override
		public void apply(IDocument document) {
			// the proposal shall enter always a space after applyment...
			String proposal = word;
			if (isAddingSpaceAtEnd()) {
				proposal += " ";
			}
			int zeroOffset = offset - textBefore.length();
			try {
				document.replace(zeroOffset, textBefore.length(), proposal);
				nextSelection = zeroOffset + proposal.length();
			} catch (BadLocationException e) {
				AsciiDoctorEditorUtil.logError("Not able to replace by proposal:" + word +", zero offset:"+zeroOffset+", textBefore:"+textBefore, e);
			}

		}

		@Override
		public Point getSelection(IDocument document) {
			Point point = new Point(nextSelection, 0);
			return point;
		}

		@Override
		public String getAdditionalProposalInfo() {
			return null;
		}

		@Override
		public String getDisplayString() {
			return word;
		}

		@Override
		public Image getImage() {
			return null;
		}

		@Override
		public IContextInformation getContextInformation() {
			return null;
		}

		@Override
		public StyledString getStyledDisplayString(IDocument document, int offset,
				BoldStylerProvider boldStylerProvider) {
			if (styledString != null) {
				return styledString;
			}
			styledString = new StyledString();
			styledString.append(word);
			try {

				int enteredTextLength = textBefore.length();
				int indexOfTextBefore = word.toLowerCase().indexOf(textBefore.toLowerCase());

				if (indexOfTextBefore != -1) {
					styledString.setStyle(indexOfTextBefore, enteredTextLength, boldStylerProvider.getBoldStyler());
				}
			} catch (RuntimeException e) {
				AsciiDoctorEditorUtil.logError("Not able to set styles for proposal:" + word, e);
			}
			return styledString;
		}

	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	public boolean isAddingSpaceAtEnd() {
		return true;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	public ICompletionListener getCompletionListener() {
		return this;
	}

	/* completion listener parts: */

	@Override
	public void assistSessionStarted(ContentAssistEvent event) {
		simpleWordCompletion.reset();

		AsciiDoctorEditorPreferences preferences = AsciiDoctorEditorPreferences.getInstance();
		boolean addKeyWords = preferences.getBooleanPreference(P_CODE_ASSIST_ADD_KEYWORDS);
		boolean addSimpleWords = preferences.getBooleanPreference(P_CODE_ASSIST_ADD_SIMPLEWORDS);

		if (addSimpleWords) {
			simpleWordCompletion.setWordListBuilder(WORD_LIST_BUILDER);
		} else {
			simpleWordCompletion.setWordListBuilder(NO_WORD_BUILDER);
		}
		if (addKeyWords) {
			addAllAsciiDoctorKeyWords();
		}
	}

	protected void addAllAsciiDoctorKeyWords() {
		for (DocumentKeyWord keyword : AsciiDoctorCommandKeyWords.values()) {
			addKeyWord(keyword);
		}
		for (DocumentKeyWord keyword : AsciiDoctorLanguageKeyWords.values()) {
			addKeyWord(keyword);
		}
		for (DocumentKeyWord keyword : AsciiDoctorSpecialVariableKeyWords.values()) {
			addKeyWord(keyword);
		}
	}

	protected void addKeyWord(DocumentKeyWord keyword) {
		simpleWordCompletion.add(keyword.getText());
	}

	@Override
	public void assistSessionEnded(ContentAssistEvent event) {
		simpleWordCompletion.reset();// clean up...
	}

	@Override
	public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {

	}

	private static class NoWordListBuilder implements WordListBuilder {

		private NoWordListBuilder() {

		}

		private List<String> list = new ArrayList<>(0);

		@Override
		public List<String> build(String source) {
			return list;
		}

	}
}
