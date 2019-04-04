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

/**
 * Converter handler for folder - currently supporting only markdown, but maybe
 * in future more
 * 
 * @author albert
 *
 */
public class ConvertFolderToAsciidocHandler extends AbstractConvertHandler {

    ToAsciidocConverter[] converters = new ToAsciidocConverter[] {
            new MarkdownFilesToAsciidoctorConverter()        
    };
    
    @Override
    protected Job createJob(File file, IResource resource) {
        return new Job("Convert folder to asciidoc files") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    int done = 0;
                    monitor.beginTask("convert markdown file to asciidoc", converters.length + 1);
                    /* get selection */
                    /* convert */
                    try {
                        for (ToAsciidocConverter converter : converters) {
                            monitor.subTask("use converter " + converter.getName());
                            converter.convertToFiles(file);
                        }
                        monitor.worked(++done);
                    } catch (IOException e) {
                        return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Was not able to convert file:" + file.getAbsolutePath(), e);
                    }

                    /* refresh */
                    resource.getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
                    monitor.worked(++done);

                    return Status.OK_STATUS;
                } catch (CoreException e) {
                    return e.getStatus();
                }
            }
        };
    }


}
