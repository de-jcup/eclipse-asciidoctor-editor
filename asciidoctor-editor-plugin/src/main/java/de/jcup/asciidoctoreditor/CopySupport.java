/*
 * Copyright 2020 Albert Tregnaghi
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CopySupport {

    private File oldBaseFolder;
    private File newBaseFolder;

    public CopySupport(File oldBaseFolder, File newBaseFolder) {
        this.oldBaseFolder=oldBaseFolder;
        this.newBaseFolder=newBaseFolder;
    }
    
    
    protected List<File> createTargetFiles(List<File> files){
        List<File> result = new ArrayList<File>();
        Path targetParentPath = newBaseFolder.toPath();
        Path parentPath = oldBaseFolder.toPath();
        for (File file: files) {
            Path path = file.toPath();
            Path relativePath = parentPath.relativize(path);
            Path targetPath = targetParentPath.resolve(relativePath);
            result.add(targetPath.toFile());
        }
        return result;
    }
    
    public void copyFilesToNewBase(List<File> sourceFiles) throws IOException {
        List<File> targetFiles = createTargetFiles(sourceFiles);
        for (int i=0;i<sourceFiles.size();i++) {
            Path sourcePath = sourceFiles.get(i).toPath();
            Path targetPath = targetFiles.get(i).toPath();
            Files.copy(sourcePath, targetPath);
        }
    }
}
