package de.jcup.asciidoctoreditor.codeassist;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;

import de.jcup.asciidoctoreditor.outline.AsciiDoctorEditorOutlineLabelProvider;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.ui.AsciidoctorIconConstants;
import de.jcup.eclipse.commons.PluginContextProvider;
import de.jcup.eclipse.commons.codeassist.ProposalInfoProvider;
import de.jcup.eclipse.commons.codeassist.ProposalProviderContentAssistSupport;

public class DynamicPlantumlContentAssistSupport extends ProposalProviderContentAssistSupport {

    public DynamicPlantumlContentAssistSupport(PluginContextProvider provider) {
        super(provider, new AsciidocReferenceProposalSupport("plantuml::", new DiagramBaseParentResolver(), new DynamicPlantumlEnabledResolver(),
                new CodeAssistFileFilter(".puml", ".plantuml", ".pu", ".iuml")));
    }

    @Override
    protected ProposalInfoProvider createProposalInfoBuilder() {
        return new ProposalInfoProvider() {

            @Override
            public Object getProposalInfo(IProgressMonitor monitor, Object target) {
                if (!(target instanceof String)) {
                    return null;
                }
                String word = (String) target;
                return word;
            }

            @Override
            public Image getImage(Object target) {
                return AsciiDoctorEditorOutlineLabelProvider.getImage(AsciidoctorIconConstants.PATH_OUTLINE_ICON_PLANTUML);
            }
        };
    }

    private static class DynamicPlantumlEnabledResolver implements EnableStateResolver {

        @Override
        public boolean isEnabled() {
            return AsciiDoctorEditorPreferences.getInstance().isDynamicCodeAssistForPlantumlEnabled();
        }

    }

}
