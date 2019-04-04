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
