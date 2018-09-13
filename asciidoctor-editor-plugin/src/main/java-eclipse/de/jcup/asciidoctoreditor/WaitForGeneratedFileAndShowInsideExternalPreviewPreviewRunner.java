package de.jcup.asciidoctoreditor;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;

class WaitForGeneratedFileAndShowInsideExternalPreviewPreviewRunner implements EnsureFileRunnable {

	private final AsciiDoctorEditor asciiDoctorEditor;
	private IProgressMonitor monitor;

	WaitForGeneratedFileAndShowInsideExternalPreviewPreviewRunner(AsciiDoctorEditor asciiDoctorEditor, IProgressMonitor monitor) {
		this.asciiDoctorEditor = asciiDoctorEditor;
		this.monitor = monitor;
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		try {
			File temporaryExternalPreviewFile = this.asciiDoctorEditor.getTemporaryExternalPreviewFile();
			while (this.asciiDoctorEditor.isNotCanceled(monitor)
					&& (temporaryExternalPreviewFile == null || !temporaryExternalPreviewFile.exists())) {
				if (System.currentTimeMillis() - start > 20000) {
					// after 20 seconds there seems to be no chance to get
					// the generated preview file back
					MessageDialog.openWarning(EclipseUtil.getActiveWorkbenchShell(), "Asciidoctor Editor", "Generated HTML output not found - maybe it's still in generation.\n\nPlease wait and try again.");
					return;
				}
				Thread.sleep(300);
			}
			AsciiDoctorEditorUtil.openFileInExternalBrowser(temporaryExternalPreviewFile);

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} 

	}
	
}