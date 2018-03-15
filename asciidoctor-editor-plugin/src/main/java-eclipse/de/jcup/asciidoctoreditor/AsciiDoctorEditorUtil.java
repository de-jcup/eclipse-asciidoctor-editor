/*
 * Copyright 2017 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor;

import java.io.File;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.script.AsciiDoctorError;

public class AsciiDoctorEditorUtil {
	private static final IProgressMonitor NULL_MONITOR = new NullProgressMonitor();
	
	private static UnpersistedMarkerHelper scriptProblemMarkerHelper = new UnpersistedMarkerHelper(
			"de.jcup.asciidoctoreditor.script.problem");

	public static AsciiDoctorEditorPreferences getPreferences() {
		return AsciiDoctorEditorPreferences.getInstance();
	}
	
	/**
	 * Returns the file or <code>null</code>
	 * @param path
	 * @return file or <code>null</code>
	 * @throws CoreException
	 */
	public static File toFile(IPath path) throws CoreException {
		if (path==null){
			return null;
		}
		IFileStore fileStore = FileBuffers.getFileStoreAtLocation(path);
		if (fileStore==null){
			return null;
		}
		File file = null;
		file = fileStore.toLocalFile(EFS.NONE, NULL_MONITOR);
		return file;
	}

	public static File toFile(IResource resource) throws CoreException {
		if (resource==null){
			return toFile((IPath)null);
		}
		return toFile(resource.getLocation());
	}

	public static void logInfo(String info) {
		getLog().log(new Status(IStatus.INFO, AsciiDoctorEditorActivator.PLUGIN_ID, info));
	}

	public static void logWarning(String warning) {
		getLog().log(new Status(IStatus.WARNING, AsciiDoctorEditorActivator.PLUGIN_ID, warning));
	}

	public static void logError(String error, Throwable t) {
		getLog().log(new Status(IStatus.ERROR, AsciiDoctorEditorActivator.PLUGIN_ID, error, t));
	}

	public static void removeScriptErrors(IEditorPart editor) {
		if (editor == null) {
			return;
		}
		IEditorInput input = editor.getEditorInput();
		if (input == null) {
			return;
		}
		IResource editorResource = input.getAdapter(IResource.class);
		if (editorResource == null) {
			return;
		}
		scriptProblemMarkerHelper.removeMarkers(editorResource);
	}

	public static void addScriptError(IEditorPart editor, int line, AsciiDoctorError error, int severity) {
		if (editor == null) {
			return;
		}
		if (error == null) {
			return;
		}

		IEditorInput input = editor.getEditorInput();
		if (input == null) {
			return;
		}
		IResource editorResource = input.getAdapter(IResource.class);
		if (editorResource == null) {
			return;
		}
		try {
			scriptProblemMarkerHelper.createScriptMarker(severity, editorResource, error.getMessage(), line, error.getStart(),
					+ error.getEnd());
		} catch (CoreException e) {
			logError("Was not able to add error markers", e);
		}

	}

	private static ILog getLog() {
		ILog log = AsciiDoctorEditorActivator.getDefault().getLog();
		return log;
	}

}
