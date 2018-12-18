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
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class AsciiDocFileUtils {

    public static File createTempFileForConvertedContent(Path tempFolder, long editorId, String filename) throws IOException {
        if (tempFolder == null) {
            tempFolder = Files.createTempDirectory("__fallback__");
        }
        File newTempSubFolder = tempFolder.toFile();

        File newTempFile = new File(newTempSubFolder, editorId + "_" + filename);
        if (newTempFile.exists()) {
            if (!newTempFile.delete()) {
                throw new IOException("Unable to delete old tempfile:" + newTempFile);
            }
        }
        newTempFile.deleteOnExit();
        return newTempFile;
    }

    /**
     * Any IO problem will throw an {@link IllegalStateException}
     * 
     * @param projectId
     * @return path, never <code>null</code>
     */
    public static Path createTempFolderForId(String projectId) {
        try {
            File newTempSubFolder = createSelfDeletingTempSubFolder(projectId, "asciidoctor-editor-temp");
            return newTempSubFolder.toPath();
        } catch (IOException e) {
            throw new IllegalStateException("Not able to create temp folder for editor", e);
        }
    }

    protected static File createSelfDeletingTempSubFolder(String tempId, String parentFolderName) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        File newTempFolder = new File(tempDir, parentFolderName);

        if (!newTempFolder.exists() && !newTempFolder.mkdirs()) {
            throw new IOException("Was not able to create folder:" + newTempFolder);
        }
        newTempFolder.deleteOnExit();

        File newTempSubFolder = new File(newTempFolder, "project_" + tempId);
        if (!newTempSubFolder.exists() && !newTempSubFolder.mkdirs()) {
            throw new IOException("not able to create temp folder:" + newTempSubFolder);
        }
        newTempSubFolder.deleteOnExit();
        return newTempSubFolder;
    }

    public static File createHiddenEditorFile(File asciidoctorFile, long editorId, File baseDir, Path tempFolder) throws IOException {
        File hiddenEditorFile = new File(tempFolder.toFile(), editorId + "_hidden-editorfile_" + asciidoctorFile.getName());

        String relativePath= calculatePathToFileFromBase(asciidoctorFile,baseDir);
        
        StringBuilder sb= new StringBuilder();
        sb.append("// origin :").append(asciidoctorFile.getAbsolutePath()).append("\n");
        sb.append("// editor :").append(editorId).append("\n");
        sb.append("// basedir:").append(baseDir.getAbsolutePath()).append("\n");
        
        sb.append("include::").append(relativePath).append("[]\n");
        
        FileUtils.writeStringToFile(hiddenEditorFile, sb.toString(), "UTF-8",false);
        hiddenEditorFile.deleteOnExit();
        return hiddenEditorFile;
    }

    static String calculatePathToFileFromBase(File asciidoctorFile, File baseDir) {
        String unixBasePath = FilenameUtils.normalizeNoEndSeparator(baseDir.getAbsolutePath(),true)+"/";
        String unixAsciiDocFilePath = FilenameUtils.normalize(asciidoctorFile.getAbsolutePath(), true);
        
        if (unixAsciiDocFilePath.startsWith(unixBasePath)){
            return unixAsciiDocFilePath.substring(unixBasePath.length());
        }
        
        return "pathProblems:"+unixAsciiDocFilePath+" not in "+unixBasePath;
    }

}
