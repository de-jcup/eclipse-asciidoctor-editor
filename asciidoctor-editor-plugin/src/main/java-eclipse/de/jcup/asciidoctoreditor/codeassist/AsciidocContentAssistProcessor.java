package de.jcup.asciidoctoreditor.codeassist;
import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;
import de.jcup.eclipse.commons.codeassist.SupportableContentAssistProcessor;

public class AsciidocContentAssistProcessor extends SupportableContentAssistProcessor {

    public AsciidocContentAssistProcessor(){
        super(new AsciidocKeywordContentAssistSupport(AsciiDoctorEditorActivator.getDefault()));
    }
}