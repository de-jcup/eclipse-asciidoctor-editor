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
package de.jcup.asciidoctor.converter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

public class TestFileAccess {

	static File projectRootFolder;
	
	static File testResources;
	
	static {
		testResources=new File("src/test/resources");
		if (! testResources.exists()) {
			testResources=new File("asciidoctor-converter-plugin/src/test/resources");
		}
		if (! testResources.exists()) {
			throw new IllegalStateException();
		}
		projectRootFolder=testResources.getParentFile().getParentFile();
	}
	
	public static Path createTempFolder() throws IOException {
	    return Files.createTempDirectory("asciidoceditor");
	}

	public static Path write(Path tempFolder, String relativePathToFile, String code) throws IOException{
	    if (tempFolder==null) {
	        throw new IllegalArgumentException("path may not be null");
	    }
	    Path path = tempFolder.resolve(relativePathToFile);
	    Files.createDirectories(path.getParent());
	    
	    Files.newBufferedWriter(path).write(code);
	    return path;
	    
	}
	
	public static File getTestResource(String path) {
		File file = new File(testResources,path);
		if (! file.exists()) {
			throw new IllegalStateException();
		}
		return file;
	}
	
	public static String getTestResourceAsString(String path) {
	    File file = getTestResource(path);
	    try  {
            List<String> lines = Files.readAllLines(file.toPath());
            StringBuilder sb= new StringBuilder();
            for (Iterator<String> it = lines.iterator();it.hasNext();) {
                sb.append(it.next());
                if (it.hasNext()) {
                    sb.append("\n");
                }
            }
            return sb.toString();
            
        } catch (IOException e) {
            throw new IllegalStateException("Should not happen", e);
        }
	}

	

}
