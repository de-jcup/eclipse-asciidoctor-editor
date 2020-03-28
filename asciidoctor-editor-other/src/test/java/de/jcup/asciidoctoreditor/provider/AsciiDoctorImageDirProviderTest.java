package de.jcup.asciidoctoreditor.provider;

import static de.jcup.asciidoctoreditor.TestFileUtil.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctoreditor.LogAdapter;

public class AsciiDoctorImageDirProviderTest {
    AsciiDoctorImageDirProvider providerToTest;
    AsciiDoctorProjectProviderContext context;
    
    @Before
    public void before() {
        context = mock(AsciiDoctorProjectProviderContext.class);
        LogAdapter logAdapter = mock(LogAdapter.class);
        when(context.getLogAdapter()).thenReturn(logAdapter);
        providerToTest = new AsciiDoctorImageDirProvider(context);
    }

    @Test
    public void testproject1_file1_result_same_for_as_string_like_in_result() throws Exception {
        /* prepare */
        File testProject1File1 = assertTestFile("imagedirprovider/testproject1/src/doc/asciidoc/file1.adoc");
        when(context.getEditorFileOrNull()).thenReturn(testProject1File1);
        
        /* execute */
        Path result = providerToTest.getImagesDirAbsoluteFileOrNull();
        String resultAsString = providerToTest.getImagesDirAbsolutePathOrNull();
        
        /* test */
        assertNotNull("no result found!", result);
        assertNotNull("no result string found!", resultAsString);
        
        assertEquals(result.toString(), resultAsString);

    }
    
    @Test
    public void testproject1_file1_found() throws Exception {
        /* prepare */
        File testProject1ImageFolder = assertTestFile("imagedirprovider/testproject1/src/doc/resources/images");
        File testProject1File1 = assertTestFile("imagedirprovider/testproject1/src/doc/asciidoc/file1.adoc");
        when(context.getEditorFileOrNull()).thenReturn(testProject1File1);
        
        /* execute */
        String resultAsString = providerToTest.getImagesDirAbsolutePathOrNull();
        
        /* test */
        assertNotNull("no result found!", resultAsString);
        
        assertEquals(testProject1ImageFolder.getAbsolutePath(), resultAsString);

    }
    
    @Test
    public void testproject2_no_image_returns_null() throws Exception {
        /* prepare */
        File testProject1File1 = assertTestFile("imagedirprovider/testproject2/src/doc/asciidoc/file1.adoc");
        when(context.getEditorFileOrNull()).thenReturn(testProject1File1);
        
        /* execute */
        String resultAsString = providerToTest.getImagesDirAbsolutePathOrNull();
        
        /* test */
        assertNull("no result found!", resultAsString);

    }

}
