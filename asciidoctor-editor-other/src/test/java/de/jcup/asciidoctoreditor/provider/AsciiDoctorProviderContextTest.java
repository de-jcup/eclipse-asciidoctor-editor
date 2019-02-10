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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.ast.DocumentHeader;
import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctoreditor.LogAdapter;
import de.jcup.asciidoctoreditor.TestscriptsUtil;

public class AsciiDoctorProviderContextTest {

	private Asciidoctor asciidoctor;
	private LogAdapter logAdapter;
    private AsciiDoctorInstanceProvider provider;

	
	@Before
	public void before(){
		asciidoctor=mock(Asciidoctor.class);
		logAdapter = mock(LogAdapter.class);
		provider = mock(AsciiDoctorInstanceProvider.class);
		
		when(provider.getAsciiDoctor(true)).thenReturn(asciidoctor);
		when(provider.getAsciiDoctor(false)).thenReturn(asciidoctor);
	}
	
	@Test
	public void test_normal_creating_context_creates_internal_providers() {
		/* execute */
		AsciiDoctorProviderContext context = new AsciiDoctorProviderContext(provider, logAdapter);
	
		/* test */
		assertNotNull(context.getAsciiDoctor());
		assertNotNull(context.getAttributesProvider());
		assertNotNull(context.getBaseDirProvider());
		assertNotNull(context.getImageProvider());
		assertNotNull(context.getOptionsProvider());
		
	}
	
	@Test
	public void image_provider_ensure_images__does_create_target_folder_and_contains_not_copied_images_when_no_headline_with_imagedir_attribute() throws Exception{
		Set<File> files = testInternalImages(false);
		/* test */
		assertEquals(5,files.size()); // copies all parts from directory of current asciidocfile, means bugfixes,diagrams,images, issues
		boolean foundLogo=false;
		for (File file: files){
			if (file.getName().equals("images")){
				File[] subFiles = file.listFiles();
				for (File subFile: subFiles){
					if (subFile.getName().equals("asciidoctor-editor-logo.png")){
						foundLogo=true;
						break;
					}
				}
			}
		}
		assertTrue(foundLogo);
		
	}
	
	@Test
	public void image_provider_ensure_images__does_create_target_folder_and_contains_copied_images_and_subfolders_when_at_least_one_headline_has_imagedir_attribute() throws Exception{
		Set<File> files = testInternalImages(true);
		/* test */
		assertEquals(4,files.size()); /* 3 images, one subfolder, one readme, but readme not copied*/
		boolean foundLogo=false;
		boolean copiedSubfolderIcon=false;
		for (File file: files){
			if (file.getName().equals("asciidoctor-editor-logo.png")){
				foundLogo=true;
			}
			if (file.getName().equals("images-subfolder1")){
				File[] subFiles = file.listFiles();
				for (File subFile: subFiles){
					if (subFile.getName().equals("asciidoctor-editor.png")){
						copiedSubfolderIcon=true;
						break;
					}
				}
			}
		}
		assertTrue(foundLogo);
		assertTrue(copiedSubfolderIcon);
	}

	private Set<File> testInternalImages(boolean imageDirSet) throws IOException {
		/* before */
		AsciiDoctorProviderContext context = new AsciiDoctorProviderContext(provider, logAdapter);
		
		File testFile = TestscriptsUtil.assertFileInTestscripts("09_includes.adoc");
		context.setAsciidocFile(testFile);
		Path tempDirectory = Files.createTempDirectory("junittest");
		context.setOutputFolder(tempDirectory);
		
		tempDirectory.toFile().deleteOnExit();
		System.out.println(tempDirectory.toAbsolutePath());
		DocumentHeader header = mock(DocumentHeader.class);
		HashMap<String, Object> map1 = new HashMap<>();
		HashMap<String, Object> map2 = new HashMap<>();
		if (imageDirSet){
			File imagesFolder = TestscriptsUtil.assertFileInTestscripts("images");
			map1.put("imagesdir", imagesFolder.getAbsolutePath());
		}
		when(header.getAttributes()).thenReturn(map1).thenReturn(map2);
		when(asciidoctor.readDocumentHeader(any(File.class))).thenReturn(header);
	
		/* execute*/
		context.getImageProvider().ensureImages();
		
		/* test */
		Set<File> files = new LinkedHashSet<>();
		File tempImagesDir = new File(tempDirectory.toFile(),"images");
		assertEquals(tempImagesDir,context.targetImagesDir);
		File[] subfiles = tempImagesDir.listFiles();
		for (File file: subfiles){
			files.add(file);
		}
		return files;
	}

}
