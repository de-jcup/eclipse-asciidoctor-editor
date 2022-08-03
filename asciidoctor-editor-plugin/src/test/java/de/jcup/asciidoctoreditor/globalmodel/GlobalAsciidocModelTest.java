package de.jcup.asciidoctoreditor.globalmodel;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctoreditor.SystemOutLogAdapter;
import de.jcup.asciidoctoreditor.globalmodel.AsciidocFile;
import de.jcup.asciidoctoreditor.globalmodel.GlobalAsciidocModel;

public class GlobalAsciidocModelTest {

    private GlobalAsciidocModel model;

    @Before
    public void before() {
        model = new GlobalAsciidocModel();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAsciidocFileOrNull_null_logadapter_set_results_in_illegal_arg() {
        model.getAsciidocFileOrNull(null, new SystemOutLogAdapter());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAsciidocFileOrNull_file_set_logadapter_null_results_in_illegal_arg() {
        File file1 = new File("./dummy1.txt");
        model.getAsciidocFileOrNull(file1, null);
    }

    @Test
    public void getAsciidocFileOrNull_unknown_results_in_null() {
        /* prepare */
        File notRegisteredFile = new File("./dummy1.txt");

        /* execute */
        AsciidocFile result = model.getAsciidocFileOrNull(notRegisteredFile, new SystemOutLogAdapter());

        /* test */
        assertNull("The file was not found?!?!", result);
    }

    @Test
    public void getAsciidocFileOrNull_knows_not_relative_file_in_results_in_asciidocfile() throws IOException {
        /* prepare */
        File file1 = new File("./dummy1.txt");
        File file2 = new File("./dummy2.txt");
        model.registerNewAsciidocFile(file1);

        /* execute */
        AsciidocFile result = model.getAsciidocFileOrNull(file2, new SystemOutLogAdapter());

        /* test */
        assertNull("The relative file was found?!?!!", result);
    }

    @Test
    public void getAsciidocFileOrNull_known_absolute_file_in_results_in_asciidocfile() throws IOException {
        /* prepare */
        File file1 = new File("./dummy1.txt");
        AsciidocFile asciidocFile = model.registerNewAsciidocFile(file1);

        /* execute */
        AsciidocFile result = model.getAsciidocFileOrNull(file1, new SystemOutLogAdapter());

        /* test */
        assertNotNull("The absolute file was not found!", result);
        assertEquals(asciidocFile, result);
    }

}
