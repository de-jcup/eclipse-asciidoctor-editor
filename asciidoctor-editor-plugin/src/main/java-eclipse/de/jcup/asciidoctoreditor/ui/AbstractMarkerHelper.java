/*
 * Copyright 2016 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.texteditor.MarkerUtilities;

import de.jcup.asciidoctoreditor.AdaptedFromEGradle;
import de.jcup.asciidoctoreditor.util.EclipseUtil;
import de.jcup.eclipse.commons.SimpleStringUtils;

@AdaptedFromEGradle
abstract class AbstractMarkerHelper {
	protected String markerType;

	private IMarker findMarker(IResource resource, String message, int lineNumber, String type) throws CoreException {
		IMarker[] marker = resource.findMarkers(type, true, IResource.DEPTH_ZERO);
		for (int i = 0; i < marker.length; i++) {
			IMarker currentMarker = marker[i];
			if (currentMarker == null) {
				continue;
			}
			Object lineNrAttribute = currentMarker.getAttribute(IMarker.LINE_NUMBER);
			String markerLineNumber = null;
			if (lineNrAttribute != null) {
				markerLineNumber = lineNrAttribute.toString();
			}
			Object messageAttribute = currentMarker.getAttribute(IMarker.MESSAGE);
			String markerMessage = null;
			if (messageAttribute != null) {
				markerMessage = messageAttribute.toString();
			}
			boolean sameMessageAndLineNr = SimpleStringUtils.equals(markerLineNumber, String.valueOf(lineNumber))
					&& SimpleStringUtils.equals(markerMessage, message);
			if (sameMessageAndLineNr) {
				return currentMarker;
			}
		}
		return null;
	}

	public void createScriptMarker(int severity, IResource resource, String message, int lineNumber, int charStart, int charEnd)
			throws CoreException {
		createMarker(resource, message, lineNumber, markerType, severity, charStart, charEnd);
	}

	private void createMarker(IResource resource, String message, int lineNumber, String markerType, int severity,
			int charStart, int charEnd) throws CoreException {
		if (lineNumber <= 0)
			lineNumber = 1;
		IMarker marker = findMarker(resource, message, lineNumber, markerType);
		if (marker == null) {
			HashMap<String, Object> map = new HashMap<>();
			map.put(IMarker.SEVERITY, new Integer(severity));
			map.put(IMarker.LOCATION, resource.getFullPath().toOSString());
			map.put(IMarker.MESSAGE, message);
			MarkerUtilities.setLineNumber(map, lineNumber);
			MarkerUtilities.setMessage(map, message);
			if (charStart != -1) {
				MarkerUtilities.setCharStart(map, charStart);
				MarkerUtilities.setCharEnd(map, charEnd);
			}
			internalCreateMarker(resource, map, markerType);
		}
	}

	/**
	 * Creates a marker on the given resource with the given type and
	 * attributes.
	 * <p>
	 * This method modifies the workspace (progress is not reported to the
	 * user).
	 * </p>
	 *
	 * @param resource
	 *            the resource
	 * @param attributes
	 *            the attribute map
	 * @param markerType
	 *            the type of marker
	 * @throws CoreException
	 *             if this method fails
	 * @see IResource#createMarker(java.lang.String)
	 */
	private void internalCreateMarker(final IResource resource, final Map<String, Object> attributes,
			final String markerType) throws CoreException {

		IWorkspaceRunnable r = new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource.createMarker(markerType);
				marker.setAttributes(attributes);
				handleMarkerAdded(marker);

			}
		};

		resource.getWorkspace().run(r, null, IWorkspace.AVOID_UPDATE, null);
	}

	protected void handleMarkerAdded(IMarker marker) {
		/* do nothing per default */
	}

	/**
	 * Removes all markers from this file having defined marker type
	 * 
	 * @param resource
	 */
	public void removeMarkers(IResource resource) {
		if (resource == null) {
			return;
		}
		removeMarkers(resource, markerType);

	}

	private IMarker[] removeMarkers(IResource resource, String markerType) {
		if (resource == null) {
			/* maybe sync problem - guard close */
			return new IMarker[] {};
		}
		IMarker[] tasks = null;
		if (resource != null) {
			try {
				tasks = resource.findMarkers(markerType, true, IResource.DEPTH_ZERO);
				for (int i = 0; i < tasks.length; i++) {
					tasks[i].delete();
				}

			} catch (CoreException e) {
				EclipseUtil.logError("Was not able to delete markers", e);
			}
		}
		if (tasks == null) {
			tasks = new IMarker[] {};
		}
		return tasks;
	}

}