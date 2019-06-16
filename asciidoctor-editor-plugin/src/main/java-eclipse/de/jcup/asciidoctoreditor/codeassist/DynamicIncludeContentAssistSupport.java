package de.jcup.asciidoctoreditor.codeassist;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;

import de.jcup.asciidoctoreditor.outline.AsciiDoctorEditorOutlineLabelProvider;
import de.jcup.asciidoctoreditor.ui.AsciidoctorIconConstants;
import de.jcup.eclipse.commons.PluginContextProvider;
import de.jcup.eclipse.commons.codeassist.ProposalInfoProvider;
import de.jcup.eclipse.commons.codeassist.ProposalProviderContentAssistSupport;

public class DynamicIncludeContentAssistSupport extends ProposalProviderContentAssistSupport{

    public DynamicIncludeContentAssistSupport(PluginContextProvider provider) {
        super(provider, new AsciidocIncludeProposalSupport());
    }
    
    @Override
    protected ProposalInfoProvider createProposalInfoBuilder() {
        return new ProposalInfoProvider() {
            
            @Override
            public Object getProposalInfo(IProgressMonitor monitor, Object target) {
                if (! (target instanceof String)){
                    return null;
                }
                String word = (String) target;
                return word;
            }

            @Override
            public Image getImage(Object target) {
                return AsciiDoctorEditorOutlineLabelProvider.getImage(AsciidoctorIconConstants.PATH_OUTLINE_ICON_INCLUDE);
            }
        };
    }

}
