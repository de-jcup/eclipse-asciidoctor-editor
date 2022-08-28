/*
 * Copyright 2021 Albert Tregnaghi
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

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import de.jcup.eclipse.commons.codeassist.DefaultPrefixCalculator;
import de.jcup.eclipse.commons.codeassist.PrefixCalculator;

/**
 * A delegating conent assist processor, which will delegate to the given
 * processor, if the current text does not start with the prefixes to block.
 *
 */
public class BlockingPrefixDelegateContentAssistProcessor implements IContentAssistProcessor {

    private PrefixCalculator prefixCalculator;

    private IContentAssistProcessor processor;

    private String[] prefixesToBlock;

    /**
     * Creates a blocking prefix delegate assist processor
     * 
     * @param processor
     * @param prefixesToBlock
     */
    public BlockingPrefixDelegateContentAssistProcessor(IContentAssistProcessor processor, String... prefixesToBlock) {
        this.processor = processor;
        this.prefixesToBlock = prefixesToBlock;
        this.prefixCalculator = new DefaultPrefixCalculator();
    }

    @Override
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
        String prefix = fetchPrefix(viewer, offset);
        if (isPrefixBlocked(prefix)) {
            return new ICompletionProposal[] {};
        }
        return processor.computeCompletionProposals(viewer, offset);
    }

    private String fetchPrefix(ITextViewer viewer, int offset) {
        return prefixCalculator.calculate(viewer.getDocument().get(), offset);
    }

    private boolean isPrefixBlocked(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return false;
        }
        for (String prefixToBlock : prefixesToBlock) {
            if (prefix.startsWith(prefixToBlock)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
        String prefix = fetchPrefix(viewer, offset);
        if (isPrefixBlocked(prefix)) {
            return new IContextInformation[] {};
        }
        return processor.computeContextInformation(viewer, offset);
    }

    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {
        return processor.getCompletionProposalAutoActivationCharacters();
    }

    @Override
    public char[] getContextInformationAutoActivationCharacters() {
        return processor.getContextInformationAutoActivationCharacters();
    }

    @Override
    public String getErrorMessage() {
        return processor.getErrorMessage();
    }

    @Override
    public IContextInformationValidator getContextInformationValidator() {
        return processor.getContextInformationValidator();
    }

}
