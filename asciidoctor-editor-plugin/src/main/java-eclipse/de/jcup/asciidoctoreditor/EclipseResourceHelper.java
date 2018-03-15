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
package de.jcup.asciidoctoreditor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.manipulation.ContainerCreator;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.FileInfoMatcherDescription;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceFilterDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

public class EclipseResourceHelper {
	public static EclipseResourceHelper DEFAULT = new EclipseResourceHelper();
	private static String FILE_FILTER_ID = "org.eclipse.ui.ide.patternFilterMatcher";

	private final IProgressMonitor NULL_MONITOR = new NullProgressMonitor();

	public void addFileFilter(IProject newProject, String pattern, IProgressMonitor monitor) throws CoreException {
		FileInfoMatcherDescription matcherDescription = new FileInfoMatcherDescription(FILE_FILTER_ID, pattern);
		/*
		 * ignore the generated files - .project and .gitignore at navigator
		 * etc.
		 */
		newProject.createFilter(IResourceFilterDescription.EXCLUDE_ALL | IResourceFilterDescription.FILES,
				matcherDescription, IResource.BACKGROUND_REFRESH, monitor);
	}

	public IFile createFile(IFolder folder, String name, String contents) throws CoreException {
		return createFile(folder.getFile(name), name, contents);
	}

	public IFile createFile(IProject project, String name, String contents) throws CoreException {
		return createFile(project.getFile(name), name, contents);
	}

	public IFolder createFolder(IPath path) throws CoreException {
		return createFolder(path, null);
	}

	public IFolder createFolder(String portableFolderPath) throws CoreException {
		return createFolder(portableFolderPath, null);
	}

	public IFolder createFolder(String portableFolderPath, IProgressMonitor monitor) throws CoreException {
		Path fullPath = new Path(portableFolderPath);
		return createFolder(fullPath, monitor);
	}

	public IFolder createFolder(IPath path, IProgressMonitor monitor) throws CoreException {
		if (monitor == null) {
			monitor = NULL_MONITOR;
		}
		ContainerCreator creator = new ContainerCreator(ResourcesPlugin.getWorkspace(), path);
		IContainer container = creator.createContainer(monitor);
		if (container instanceof IFolder) {
			return (IFolder) container;
		}
		return null;
	}

	public IFile createLinkedFile(IContainer container, IPath linkPath, File linkedFileTarget) throws CoreException {
		IFile iFile = container.getFile(linkPath);
		iFile.createLink(new Path(linkedFileTarget.getAbsolutePath()), IResource.ALLOW_MISSING_LOCAL, NULL_MONITOR);
		return iFile;
	}

	public IFile createLinkedFile(IContainer container, IPath linkPath, Plugin plugin, IPath linkedFileTargetPath)
			throws CoreException {
		File file = getFileInPlugin(plugin, linkedFileTargetPath);
		IFile iFile = container.getFile(linkPath);
		iFile.createLink(new Path(file.getAbsolutePath()), IResource.ALLOW_MISSING_LOCAL, NULL_MONITOR);
		return iFile;
	}

	public IFolder createLinkedFolder(IContainer container, IPath linkPath, File linkedFolderTarget)
			throws CoreException {
		IFolder folder = container.getFolder(linkPath);
		folder.createLink(new Path(linkedFolderTarget.getAbsolutePath()), IResource.ALLOW_MISSING_LOCAL, NULL_MONITOR);
		return folder;
	}

	public IFolder createLinkedFolder(IContainer container, IPath linkPath, Plugin plugin, IPath linkedFolderTargetPath)
			throws CoreException {
		File file = getFileInPlugin(plugin, linkedFolderTargetPath);
		IFolder iFolder = container.getFolder(linkPath);
		iFolder.createLink(new Path(file.getAbsolutePath()), IResource.ALLOW_MISSING_LOCAL, NULL_MONITOR);
		return iFolder;
	}

	public IProject createLinkedProject(String projectName, Plugin plugin, IPath linkPath) throws CoreException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = workspace.getRoot().getProject(projectName);

