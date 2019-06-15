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
package de.jcup.asciidoctoreditor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.util.EclipseUtil;

public class OpenWithAsciiDoctorEditor extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IFile file = getSelectedFile();
		if (file==null){
			return null;
		}
		IWorkbenchPage page = EclipseUtil.getActivePage();
		if (page==null){
			return null;
		}
		try {
			page.openEditor(new FileEditorInput(file), AsciiDoctorEditor.EDITOR_ID);
		} catch (PartInitException e) {
			throw new ExecutionException("Was not able to open asciidoctor editor for file:"+file.getName(),e);
		}
		return null;
	}
	
	protected IFile getSelectedFile() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null) {
			return null;
		}
		
		ISelection selection = window.getSelectionService().getSelection();
		if (! (selection instanceof IStructuredSelection)){
			return null;
		}
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		
		Object firstElement = structuredSelection.getFirstElement();
		if (!(firstElement instanceof IAdaptable)) {
			return null;
		}

		IFile file = (IFile) ((IAdaptable) firstElement).getAdapter(IFile.class);
		return file;
	}
	
}
