package de.jcup.asciidoctoreditor.codeassist;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.ui.IEditorPart;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.eclipse.commons.codeassist.AbstractWordCodeCompletition;
import de.jcup.eclipse.commons.codeassist.ProposalProvider;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class AsciidocIncludeProposalSupport extends AbstractWordCodeCompletition {

    private AsciidocIncludeProposalCalculator calculator = new AsciidocIncludeProposalCalculator();
    
    public AsciidocIncludeProposalSupport(){
    }
    
    @Override
    public Set<ProposalProvider> calculate(String text, int index) {
        IEditorPart activeEditor = EclipseUtil.getActiveEditor();
        if (! (activeEditor instanceof AsciiDoctorEditor) || ! AsciiDoctorEditorPreferences.getInstance().isDynamicCodeAssistForIncludesEnabled()) {
            return Collections.emptySet();
        }
        AsciiDoctorEditor editor = (AsciiDoctorEditor) activeEditor;
        File file = editor.getEditorFileOrNull();
        if (file==null) {
            return Collections.emptySet();
        }
        Set<ProposalProvider> set = new LinkedHashSet<ProposalProvider>();
        Set<AsciidocIncludeProposalData> asciidocIncludeProposalData = calculator.calculate(file,text,index);
        for (AsciidocIncludeProposalData d: asciidocIncludeProposalData) {
            set.add(new AsciidocIncludeProposalProvider(d));
        }
        return set;
        
    }

    @Override
    public void reset() {

    }
    
    public class AsciidocIncludeProposalProvider implements ProposalProvider{
        
        private AsciidocIncludeProposalData asciidocIncludeProposalData;

        private AsciidocIncludeProposalProvider(AsciidocIncludeProposalData asciidocIncludeProposalData){
            this.asciidocIncludeProposalData=asciidocIncludeProposalData;
        }

        @Override
        public int compareTo(ProposalProvider o) {
            return 0;
        }

        @Override
        public List<String> getCodeTemplate() {
            return Arrays.asList(asciidocIncludeProposalData.getInclude());
        }

        @Override
        public String getLabel() {
           return asciidocIncludeProposalData.getLabel();
        }
        
    }

}
