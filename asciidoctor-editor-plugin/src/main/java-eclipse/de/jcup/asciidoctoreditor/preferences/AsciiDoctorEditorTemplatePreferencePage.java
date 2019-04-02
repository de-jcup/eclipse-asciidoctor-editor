package de.jcup.asciidoctoreditor.preferences;

import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;
import de.jcup.eclipse.commons.templates.TemplateSupportPreferencePage;

public class AsciiDoctorEditorTemplatePreferencePage extends TemplateSupportPreferencePage{

    public AsciiDoctorEditorTemplatePreferencePage() {
        super(AsciiDoctorEditorActivator.getDefault().getTemplateSupportProvider());
    }

}