		IProjectDescription desc = workspace.newProjectDescription(projectName);
		File file = getFileInPlugin(plugin, linkPath);
		IPath projectLocation = new Path(file.getAbsolutePath());
		if (Platform.getLocation().equals(projectLocation))
			projectLocation = null;
		desc.setLocation(projectLocation);

		project.create(desc, NULL_MONITOR);
		if (!project.isOpen())
			project.open(NULL_MONITOR);

		return project;
	}

	public File createTempFileInPlugin(Plugin plugin, IPath path) {
		IPath stateLocation = plugin.getStateLocation();
		stateLocation = stateLocation.append(path);
		return stateLocation.toFile();
	}

	public File getFileInPlugin(Plugin plugin, IPath path) throws CoreException {
		try {
			URL installURL = plugin.getBundle().getEntry(path.toString());
			URL localURL = FileLocator.toFileURL(installURL);
			return new File(localURL.getFile());
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, AsciiDoctorEditorActivator.PLUGIN_ID,
					"cannot get file in plugin from path:" + path, e));
		}
	}

	/**
	 * Returns the file or <code>null</code>
	 * 
	 * @param path
	 * @return file or <code>null</code>
	 * @throws CoreException
	 */
	public File toFile(IPath path) throws CoreException {
		if (path == null) {
			return null;
		}
		IFileStore fileStore = FileBuffers.getFileStoreAtLocation(path);

		File file = null;
		file = fileStore.toLocalFile(EFS.NONE, NULL_MONITOR);
		return file;
	}

	public File toFile(IResource resource) throws CoreException {
		if (resource == null) {
			return toFile((IPath) null);
		}
		return toFile(resource.getLocation());
	}

	public File getFileInPlugin(String path) throws IOException {
		return getFileInPlugin(path,AsciiDoctorEditorActivator.PLUGIN_ID);
	}
	public File getFileInPlugin(String path, String pluginId) throws IOException {
		Bundle bundle = Platform.getBundle(pluginId);
		URL url = bundle.getEntry(path);
		if (url == null) {
			/* PDE workaround */
			String path2 = "bin/" + path;
			url = bundle.getEntry(path2);
			if (url == null) {
				return null;
			}

		}
		URL resolvedFileURL = FileLocator.toFileURL(url);
		if (resolvedFileURL == null) {
			throw new FileNotFoundException("Cannot convert URL to file:" + resolvedFileURL);
		}

		// We need to use the 3-arg constructor of URI in order to properly
		// escape file system chars
		URI resolvedURI;
		try {
			resolvedURI = new URI(resolvedFileURL.getProtocol(), resolvedFileURL.getPath(), null);
			File file = new File(resolvedURI);
			if (!file.exists()) {
				throw new FileNotFoundException("Cannot convert URL to file:" + resolvedFileURL);
			}
			return file;
		} catch (URISyntaxException e) {
			throw new IOException("Cannot find file at resolvedFileURL:" + resolvedFileURL, e);
		}
	}

	/**
	 * Returns the IFile representation for given file or <code>null</code> if
	 * file not in workspace
	 * 
	 * @param file
	 * @return file or null
	 * @deprecated does not work correctly. Better: IFileStore fileStore =
	 *             EFS.getLocalFileSystem().getStore(localFile.toURI());
	 */
	public IFile toIFile(File file) {
		IPath path = Path.fromOSString(file.getAbsolutePath());
		return toIFile(path);
	}

	public IFile toIFile(IPath path) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IFile fileResult = workspace.getRoot().getFile(path);
		return fileResult;
	}

	public IFile toIFile(String pathString) {
		IPath path = Path.fromOSString(pathString);
		return toIFile(path);
	}

	public IPath toPath(File tempFolder) {
		if (tempFolder == null) {
			throw new IllegalArgumentException("'tempFolder' may not be null");
		}
		IPath path = Path.fromOSString(tempFolder.getAbsolutePath());
		return path;
	}

	private IFile createFile(IFile file, String name, String contents) throws CoreException {
		if (contents == null)
			contents = "";
		InputStream inputStream = new ByteArrayInputStream(contents.getBytes());
		file.create(inputStream, true, NULL_MONITOR);
		return file;
	}

	void deleteRecursive(File file) {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				deleteRecursive(child);
			}
			file.delete();
		} else {
			file.delete();
		}
	}

}