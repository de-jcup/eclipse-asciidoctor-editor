package de.jcup.asciidoctoreditor.codeassist;
import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;
import de.jcup.eclipse.commons.codeassist.SupportableContentAssistProcessor;

public class PlantumlContentAssistProcessor extends SupportableContentAssistProcessor {

    public PlantumlContentAssistProcessor(){
        super(new PlantUMLKeywordContentAssistSupport(AsciiDoctorEditorActivator.getDefault()));
    }
    
}