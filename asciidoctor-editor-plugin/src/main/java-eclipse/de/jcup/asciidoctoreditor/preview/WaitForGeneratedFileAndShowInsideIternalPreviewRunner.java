/*
 * Copyright 2018 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.asciidoctoreditor.preview;

import static de.jcup.asciidoctoreditor.EclipseUtil.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.AsciiDoctorEditorUtil;
import de.jcup.asciidoctoreditor.EnsureFileRunnable;

public class WaitForGeneratedFileAndShowInsideIternalPreviewRunner implements EnsureFileRunnable {

	private final AsciiDoctorEditor asciiDoctorEditor;
	private IProgressMonitor monitor;

	public WaitForGeneratedFileAndShowInsideIternalPreviewRunner(AsciiDoctorEditor asciiDoctorEditor, IProgressMonitor monitor) {
		this.asciiDoctorEditor = asciiDoctorEditor;
		this.monitor = monitor;
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		boolean aquired = false;
		try {
			while (asciiDoctorEditor.isNotCanceled(monitor)
					&& (asciiDoctorEditor.getTemporaryExternalPreviewFile() == null || !asciiDoctorEditor.getTemporaryExternalPreviewFile().exists())) {
				if (System.currentTimeMillis() - start > 20000) {
					// after 20 seconds there seems to be no chance to get
					// the generated preview file back
					asciiDoctorEditor.getBrowserAccess().safeBrowserSetText(
							"<html><body><h3>Preview file generation timed out, so preview not available.</h3></body></html>");
					return;
				}
				Thread.sleep(300);
			}
			aquired = asciiDoctorEditor.getOutputBuildSemaphore().tryAcquire(5, TimeUnit.SECONDS);

			safeAsyncExec(() -> {

				try {
					URL url = asciiDoctorEditor.getTemporaryExternalPreviewFile().toURI().toURL();
					String foundURL = asciiDoctorEditor.getBrowserAccess().getUrl();
					try {
						URL formerURL = new URL(asciiDoctorEditor.getBrowserAccess().getUrl());
						foundURL = formerURL.toExternalForm();
					} catch (MalformedURLException e) {
						/* ignore - about pages etc. */
					}
					String externalForm = url.toExternalForm();
					if (!externalForm.equals(foundURL)) {
						asciiDoctorEditor.getBrowserAccess().setUrl(externalForm);
					} else {
						asciiDoctorEditor.getBrowserAccess().refresh();
					}

				} catch (MalformedURLException e) {
					AsciiDoctorEditorUtil.logError("Was not able to use malformed URL", e);
					asciiDoctorEditor.getBrowserAccess().safeBrowserSetText("<html><body><h3>URL malformed</h3></body></html>");
				}
			});

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} finally {
			if (aquired == true) {
				asciiDoctorEditor.getOutputBuildSemaphore().release();
			}
		}

	}
}