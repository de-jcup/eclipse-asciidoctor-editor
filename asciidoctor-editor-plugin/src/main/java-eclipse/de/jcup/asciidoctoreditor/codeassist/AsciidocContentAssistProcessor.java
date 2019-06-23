package de.jcup.asciidoctoreditor.codeassist;
import static de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator.*;

import de.jcup.eclipse.commons.codeassist.SupportableContentAssistProcessor;
public class AsciidocContentAssistProcessor extends SupportableContentAssistProcessor {

    public AsciidocContentAssistProcessor(){
        super(new AsciidocKeywordContentAssistSupport(getDefault()), 
                new DynamicIncludeContentAssistSupport(getDefault()),
                new DynamicImageContentAssistSupport(getDefault()),
                new DynamicPlantumlContentAssistSupport(getDefault()),
                new DynamicDitaaContentAssistSupport(getDefault())
                        );
    }
}