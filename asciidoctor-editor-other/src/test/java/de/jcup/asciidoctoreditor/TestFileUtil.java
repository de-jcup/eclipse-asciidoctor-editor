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
package de.jcup.asciidoctoreditor;

import java.io.File;
import java.io.IOException;

public class TestFileUtil {

	static File testSourceFolder;
	
	static{
		File testscriptFolder=new File("./testscripts/");
		if (!testscriptFolder.exists()){
			testscriptFolder=new File("./asciidoctor-editor-other/testscripts/");
		}
		if (!testscriptFolder.exists()){
			throw new IllegalStateException("testscripts folder not found");
		}
		testSourceFolder = new File(testscriptFolder.getParentFile(),"src/test/resources");
		if (!testSourceFolder.exists()){
            throw new IllegalStateException("test resource folder not found");
        }
	}

	/**
     * Asserts given test file does exist
     * @param relativePathInTestResources
     * @return file
     */
    public static File assertTestFile(String relativePathInTestResources) {
        try{
            File file = testSourceFolder.getCanonicalFile().toPath().resolve(relativePathInTestResources).toFile();
            file = file.getAbsoluteFile();
            if (!file.exists()){
                throw new IllegalArgumentException("Test case corrupt! Test resource file does not exist:"+file);
            }
            return file;
        }catch(IOException e) {
            throw new IllegalArgumentException("Test case corrupt! Test resource file does not exist:"+relativePathInTestResources,e);
        }
    }
}
