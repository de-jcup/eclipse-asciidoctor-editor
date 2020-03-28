package de.jcup.asciidoctoreditor.provider;

import static de.jcup.asciidoctoreditor.TestResourcesLoader.*;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class AttributeSearchTest {

    AttributeSearch searchToTest;

    @Before
    public void before() {
        searchToTest = new AttributeSearch();
    }
    
    @Test
    public void testproject1_file1_found() throws Exception{
        /* prepare */
        File testProject1Folder = assertTestFile("attributesearch/testproject1");
        File testProject1File1 = assertTestFile("attributesearch/testproject1/src/doc/asciidoc/file1.adoc");
        
        /* execute */
        FileMatch result = searchToTest.resolveFirstAttributeFoundTopDown(AttributeSearchParameter.IMAGES_DIR_ATTRIBUTE, testProject1Folder);
        
        /* test */
        assertNotNull("no result found!", result);
        File resultFile = result.getFile();
        assertNotNull(resultFile);
        
        assertEquals(testProject1File1.toString(), resultFile.toString());
        assertEquals("./../resources/images",result.getValue());
        
    }

}
