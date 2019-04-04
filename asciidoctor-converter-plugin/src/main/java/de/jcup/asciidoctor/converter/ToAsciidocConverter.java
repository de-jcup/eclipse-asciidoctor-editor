package de.jcup.asciidoctor.converter;

import java.io.File;
import java.io.IOException;

public interface ToAsciidocConverter {

    public String convert(String origin);

    public String convert(File file) throws IOException;
    
    public void convertToFiles(File fileOrFolderToConvert) throws IOException;

    public String getName();
}
