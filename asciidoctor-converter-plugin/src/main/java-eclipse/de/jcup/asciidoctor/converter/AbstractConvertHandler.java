/*
 * Copyright 2019 Albert Tregnaghi
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
package de.jcup.asciidoctor.converter;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public abstract class AbstractConvertHandler extends AbstractHandler{

    @Override
    public final Object execute(ExecutionEvent event) throws ExecutionException {
        
        final IResource resource = getSelectedIFile();
        if (resource==null) {
            return null;
        }
        final File file; 
        try {
            file = EclipseResourceHelper.DEFAULT.toFile(resource);
            if (file == null) {
                return null;
            }
        } catch (CoreException e1) {
            throw new ExecutionException("Cannot get selected file", e1);
        }
        
        
        Job job = createJob(file,resource);
        job.schedule();
        
        return null;
        
    }

    /**
     * Create conversion job
     * @param file never <code>null</code>
     * @param resource never <code>null</code>
     * @return job never <code>null</code>
     */
    protected abstract Job createJob(File file, IResource resource);

    private IResource getSelectedIFile() {
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

        IResource file = (IResource) ((IAdaptable) firstElement).getAdapter(IResource.class);
        return file;
    }

}
