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
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import de.jcup.asciidoctoreditor.LogAdapter;
import de.jcup.asciidoctoreditor.UniqueAsciidoctorEditorId;

public class AsciiDocFileUtils {

    
    
    /**
     * Creates a flat filename, which contains of ${messageDigestAbsolutePath}_{fileNameWithEnding}
     * @param file
     * @param encoder
     * @return flat filename
     */
    public static String createFlatFileName(File file, StringEncoder encoder) {
        String name = file.getName();
        String parentCanonicalPath;
        try {
            parentCanonicalPath = file.getParentFile().getCanonicalPath();
        } catch (IOException e) {
            parentCanonicalPath = "not_canonical_"+System.currentTimeMillis();
        }
        return createEncodingSafeFileName(encoder.encode(parentCanonicalPath)+"_"+name);
    }
    
    public static File createTempFileForConvertedContent(Path tempFolder, UniqueAsciidoctorEditorId editorId, String filename) throws IOException {
        if (tempFolder == null) {
            tempFolder = Files.createTempDirectory("__fallback__");
        }
        File newTempSubFolder = tempFolder.toFile();

        File newTempFile = new File(newTempSubFolder, editorId.getUniquePrefix() + "_" + filename);
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

    /**
     * Creates an encoding safe filename, means filename without characters not being standard ascii chars...
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

    protected static String createEncodingSafeFileName(String name) {
        return Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public static File createHiddenEditorFile(LogAdapter logAdapter, File asciidoctorFile, UniqueAsciidoctorEditorId editorId, File baseDir, Path tempFolder, List<AsciidoctorConfigFile> configFiles, String rootConfigFolder)
            throws IOException {
        /* @formatter:off
         * 
         * Issue:https://github.com/de-jcup/eclipse-asciidoctor-editor/issues/193
         * Problem was: eclipse started with another file encoding set than
         * UTF-8, so File.getName() makes problems in embedded asciidoctor instance 
         * 
         * see also https://stackoverflow.com/questions/10106161/encoding-of-file-names-in-java
         * 
         * So we create the hidden editor file as encoding safe file when not UTF-8 is set as default file encoding on system
         * @formatter:on
         */
        File hiddenEditorFile = createEncodingSafeFile(tempFolder, editorId.getUniquePrefix() + "_hidden-editorfile_" + asciidoctorFile.getName());
        
        try {
            String relativePath = calculatePathToFileFromBase(asciidoctorFile, baseDir);
            StringBuilder sb = new StringBuilder();
            sb.append("// origin :").append(asciidoctorFile.getAbsolutePath()).append("\n");
            sb.append("// editor :").append(editorId.getUniquePrefix()).append("\n");
            sb.append("// basedir:").append(baseDir.getAbsolutePath()).append("\n\n");
            sb.append("// ************************:\n");
            sb.append("// asciidoctorconfig files:\n");
            sb.append("// ************************:\n");
            sb.append("// rootConfigFolder: ").append(rootConfigFolder).append("\n");
            /** append config file information */
            int fc = 1;
            for (AsciidoctorConfigFile configFile: configFiles){
                sb.append("\n// config file:").append(fc++).append(", location=").append(configFile.getLocation()).append("\n");
                sb.append(configFile.getContentCustomized());
            }
            sb.append("\ninclude::").append(relativePath).append("[]\n");

            FileUtils.writeStringToFile(hiddenEditorFile, sb.toString(), "UTF-8", false);
            hiddenEditorFile.deleteOnExit();
        } catch (NotInsideCurrentBaseDirException e) {
            /*
             * fallback to orign file - maybe something does not work but at
             * least content will be shown!
             */
            logAdapter.logWarn("File not in current base dir so copied origin as hidden file:" + asciidoctorFile.getAbsolutePath());
            FileUtils.copyFile(asciidoctorFile, hiddenEditorFile);
        }

        return hiddenEditorFile;
    }

    static String calculatePathToFileFromBase(File asciidoctorFile, File baseDir) {
        String unixBasePath = FilenameUtils.normalizeNoEndSeparator(baseDir.getAbsolutePath(), true) + "/";
        String unixAsciiDocFilePath = FilenameUtils.normalize(asciidoctorFile.getAbsolutePath(), true);

        if (unixAsciiDocFilePath.startsWith(unixBasePath)) {
            return unixAsciiDocFilePath.substring(unixBasePath.length());
        }

        throw new NotInsideCurrentBaseDirException("pathProblems:" + unixAsciiDocFilePath + " not in " + unixBasePath);
    }

    public static class NotInsideCurrentBaseDirException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public NotInsideCurrentBaseDirException(String string) {
            super(string);
        }

    }
}
