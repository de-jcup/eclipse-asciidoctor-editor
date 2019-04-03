package de.jcup.asciidoctor.converter.markdown;

import nl.jworks.markdown_to_asciidoc.Converter;

public class MarkdownFilesToAsciidoctorConverter {

    public String convertString(String markdown) {
        return Converter.convertMarkdownToAsciiDoc(markdown);
    }
    
    
}
