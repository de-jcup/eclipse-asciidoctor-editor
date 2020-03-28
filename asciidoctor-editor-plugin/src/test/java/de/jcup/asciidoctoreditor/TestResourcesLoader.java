/*
 * Copyright 2017 Albert Tregnaghi
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestResourcesLoader {
	private static File testResourceRootFolder = new File("./asciidoctor-editor-plugin/src/test/resources");
	static{
		if (!testResourceRootFolder.exists()){
			// workaround for difference between eclipse test and gradle execution (being in root folder...)
			testResourceRootFolder = new File("./../asciidoctor-editor-plugin/src/test/resources");
		}
	}
	
	public static String loadTestFile(String pathFromResources) throws IOException{
		assertTestRespirceFolderExists();
		
		File file = assertTestFile(pathFromResources);
		StringBuilder sb = new StringBuilder();
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))){
			String line = null;
			while ((line=br.readLine())!=null){
				sb.append(line);
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	/**
	 * Asserts given test file does exist
	 * @param relativePathInTestResources
	 * @return file
	 */
    public static File assertTestFile(String relativePathInTestResources) {
        try{
            File file = testResourceRootFolder.getCanonicalFile().toPath().resolve(relativePathInTestResources).toFile();
            file = file.getAbsoluteFile();
            if (!file.exists()){
                throw new IllegalArgumentException("Test case corrupt! Test resource file does not exist:"+file);
            }
            return file;
        }catch(IOException e) {
            throw new IllegalArgumentException("Test case corrupt! Test resource file does not exist:"+relativePathInTestResources,e);
        }
    }

	private static void assertTestRespirceFolderExists() {
		if (!testResourceRootFolder.exists()){
			throw new IllegalArgumentException("Test setup corrupt! Root folder of test resources not found:"+testResourceRootFolder);
		}
	}
}
