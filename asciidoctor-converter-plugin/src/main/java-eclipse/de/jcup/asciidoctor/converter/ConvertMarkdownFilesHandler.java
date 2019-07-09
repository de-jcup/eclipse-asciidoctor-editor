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
import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.jcup.asciidoctor.converter.markdown.MarkdownFilesToAsciidoctorConverter;

public class ConvertMarkdownFilesHandler extends AbstractConvertHandler{

    private MarkdownFilesToAsciidoctorConverter converter = new MarkdownFilesToAsciidoctorConverter();

    @Override
    protected Job createJob(File file, IResource resource) {
        return new Job("Convert mark down file to asciidoc") {
            
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    monitor.beginTask("convert markdown file to asciidoc", 2);
                    /* convert*/
                    try {
                        converter.convertToFiles(file);
                        monitor.worked(1);
                    } catch (IOException e) {
                       return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Was not able to convert file:"+file.getAbsolutePath(),e);
                    }
                    
                    /* refresh*/
                    resource.getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
                    monitor.worked(2);
                    
                    return Status.OK_STATUS;
                } catch (CoreException e) {
                    return e.getStatus();
                }
            }
        };
    }
    
}
