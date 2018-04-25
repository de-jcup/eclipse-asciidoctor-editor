package de.jcup.asciidoctoreditor;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


public class AsciiDoctorEclipseLogAdapter implements LogAdapter {

	public static final LogAdapter INSTANCE = new AsciiDoctorEclipseLogAdapter();
	
	private AsciiDoctorEclipseLogAdapter(){
		
	}
	
	public void logInfo(String info) {
		getLog().log(new Status(IStatus.INFO, AsciiDoctorEditorActivator.PLUGIN_ID, info));
	}

	public void logWarn(String warning) {
		getLog().log(new Status(IStatus.WARNING, AsciiDoctorEditorActivator.PLUGIN_ID, warning));
	}

	public void logError(String error, Throwable t) {
		getLog().log(new Status(IStatus.ERROR, AsciiDoctorEditorActivator.PLUGIN_ID, error, t));
	}

	private static ILog getLog() {
		ILog log = AsciiDoctorEditorActivator.getDefault().getLog();
		return log;
	}
}
