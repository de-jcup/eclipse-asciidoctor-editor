package de.jcup.asciidoctoreditor;

import org.eclipse.core.resources.IFile;

import de.jcup.eclipse.commons.tasktags.AbstractConfigurableTaskTagsSupportProvider;

public class AsciiDoctorEditorTaskTagsSupportProvider extends AbstractConfigurableTaskTagsSupportProvider{

	public AsciiDoctorEditorTaskTagsSupportProvider(AsciiDoctorEditorActivator plugin) {
		super(plugin);
	}

	@Override
	public boolean isLineCheckforTodoTaskNessary(String line, int lineNumber, String[] lines) {
		if (line==null){
			return false;
		}
		return line.contains("//");
	}

	@Override
	public String getTodoTaskMarkerId() {
		return "de.jcup.asciidoctoreditor.taskmarker";
	}

	@Override
	public boolean isFileHandled(IFile file) {
		if (file==null){
			return false;
		}
		String fileExtension = file.getFileExtension();
		if (fileExtension==null){
			return false;
		}
		return fileExtension.equals("adoc") || fileExtension.contentEquals("asciidoc");
	}

}
