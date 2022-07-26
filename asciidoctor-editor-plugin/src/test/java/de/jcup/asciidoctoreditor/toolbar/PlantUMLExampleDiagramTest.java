package de.jcup.asciidoctoreditor.toolbar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class PlantUMLExampleDiagramTest {

    @Test
    public void every_entry_has_correct_path_and_file_does_exist() throws IOException {
        StringBuilder problems = new StringBuilder();
        for (PlantUMLExampleDiagram diagram : PlantUMLExampleDiagram.values()) {
            String path = diagram.getPath();
            File file = new File("./" + path);
            if (!file.exists()) {
                problems.append(diagram.name() + " has not correct file path! '" + file.getCanonicalPath() + "' does not exists!\n");
            }
        }

        if (problems.length() > 0) {
            fail(problems.toString());
        }

    }

}
