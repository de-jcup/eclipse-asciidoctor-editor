package de.jcup.asciidoctoreditor.provider;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.jcup.asciidoctoreditor.TemporaryOutputFileType;

public class AsciiDoctorTempFileProviderTest {

    private static final String RELATIVE_PATH_SRC_DOCS_CONTENT= "src/docs/content/";
    private static final String FILENAME_EDITORFILE1 = "editorfile1.adoc";
    private static Path TEMP_FOLDER;
    private static File PROJECT_FOLDER;
    
    private static File EDITOR_FILE_1;
    private static long EDITOR_ID = 12345L;
    private AsciiDoctorTempFileProvider providerToTest;

    @BeforeClass
    public static void beforeClass() throws Exception {
        TEMP_FOLDER = Files.createTempDirectory("asciidoctor-temp-file-provider_tempfolder");
        
        PROJECT_FOLDER = Files.createTempDirectory("asciidoctor-temp-file-provider_projectfolder").toFile();
        File projectFile = new File(PROJECT_FOLDER,".project");
        assertTrue(projectFile.createNewFile());
        
        EDITOR_FILE_1= new File(PROJECT_FOLDER,RELATIVE_PATH_SRC_DOCS_CONTENT+FILENAME_EDITORFILE1);
    }

    @Before
    public void before() {
        AsciiDoctorProjectProviderContext context = mock(AsciiDoctorProjectProviderContext.class);
        
        when(context.getRootDirectory()).thenReturn(PROJECT_FOLDER);
        when(context.getTempFolder()).thenReturn(TEMP_FOLDER);
        
        providerToTest = new AsciiDoctorTempFileProvider(context);
    }

    @Test
    public void createHiddenEditorFile_editorfile1() throws Exception {
        /* execute */
        File editorFile = providerToTest.createHiddenEditorTempFile(EDITOR_FILE_1, EDITOR_ID);
        
        /* test */
        File expected =new File(TEMP_FOLDER.toFile(),RELATIVE_PATH_SRC_DOCS_CONTENT+EDITOR_ID+"_hidden-editorfile_editorfile1.adoc");
    
        assertEquals(expected.toString(),editorFile.toString());
    }

    @Test
    public void createOutputTempFile_editorfile1_external_preview() throws Exception {
        /* execute */
        File editorFile = providerToTest.createHTMLPreviewTempFile(EDITOR_FILE_1, EDITOR_ID,TemporaryOutputFileType.EXTERNAL_PREVIEW);
        
        /* test */
        File expected =new File(TEMP_FOLDER.toFile(),RELATIVE_PATH_SRC_DOCS_CONTENT+EDITOR_ID+"_external-preview_editorfile1.html");
    
        assertEquals(expected.toString(),editorFile.toString());
    }
    
    @Test
    public void createOutputTempFile_editorfile1_origin() throws Exception {
        /* execute */
        File editorFile = providerToTest.createHTMLPreviewTempFile(EDITOR_FILE_1, EDITOR_ID,TemporaryOutputFileType.ORIGIN);
        
        /* test */
        File expected =new File(TEMP_FOLDER.toFile(),RELATIVE_PATH_SRC_DOCS_CONTENT+EDITOR_ID+"_editorfile1.html");
    
        assertEquals(expected.toString(),editorFile.toString());
    }
    
    @Test
    public void createOutputTempFile_hidden_editorfile_internal_preview() throws Exception {
        /* execute */
        File hidddenEditorFile = providerToTest.createHiddenEditorTempFile(EDITOR_FILE_1, EDITOR_ID);
        File editorFile = providerToTest.createHTMLPreviewTempFile(hidddenEditorFile, EDITOR_ID,TemporaryOutputFileType.INTERNAL_PREVIEW);
        
        /* test */
        File expected =new File(TEMP_FOLDER.toFile(),RELATIVE_PATH_SRC_DOCS_CONTENT+EDITOR_ID+"_internal-preview_editorfile1.html");
    
        assertEquals(expected.toString(),editorFile.toString());
    }
    
    @Test
    public void createOutputTempFile_hidden_editorfile_external_preview() throws Exception {
        /* execute */
        File hidddenEditorFile = providerToTest.createHiddenEditorTempFile(EDITOR_FILE_1, EDITOR_ID);
        File editorFile = providerToTest.createHTMLPreviewTempFile(hidddenEditorFile, EDITOR_ID,TemporaryOutputFileType.EXTERNAL_PREVIEW);
        
        /* test */
        File expected =new File(TEMP_FOLDER.toFile(),RELATIVE_PATH_SRC_DOCS_CONTENT+EDITOR_ID+"_external-preview_editorfile1.html");
    
        assertEquals(expected.toString(),editorFile.toString());
    }
    
    @Test
    public void createOutputTempFile_hidden_editorfile_orign() throws Exception {
        /* execute */
        File hidddenEditorFile = providerToTest.createHiddenEditorTempFile(EDITOR_FILE_1, EDITOR_ID);
        File editorFile = providerToTest.createHTMLPreviewTempFile(hidddenEditorFile, EDITOR_ID,TemporaryOutputFileType.ORIGIN);
        
        /* test */
        File expected =new File(TEMP_FOLDER.toFile(),RELATIVE_PATH_SRC_DOCS_CONTENT+EDITOR_ID+"_hidden-editorfile_editorfile1.html");
    
        assertEquals(expected.toString(),editorFile.toString());
    }

}
