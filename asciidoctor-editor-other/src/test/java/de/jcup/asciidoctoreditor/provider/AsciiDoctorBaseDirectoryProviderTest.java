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

import de.jcup.asciidoctoreditor.AsciiDocFileUtils;

import static org.mockito.Mockito.*;

import java.io.File;
import java.nio.file.Files;

public class AsciiDoctorBaseDirectoryProviderTest {

	private AsciiDoctorProviderContext context;
	private AsciiDoctorBaseDirectoryProvider providerToTest;

	@Rule
	public ExpectedException expectedException = ExpectedException.none(); 
	
	@Before
	public void before(){
		context = mock(AsciiDoctorProviderContext.class);
		providerToTest = new AsciiDoctorBaseDirectoryProvider(context);
	}
	
	@Test
	public void when_no_asciidocfile_is_set_an_illegal_state_exception_is_thrown() {
		/* test */
		expectedException.expect(IllegalStateException.class);
		
		/* execute */
		providerToTest.findBaseDir();
	}
	
	@Test
	public void when_asciidocfile_is_set_base_dir_is_scanned_to_last_adoc_file_found_in_upper_hiararchy1() {
		
		File asciidocFile = ensuredTestFile("src/test/resources/basedirtesting/testproject1/mydoc1/subfolder1/testproject1.adoc");
		File expectedbaseDir = ensuredTestFile("src/test/resources/basedirtesting/testproject1/mydoc1/subfolder1");

		/* prepare */
		when(context.getAsciiDocFile()).thenReturn(asciidocFile);
		
		/* execute */
		File baseDir = providerToTest.findBaseDir();

		/* test */
		assertNotNull(baseDir);
		assertEquals(expectedbaseDir,baseDir);
	}
	
	@Test
	public void when_asciidocfile_is_set_base_dir_is_scanned_to_last_adoc_file_found_in_upper_hiararchy2() {
		
		File asciidocFile = ensuredTestFile("src/test/resources/basedirtesting/testproject1/mydoc1/subfolder1/sub2/testproject2.adoc");
		File expectedbaseDir = ensuredTestFile("src/test/resources/basedirtesting/testproject1/mydoc1/subfolder1");

		/* prepare */
		when(context.getAsciiDocFile()).thenReturn(asciidocFile);
		
		/* execute */
		File baseDir = providerToTest.findBaseDir();

		/* test */
		assertNotNull(baseDir);
		assertEquals(expectedbaseDir,baseDir);
	}
	
	@Test
	public void when_calculated_base_dir_user_local_temp_an_illegal_state_exception_is_thrown() throws Exception {
		
		File asciidocFile = Files.createTempFile("prefix", "suffix").toFile();
		/* sanity check - it must be clear the the parent directory is potentially problematic- e.g. on windows : C:\Users\$username\AppData\Local\Temp*/
		File problematic = new File(System.getProperty("java.io.tmpdir"));
		assertEquals(problematic, asciidocFile.getParentFile());

		/* test */
		expectedException.expect(IllegalStateException.class);
		
		/* prepare */
		when(context.getAsciiDocFile()).thenReturn(asciidocFile);
		
		/* execute */
		providerToTest.findBaseDir();

	}
	
	@Test
	public void converted_content_file_locations_to_base_dir_does_not_throw_an_exception_and_basedir_is_not_root_temp_dir() throws Exception {
		
		File asciidocFile = AsciiDocFileUtils.createTempFileForConvertedContent(System.nanoTime(), "junit_testcase_temporary_file_for_issue_97.xyz");
		
		/* prepare */
		when(context.getAsciiDocFile()).thenReturn(asciidocFile);
		
		/* execute */
		File baseDir = providerToTest.findBaseDir();
		
		/* test */
		File problematic = new File(System.getProperty("java.io.tmpdir"));
		assertNotEquals(problematic, baseDir);
		
		/* but ensure the base dir is a directory...*/
		assertTrue("not a directory:"+baseDir, baseDir.isDirectory());
	}

	
	
	protected File ensuredTestFile(String pathname) {
		File asciidocFile = new File(pathname);
		if (! asciidocFile.exists()){
			asciidocFile=new File("asciidoctor-editor-other/"+pathname);
		}
		if (! asciidocFile.exists()){
			throw new IllegalStateException("test case corrupt-file not found:"+asciidocFile);
		}
		return asciidocFile;
	}

}
