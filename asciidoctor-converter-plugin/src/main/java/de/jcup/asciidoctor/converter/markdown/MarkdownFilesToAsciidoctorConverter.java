package de.jcup.asciidoctor.converter.markdown;

import de.jcup.asciidoctor.converter.AbstractToAsciidoctorConverter;
import nl.jworks.markdown_to_asciidoc.Converter;

public class MarkdownFilesToAsciidoctorConverter extends AbstractToAsciidoctorConverter{
    
    protected String convertImpl(String markdown) {
        if (markdown==null) {
            return "";
        }
        return Converter.convertMarkdownToAsciiDoc(markdown);
    }

    @Override
    public String getName() {
        return "md2asciidoc";
    }

    @Override
    protected String getAcceptedFileEnding() {
        return ".md";
    }

}
