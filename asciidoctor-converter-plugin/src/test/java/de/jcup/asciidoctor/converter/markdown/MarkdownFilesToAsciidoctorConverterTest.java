/*
 * Copyright 2019 Albert Tregnaghi
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
package de.jcup.asciidoctor.converter.markdown;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctor.converter.TestFileAccess;

public class MarkdownFilesToAsciidoctorConverterTest {

    private MarkdownFilesToAsciidoctorConverter converterToTest;

    @Before
    public void before() {
        converterToTest = new MarkdownFilesToAsciidoctorConverter();
    }
    @Test
    public void convert_file_markdown_works() throws Exception{
        /* prepare */
        Path tempFolder = TestFileAccess.createTempFolder();
        String code = TestFileAccess.getTestResourceAsString("markdown/origin/markdown1.md");
        
        Path sourcePath = TestFileAccess.write(tempFolder,"markdown.md",code);
        
        /* execute */
        converterToTest.convertToFiles(sourcePath.toFile());
        
        /* test */
        Path targetFolder = tempFolder.resolve("converted2asciidoc");
        assertExists(targetFolder);
        assertExists(targetFolder.resolve("markdown.adoc"));
        
    }
    
    @Test
    public void convert_files_markdown_works() throws Exception{
        /* prepare */
        Path tempFolder = TestFileAccess.createTempFolder();
        String code = TestFileAccess.getTestResourceAsString("markdown/origin/markdown1.md");
        
        TestFileAccess.write(tempFolder,"markdown.md",code);
        TestFileAccess.write(tempFolder,"sub1/markdown1.md",code);
        TestFileAccess.write(tempFolder,"sub1/sub2/markdown2.md",code);
        
        /* execute */
        converterToTest.convertToFiles(tempFolder.toFile());
        
        /* test */
        Path targetFolder = tempFolder.resolve("converted2asciidoc");
        assertExists(targetFolder);
        assertExists(targetFolder.resolve("markdown.adoc"));
        assertExists(targetFolder.resolve("sub1/markdown1.adoc"));
        assertExists(targetFolder.resolve("sub1/sub2/markdown2.adoc"));
        
    }
    
    private void assertExists(Path path) {
        if (!Files.exists(path)) {
            String message = "Does not exist:"+path;
            System.err.println(message);
            fail(message);
        }
    }
    
    @Test
    public void convert_file_markdown1_works() throws Exception{
        /* prepare*/
        File origin = TestFileAccess.getTestResource("markdown/origin/markdown1.md");
        String expected = TestFileAccess.getTestResourceAsString("markdown/expected/markdown1.adoc");
        /* execute */
        String converted = converterToTest.convert(origin);
        System.out.println(converted);
        
        /* test */
        assertEquals(expected,converted);
        
    }
    
    @Test
    public void convert_string_title() {
        assertEquals("= Title", converterToTest.convert("# Title")); 
    }
    
    @Test
    public void convert_string_headline1() {
        assertEquals("== Headline", converterToTest.convert("## Headline")); 
    }

}
