/*
 * Copyright 2022 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.globalmodel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jcup.asciidoctoreditor.LogAdapter;

public class GlobalAsciidocModel {
    
    public static GlobalAsciidocModelBuilder builder() {
        return new GlobalAsciidocModelBuilder();
    }

    private Map<File, AsciidocFile> fileTofileMap = new HashMap<>();

    public AsciidocFile getAsciidocFileOrNull(File startFile, LogAdapter logAdapter) {
        if (startFile==null) {
            throw new IllegalArgumentException("Given start file to for global model creation must not be null!");
        }
        if (logAdapter==null) {
            throw new IllegalArgumentException("Log adapter may not be null!");
        }
        File absoluteFile;
        try {
            absoluteFile = startFile.getCanonicalFile();
            return fileTofileMap.get(absoluteFile);
        } catch (IOException e) {
            logAdapter.logError("Was not able to fetch asciidocfile for "+startFile, e);
            return null;
        }
    }
    
    
    private File transformToAbsoluteFile(File file) throws IOException {
        File absolutePathTargetFile = file.getCanonicalFile();
        return absolutePathTargetFile;
    }
    

    /**
     * 
     * @return a (new) list of all asciidoc files inside the model 
     */
    public List<AsciidocFile> getAllAsciidocFiles() {
        return new ArrayList<>(fileTofileMap.values());
    }


    AsciidocFile registerNewAsciidocFile(File relativeFile) throws IOException {
        AsciidocFile asciidocFile = new AsciidocFile();
        File absoluteFile = asciidocFile.file = transformToAbsoluteFile(relativeFile);
        fileTofileMap.put(absoluteFile, asciidocFile);
        return asciidocFile;
    }

 
}
