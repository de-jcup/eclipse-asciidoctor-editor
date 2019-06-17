package de.jcup.asciidoctoreditor.asciidoc;

import java.io.File;
import java.util.Map;

public interface AsciidoctorAdapter {

    void convertFile(File editorFileOrNull, File asciiDocFile, Map<String, Object> options);

    Map<String, Object> resolveAttributes(File baseDir);

}