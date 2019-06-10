package de.jcup.asciidoctoreditor;

import java.io.File;
import java.util.Map;

public interface AsciidoctorAdapter {

    void convertFile(File filename, Map<String, Object> options);

    Map<String, Object> resolveAttributes(File baseDir);

}
