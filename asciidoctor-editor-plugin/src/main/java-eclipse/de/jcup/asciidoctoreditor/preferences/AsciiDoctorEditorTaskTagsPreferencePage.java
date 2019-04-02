package de.jcup.asciidoctoreditor.preferences;

import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;
import de.jcup.eclipse.commons.tasktags.AbstractTaskTagsPreferencePage;

public class AsciiDoctorEditorTaskTagsPreferencePage extends AbstractTaskTagsPreferencePage{

	public AsciiDoctorEditorTaskTagsPreferencePage() {
		super(AsciiDoctorEditorActivator.getDefault().getTaskSupportProvider(), "Asciidoctor TODOs","Define your TODOs, FIXMEs inside your documentation");
	}

}
