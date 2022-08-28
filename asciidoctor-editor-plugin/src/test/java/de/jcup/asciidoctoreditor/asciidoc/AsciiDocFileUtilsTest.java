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
package de.jcup.asciidoctoreditor.asciidoc;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Path;

import org.junit.Test;

public class AsciiDocFileUtilsTest {

    @Test
    public void getUserHomeSubfolderAsExpected() {
        /* prepare */
        String userHomeProperty = System.getProperty("user.home");
        userHomeProperty= userHomeProperty.replace("\\","/");
        assertFalse(userHomeProperty.endsWith("/"));
        String expectedPath = userHomeProperty+"/.eclipse-asciidoctor-editor";
        
        /* execute */
        File subFolder = AsciiDocFileUtils.getEditorHomeSubFolder();
        
        /* test */
        assertEquals(expectedPath, subFolder.getAbsolutePath());
    }

    @Test
    public void createdTempFolderIsInsideEditorHomeSubFolder() {
        /* execute*/
        Path folder = AsciiDocFileUtils.createTempFolderForId("i-am-a-project");
        
        /* test */
        assertNotNull(folder);
        File parentFile = folder.toFile().getParentFile();
        
        assertEquals(new File(AsciiDocFileUtils.getEditorHomeSubFolder(),"tmp"),parentFile);
        
    }

    @Test
    public void createSafeFilename() {
        /* change when not standard ASCII code */
        assertEquals("Apfel-wurfeln-it-offentlich-verboten.txt", AsciiDocFileUtils.createEncodingSafeFileName("Äpfel-würfeln-ißt-öffentlich-verboten.txt"));
        assertEquals("Apfel-wurfeln-ist-offentlich-verboten.txt", AsciiDocFileUtils.createEncodingSafeFileName("Äpfel-würfeln-ist-öffentlich-verboten.txt"));
        assertEquals("monchere.txt", AsciiDocFileUtils.createEncodingSafeFileName("mon´chere.txt"));

        /* keep ASCII special chars: */
        assertEquals("Apfel-wurfeln-ist-offentlich-verboten.txt", AsciiDocFileUtils.createEncodingSafeFileName("Apfel-wurfeln-ist-offentlich-verboten.txt"));
        assertEquals("de`juice.txt", AsciiDocFileUtils.createEncodingSafeFileName("de`juice.txt"));
    }

    @Test
    public void calculatePathToFileFromBase() {
        /* prepare */
        File file = new File("./");
        File asciiDocFile = new File(file, "basefolder/sub1/sub2/sub3/test.adoc");
        File baseDir = new File(file, "basefolder");

        /* execute */
        String path = AsciiDocFileUtils.calculatePathToFileFromBase(asciiDocFile, baseDir);

        /* test */
        assertEquals("sub1/sub2/sub3/test.adoc", path);
    }

}