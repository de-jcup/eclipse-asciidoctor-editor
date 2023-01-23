/*
 * Copyright 2018 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.asciidoctoreditor.provider;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.jcup.asciidoctoreditor.LogAdapter;
import de.jcup.asciidoctoreditor.UniqueIdProvider;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocFileUtils;

import static org.mockito.Mockito.*;

import java.io.File;
import java.nio.file.Files;

public class AsciiDoctorBaseDirectoryProviderTest {

	private AsciiDoctorWrapperContext context;
	private AsciiDoctorBaseDirectoryProvider providerToTest;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
    private LogAdapter logAdapter; 
	
	@Before
	public void before(){
		context = mock(AsciiDoctorWrapperContext.class);
		logAdapter = mock(LogAdapter.class);
		when(context.getLogAdapter()).thenReturn(logAdapter);
		providerToTest = new AsciiDoctorBaseDirectoryProvider(context);
	}
	
	@Test
    public void asciidoc_files_having_same_name_and_no_common_base_adoc_file_are_rendered_correctly() {
        
        File file = ensuredTestFile("src/test/resources/basedirtesting/testproject3/subfolder1/001_article.adoc");
        File expectedbaseDir = ensuredTestFile("src/test/resources/basedirtesting/testproject3/subfolder1/");

        /* prepare */
        when(context.getEditorFileOrNull()).thenReturn(file);
        
        /* execute */
        File baseDir = providerToTest.findProjectBaseDir();

        /* test */
        assertNotNull(baseDir);
        assertEquals(expectedbaseDir.toString(),baseDir.toString());
        
        /* ------ */
        /* phase 2*/       // we use now subfolder2 with same name
        /* ------ */
        
        file = ensuredTestFile("src/test/resources/basedirtesting/testproject3/subfolder2/001_article.adoc");
        expectedbaseDir = ensuredTestFile("src/test/resources/basedirtesting/testproject3/subfolder2/");

        /* prepare */
        when(context.getEditorFileOrNull()).thenReturn(file);
        
        /* execute */
        baseDir = providerToTest.findProjectBaseDir();

        /* test */
        assertNotNull(baseDir);
        assertEquals(expectedbaseDir.toString(),baseDir.toString());
    }
    
	
	@Test
	public void when_asciidocfile_is_set_base_dir_is_scanned_to_last_adoc_file_found_in_upper_hiararchy1() {
		
		File file = ensuredTestFile("src/test/resources/basedirtesting/testproject1/mydoc1/subfolder1/testproject1.adoc");
		File expectedbaseDir = ensuredTestFile("src/test/resources/basedirtesting/testproject1/mydoc1/subfolder1");

		/* prepare */
		when(context.getEditorFileOrNull()).thenReturn(file);
		
		/* execute */
		File baseDir = providerToTest.findProjectBaseDir();

		/* test */
		assertNotNull(baseDir);
		assertEquals(expectedbaseDir,baseDir);
	}
	
	@Test
    public void when_asciidocfile_is_set_base_dir_is_scanned_to_last_adoc_file_found_in_upper_hiararchy1_but__empty_folder_stops() {
	    
        File file = ensuredTestFile("src/test/resources/basedirtesting/testproject3/subfolder2/subfolder3-no-adocfile/subfolder4/test-file-a.adoc");
        File expectedbaseDir = ensuredTestFile("src/test/resources/basedirtesting/testproject3/subfolder2/subfolder3-no-adocfile/subfolder4");

        /* prepare */
        when(context.getEditorFileOrNull()).thenReturn(file);
        
        /* execute */
        File baseDir = providerToTest.findProjectBaseDir();

        /* test */
        assertNotNull(baseDir);
        assertEquals(expectedbaseDir,baseDir);
    }
	
	@Test
	public void when_asciidocfile_is_set_base_dir_is_scanned_to_last_adoc_file_found_in_upper_hiararchy2() {
		
		File file = ensuredTestFile("src/test/resources/basedirtesting/testproject1/mydoc1/subfolder1/sub2/testproject2.adoc");
		File expectedbaseDir = ensuredTestFile("src/test/resources/basedirtesting/testproject1/mydoc1/subfolder1");

		/* prepare */
		when(context.getEditorFileOrNull()).thenReturn(file);
		
		/* execute */
		File baseDir = providerToTest.findProjectBaseDir();

		/* test */
		assertNotNull(baseDir);
		assertEquals(expectedbaseDir,baseDir);
	}
	
	@Test
	public void when_calculated_base_dir_user_local_temp_an_illegal_state_exception_is_thrown() throws Exception {
		
		File file = Files.createTempFile("prefix", "suffix").toFile();
		/* sanity check - it must be clear the the parent directory is potentially problematic- e.g. on windows : C:\Users\$username\AppData\Local\Temp*/
		File problematic = new File(System.getProperty("java.io.tmpdir"));
		assertEquals(problematic, file.getParentFile());

		/* test */
		expectedException.expect(IllegalStateException.class);
		
		/* prepare */
		when(context.getEditorFileOrNull()).thenReturn(file);
		
		/* execute */
		providerToTest.findProjectBaseDir();

	}
	
	@Test
	public void converted_content_file_locations_to_base_dir_does_not_throw_an_exception_and_basedir_is_not_root_temp_dir() throws Exception {
		
	    UniqueIdProvider uniqueIdProvider = mock(UniqueIdProvider.class);
	    when(uniqueIdProvider.getUniqueId()).thenReturn("test_"+System.nanoTime());
	    
		File file = AsciiDocFileUtils.createTempFileForConvertedContent(null, uniqueIdProvider,"junit_testcase_temporary_file_for_issue_97.xyz");
		
		/* prepare */
		when(context.getEditorFileOrNull()).thenReturn(file);
		
		/* execute */
		File baseDir = providerToTest.findProjectBaseDir();
		
		/* test */
		File problematic = new File(System.getProperty("java.io.tmpdir"));
		assertNotEquals(problematic, baseDir);
		
		/* but ensure the base dir is a directory...*/
		assertTrue("not a directory:"+baseDir, baseDir.isDirectory());
	}

	
	
	protected File ensuredTestFile(String pathname) {
		File file = new File(pathname);
		if (! file.exists()){
			file=new File("asciidoctor-editor-other/"+pathname);
		}
		if (! file.exists()){
			throw new IllegalStateException("test case corrupt-file not found:"+file);
		}
		return file;
	}

}
