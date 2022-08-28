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
package de.jcup.asciidoctoreditor.util;

import java.io.File;
import java.net.URL;

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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.script.ASPMarker;
import de.jcup.asciidoctoreditor.script.AsciiDoctorMarker;
import de.jcup.asciidoctoreditor.ui.UnpersistedMarkerHelper;

public class AsciiDoctorEditorUtil {
    private static final IProgressMonitor NULL_MONITOR = new NullProgressMonitor();

    private static UnpersistedMarkerHelper validationMarkerHelper = new UnpersistedMarkerHelper("de.jcup.asciidoctoreditor.script.problem");
    private static UnpersistedMarkerHelper aspMarkerHelper = new UnpersistedMarkerHelper("de.jcup.asciidoctoreditor.asp.marker");
    
    public static AsciiDoctorEditorPreferences getPreferences() {
        return AsciiDoctorEditorPreferences.getInstance();
    }

    public static final AsciiDoctorEditor findActiveAsciidoctorEditorOrNull() {
        IEditorPart editor = findActiveEditorOrNull();
        if (editor instanceof AsciiDoctorEditor) {
            return (AsciiDoctorEditor) editor;
        }
        return null;
    }

    public static final IEditorPart findActiveEditorOrNull() {
        IWorkbench workbench = PlatformUI.getWorkbench();
        if (workbench == null) {
            return null;
        }
        IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
        if (activeWorkbenchWindow == null) {
            return null;
        }
        IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
        if (activePage == null) {
            return null;
        }
        IEditorPart editor = activePage.getActiveEditor();
        return editor;
    }

    /**
     * Get image by path from image registry. If not already registered a new image
     * will be created and registered. If not createable a fallback image is used
     * instead
     * 
     * @param path
     * @return image
     */
    public static Image getImage(String path) {
        return EclipseUtil.getImage(path, AsciiDoctorEditorActivator.PLUGIN_ID);
    }

    public static ImageDescriptor createImageDescriptor(String path) {
        return EclipseUtil.createImageDescriptor(path, AsciiDoctorEditorActivator.PLUGIN_ID);
    }

    /**
     * Returns the file or <code>null</code>
     * 
     * @param path
     * @return file or <code>null</code>
     * @throws CoreException
     */
    public static File toFile(IPath path) throws CoreException {
        if (path == null) {
            return null;
        }
        IFileStore fileStore = FileBuffers.getFileStoreAtLocation(path);
        if (fileStore == null) {
            return null;
        }
        File file = null;
        file = fileStore.toLocalFile(EFS.NONE, NULL_MONITOR);
        return file;
    }

    public static File toFile(IResource resource) throws CoreException {
        if (resource == null) {
            return toFile((IPath) null);
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
        validationMarkerHelper.removeMarkers(editorResource);
        aspMarkerHelper.removeMarkers(editorResource);
    }

    public static void addAsciiDoctorMarker(IEditorPart editor, int line, AsciiDoctorMarker marker, int severity) {
        if (editor == null) {
            return;
        }
        if (marker == null) {
            return;
        }

        IEditorInput input = editor.getEditorInput();
        if (input == null) {
            return;
        }
        IResource editorResource = input.getAdapter(IResource.class);
        addAsciiDoctorMarker(line, marker, severity, editorResource);

    }

    public static void addAsciiDoctorMarker(int line, AsciiDoctorMarker marker, int severity, IResource editorResource) {
        if (editorResource == null) {
            return;
        }
        if (marker == null) {
            return;
        }
        try {
            if (marker instanceof ASPMarker) {
                aspMarkerHelper.createScriptMarker(severity, editorResource, marker.getMessage(), line, marker.getStart(), +marker.getEnd());
            } else {
                validationMarkerHelper.createScriptMarker(severity, editorResource, marker.getMessage(), line, marker.getStart(), +marker.getEnd());
            }
        } catch (CoreException e) {
            logError("Was not able to add error markers", e);
        }
    }

    private static ILog getLog() {
        ILog log = AsciiDoctorEditorActivator.getDefault().getLog();
        return log;
    }

    public static void openFileInExternalBrowser(File tempAdFile) {
        try {
            URL url = tempAdFile.toURI().toURL();
            // Open default external browser
            IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
            IWebBrowser externalBrowser = browserSupport.getExternalBrowser();
            externalBrowser.openURL(url);

        } catch (Exception ex) {
            AsciiDoctorEditorUtil.logError("Was not able to open url in external browser", ex);
        }
    }

    public static boolean isAsciidocFileExtension(String extension) {
        if (extension == null) {
            return false;
        }
        switch (extension) {
        case "adoc":
        case "asc":
        case "asciidoc":
        case "ad":
            return true;
        default:
            return false;
        }
    }

}
