package de.jcup.asciidoctoreditor;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsoleStream;

import de.jcup.asciidoctoreditor.console.AsciiDoctorConsole;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;

public class AsciiDoctorConsoleUtil {

	/**
	 * Using this method will print out a error message. If "show always consolole on error" is enabled
	 * in preferences the console will be opened if not visible
	 * @param message
	 */
	public static void error(String message) {
		output(message);

		if (AsciiDoctorEditorPreferences.getInstance().isConsoleAlwaysShownOnError()) {
			showConsole();
		}
	}

	/**
	 * Will output to {@link AsciiDoctorConsole} instance
	 * @param message
	 */
	public static void output(String message) {
		AsciiDoctorConsole myConsole = findConsole();
		MessageConsoleStream out = myConsole.newMessageStream();
		out.println(message);
	}

	public static void showConsole() {
		getConsoleManager().showConsoleView(findConsole());
	}

	private static AsciiDoctorConsole findConsole() {
		IConsoleManager conMan = getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (existing[i] instanceof AsciiDoctorConsole) {
				return (AsciiDoctorConsole) existing[i];
			}
		// no console found, so create a new one
		AsciiDoctorConsole asciiDoctorConsole = new AsciiDoctorConsole(EclipseUtil
				.createImageDescriptor("icons/asciidoctor-editor.png", AsciiDoctorEditorActivator.PLUGIN_ID));
		conMan.addConsoles(new IConsole[] { asciiDoctorConsole });

		return asciiDoctorConsole;
	}

	private static IConsoleManager getConsoleManager() {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		return conMan;
	}

}
