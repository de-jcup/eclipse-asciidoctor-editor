package de.jcup.asciidoctoreditor.globalmodel;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class GlobalAsciidocModelRecursionFinderTest {

    private GlobalAsciidocModelRecursionFinder finderToTest;

    @Before
    public void before() {
        finderToTest = new GlobalAsciidocModelRecursionFinder();
    }

    @Test
    public void isRecursive_not_included_file_false() {
        /* prepare */
        AsciidocFile file = new AsciidocFile();

        /* execute */
        boolean result = finderToTest.isRecursive(file);

        /* execute */
        assertFalse(result);

    }

    @Test
    public void isRecursive_included_but_not_recursive_file_false() throws IOException {
        /* prepare */
        GlobalAsciidocModel model = new GlobalAsciidocModel();
        AsciidocFile asciidocFile1 = model.registerNewAsciidocFile(new File("./something1.adoc"));
        AsciidocFile asciidocFile2 = model.registerNewAsciidocFile(new File("./something2.adoc"));
        AsciidocFile asciidocFile3 = model.registerNewAsciidocFile(new File("./something3.adoc"));

        asciidocFile1.addInclude(asciidocFile2,1,2);
        asciidocFile2.addInclude(asciidocFile3,1,2);

        /* execute */
        boolean result = finderToTest.isRecursive(asciidocFile1);

        /* execute */
        assertFalse(result);

    }

    @Test
    public void f1_f1_f2_f3_is_recursive_f1_returns_true() throws IOException {
        /* prepare */
        GlobalAsciidocModel model = new GlobalAsciidocModel();
        AsciidocFile asciidocFile1 = model.registerNewAsciidocFile(new File("./something1.adoc"));
        AsciidocFile asciidocFile2 = model.registerNewAsciidocFile(new File("./something2.adoc"));
        AsciidocFile asciidocFile3 = model.registerNewAsciidocFile(new File("./something3.adoc"));

        asciidocFile1.addInclude(asciidocFile2,1,2);
        asciidocFile2.addInclude(asciidocFile3,1,2);

        asciidocFile1.addInclude(asciidocFile1,1,2); // recursion directly

        /* execute */
        boolean result = finderToTest.isRecursive(asciidocFile1);

        /* execute */
        assertTrue(result);

    }

    @Test
    public void f1_f1_f2_f3_is_recursive_f3_returns_false() throws IOException {
        /* prepare */
        GlobalAsciidocModel model = new GlobalAsciidocModel();
        AsciidocFile asciidocFile1 = model.registerNewAsciidocFile(new File("./something1.adoc"));
        AsciidocFile asciidocFile2 = model.registerNewAsciidocFile(new File("./something2.adoc"));
        AsciidocFile asciidocFile3 = model.registerNewAsciidocFile(new File("./something3.adoc"));

        asciidocFile1.addInclude(asciidocFile2,1,2);
        asciidocFile2.addInclude(asciidocFile3,1,2);

        asciidocFile1.addInclude(asciidocFile1,0,815); // recursion f1 directly but we test3

        /* execute */
        boolean result = finderToTest.isRecursive(asciidocFile3);

        /* execute */
        assertFalse(result);

    }

    @Test
    public void f1_f2_f3_f1_is_recursive_f1_returns_true() throws IOException {
        /* prepare */
        GlobalAsciidocModel model = new GlobalAsciidocModel();
        AsciidocFile asciidocFile1 = model.registerNewAsciidocFile(new File("./something1.adoc"));
        AsciidocFile asciidocFile2 = model.registerNewAsciidocFile(new File("./something2.adoc"));
        AsciidocFile asciidocFile3 = model.registerNewAsciidocFile(new File("./something3.adoc"));

        asciidocFile1.addInclude(asciidocFile2,1,2);
        asciidocFile2.addInclude(asciidocFile3,1,2);

        asciidocFile3.addInclude(asciidocFile1,0,815);

        /* execute */
        boolean result = finderToTest.isRecursive(asciidocFile1);

        /* execute */
        assertTrue(result);

    }

    @Test
    public void f1_f2_f3_f2_is_recursive_f2_returns_true() throws IOException {
        /* prepare */
        GlobalAsciidocModel model = new GlobalAsciidocModel();
        AsciidocFile asciidocFile1 = model.registerNewAsciidocFile(new File("./something1.adoc"));
        AsciidocFile asciidocFile2 = model.registerNewAsciidocFile(new File("./something2.adoc"));
        AsciidocFile asciidocFile3 = model.registerNewAsciidocFile(new File("./something3.adoc"));

        asciidocFile1.addInclude(asciidocFile2,1,2);
        asciidocFile2.addInclude(asciidocFile3,1,2);

        asciidocFile3.addInclude(asciidocFile2,0,815);

        /* execute */
        boolean result = finderToTest.isRecursive(asciidocFile2);

        /* execute */
        assertTrue(result);

    }

    @Test
    public void f1_f2_f3_f3_is_recursive_f2_returns_false() throws IOException {
        /* prepare */
        GlobalAsciidocModel model = new GlobalAsciidocModel();
        AsciidocFile asciidocFile1 = model.registerNewAsciidocFile(new File("./something1.adoc"));
        AsciidocFile asciidocFile2 = model.registerNewAsciidocFile(new File("./something2.adoc"));
        AsciidocFile asciidocFile3 = model.registerNewAsciidocFile(new File("./something3.adoc"));

        asciidocFile1.addInclude(asciidocFile2,1,2);
        asciidocFile2.addInclude(asciidocFile3,1,2);

        asciidocFile3.addInclude(asciidocFile3,1,2);

        /* execute */
        boolean result = finderToTest.isRecursive(asciidocFile2);

        /* execute */
        assertFalse(result);

    }

    @Test
    public void f1_f2_f3_f4_f1_is_recursive_f1_returns_true() throws IOException {
        /* prepare */
        GlobalAsciidocModel model = new GlobalAsciidocModel();
        AsciidocFile asciidocFile1 = model.registerNewAsciidocFile(new File("./something1.adoc"));
        AsciidocFile asciidocFile2 = model.registerNewAsciidocFile(new File("./something2.adoc"));
        AsciidocFile asciidocFile3 = model.registerNewAsciidocFile(new File("./something3.adoc"));
        AsciidocFile asciidocFile4 = model.registerNewAsciidocFile(new File("./something4.adoc"));

        asciidocFile1.addInclude(asciidocFile2,1,2);
        asciidocFile2.addInclude(asciidocFile3,1,2);

        asciidocFile3.addInclude(asciidocFile4,1,2);
        asciidocFile4.addInclude(asciidocFile1,1,2);

        /* execute */
        boolean result = finderToTest.isRecursive(asciidocFile1);

        /* execute */
        assertTrue(result);

    }

}
