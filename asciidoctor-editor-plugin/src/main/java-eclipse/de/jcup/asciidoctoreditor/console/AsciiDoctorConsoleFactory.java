package de.jcup.asciidoctoreditor.console;

import org.eclipse.ui.console.IConsoleFactory;

import de.jcup.asciidoctoreditor.AsciiDoctorConsoleUtil;

public class AsciiDoctorConsoleFactory implements IConsoleFactory{

	@Override
	public void openConsole() {
		AsciiDoctorConsoleUtil.showConsole();
	}

}
