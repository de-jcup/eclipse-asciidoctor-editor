package de.jcup.asciidoctor.converter;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.jcup.asciidoctor.converter.markdown.MarkdownFilesToAsciidoctorConverter;

public class ConvertMarkdownFilesHandler extends AbstractHandler{

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        MarkdownFilesToAsciidoctorConverter converter = new MarkdownFilesToAsciidoctorConverter();
        
        return null;
    }


}
