package de.jcup.asciidoctoreditor;

import java.io.File;
import java.util.Map;

import org.asciidoctor.ast.DocumentHeader;

public interface AsciidoctorAdapter {

    void convertFile(File filename, Map<String, Object> options);

    DocumentHeader readDocumentHeader(File file);

}
