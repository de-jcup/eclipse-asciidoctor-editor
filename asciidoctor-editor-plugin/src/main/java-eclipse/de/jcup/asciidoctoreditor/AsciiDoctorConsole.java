package de.jcup.asciidoctoreditor;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;

public class AsciiDoctorConsole {

	public static void error(String message) {
		MessageConsole myConsole = output(message);
		
		if (AsciiDoctorEditorPreferences.getInstance().isConsoleAlwaysShownOnOutput()){
			getConsoleManager().showConsoleView(myConsole);
		}
	}

	protected static MessageConsole output(String message) {
		MessageConsole myConsole = findConsole("Asciidoctor");
		MessageConsoleStream out = myConsole.newMessageStream();
		out.println(message);
		return myConsole;
	}

	public static MessageConsole findConsole(String name) {
		IConsoleManager conMan = getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName())) {
				return (MessageConsole) existing[i];
			}
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, EclipseUtil.createImageDescriptor("icons/asciidoctor-editor.png", AsciiDoctorEditorActivator.PLUGIN_ID));
		conMan.addConsoles(new IConsole[] { myConsole });
		
		return myConsole;
	}

	protected static IConsoleManager getConsoleManager() {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		return conMan;
	}

}
