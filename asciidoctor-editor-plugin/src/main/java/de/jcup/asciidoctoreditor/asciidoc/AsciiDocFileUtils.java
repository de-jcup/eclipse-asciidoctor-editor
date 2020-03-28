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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.Normalizer;

import org.apache.commons.io.FilenameUtils;

public class AsciiDocFileUtils {

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

    public static String calculateRelativePathToFileFromBase(File asciidoctorFile, File relativePathBaseDir) {
        String unixBasePath = FilenameUtils.normalizeNoEndSeparator(relativePathBaseDir.getAbsolutePath(), true) + "/";
        String unixAsciiDocFilePath = null;
        if (asciidoctorFile.isDirectory()) {
            unixAsciiDocFilePath = FilenameUtils.normalizeNoEndSeparator(asciidoctorFile.getAbsolutePath(), true) + "/";
        } else {
            unixAsciiDocFilePath = FilenameUtils.normalize(asciidoctorFile.getAbsolutePath(), true);
        }

        if (unixAsciiDocFilePath.startsWith(unixBasePath)) {
            return unixAsciiDocFilePath.substring(unixBasePath.length());
        }

        throw new NotInsideCurrentBaseDirException("pathProblems:" + unixAsciiDocFilePath + " not in " + unixBasePath);
    }

    static String createEncodingSafeFileName(String name) {
        return Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    /**
     * Creates an encoding safe filename, means filename without characters not
     * being standard ascii chars...
     * 
     * @param path
     * @param name
     * @return file with normalized name
     */
    public static File createEncodingSafeFile(Path path, String name) {

        String fileEncoding = System.getProperty("file.encoding");
        if (!("UTF-8".equalsIgnoreCase(fileEncoding))) {
            /* e.g. cp1252 in windows... */
            name = createEncodingSafeFileName(name);
        }
        return new File(path.toFile(), name);
    }

    private static File createSelfDeletingTempSubFolder(String tempId, String parentFolderName) throws IOException {
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

    public static class NotInsideCurrentBaseDirException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public NotInsideCurrentBaseDirException(String string) {
            super(string);
        }

    }

}
