package de.jcup.asciidoctoreditor.codeassist;

import de.jcup.eclipse.commons.PluginContextProvider;
import de.jcup.eclipse.commons.codeassist.ProposalProviderContentAssistSupport;

public class DynamicIncludeContentAssistSupport extends ProposalProviderContentAssistSupport{

    public DynamicIncludeContentAssistSupport(PluginContextProvider provider) {
        super(provider, new AsciidocIncludeProposalSupport());
    }

}
