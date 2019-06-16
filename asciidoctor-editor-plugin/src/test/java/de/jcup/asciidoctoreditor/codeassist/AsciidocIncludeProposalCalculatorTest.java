package de.jcup.asciidoctoreditor.codeassist;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctoreditor.TestResourcesLoader;

public class AsciidocIncludeProposalCalculatorTest {

    private AsciidocIncludeProposalCalculator toTest;
    private File editorFile1;

    @Before
    public void before() {
        toTest = new AsciidocIncludeProposalCalculator();
        editorFile1 = TestResourcesLoader.assertTestFile("codeassist/include/test1/editorfile1.adoc");
        /* Hmm. we should use a mock for internal calculator - currently no mockit available, so let as is */
    }

    
    @Test
    public void editorFile1_only_include_results_in_2_files_one_folder_as_label() throws Exception {
        /* prepare */
        String text = "include::";
        int index = text.length();
        
        /* execute */
        Set<AsciidocIncludeProposalData> result = toTest.calculate(editorFile1, text, index);
        
        /* test */
        assertNotNull(result);
        assertEquals(3, result.size());
        Iterator<AsciidocIncludeProposalData> it = result.iterator();
        assertEquals("include::otherfile1.adoc[]",it.next().getInclude());
        assertEquals("include::otherfile3.adoc[]",it.next().getInclude());
        assertEquals("include::subfolder1/",it.next().getInclude());
        
    }
    
    
    @Test
    public void editorFile1_subfolder1_already_includes_returns_result_for_subfolder() throws Exception {
        /* prepare */
        String text = "include::subfolder1";
        int index = text.length();
        
        /* execute */
        Set<AsciidocIncludeProposalData> result = toTest.calculate(editorFile1, text, index);
        
        /* test */
        assertNotNull(result);
        assertEquals(1, result.size());
        Iterator<AsciidocIncludeProposalData> it = result.iterator();
        assertEquals("include::subfolder1/otherfile4.adoc[]",it.next().getInclude());        
    }
    
    @Test
    public void editorFile1_include_subfolder1_results_in_1_file() throws Exception {
        /* prepare */
        String text = "include::subfolder1/";
        int index = text.length();
        
        /* execute */
        Set<AsciidocIncludeProposalData> result = toTest.calculate(editorFile1, text, index);
        
        /* test */
        assertNotNull(result);
        assertEquals(1, result.size());
        Iterator<AsciidocIncludeProposalData> it = result.iterator();
        assertEquals("include::subfolder1/otherfile4.adoc[]",it.next().getInclude());
        
    }
    
    @Test
    public void editorFile1_include_sub_results_in_1_folder() throws Exception {
        /* prepare */
        String text = "include::sub";
        int index = text.length()-1;
        
        /* execute */
        Set<AsciidocIncludeProposalData> result = toTest.calculate(editorFile1, text, index);
        
        /* test */
        assertNotNull(result);
        assertEquals(1, result.size());
        Iterator<AsciidocIncludeProposalData> it = result.iterator();
        assertEquals("include::subfolder1/",it.next().getInclude());
        
    }

}
