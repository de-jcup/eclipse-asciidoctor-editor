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
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import de.jcup.asciidoctoreditor.LogAdapter;
import de.jcup.asciidoctoreditor.TemporaryFileType;
import de.jcup.asciidoctoreditor.UniqueIdProvider;

public class AsciiDocFileUtils {

    private static File homeSubFolder;
    private static File editorRootTempFolder;

    public static File createTempFileForConvertedContent(Path tempFolder, UniqueIdProvider uniqueIdProvider, String filename) throws IOException {
        if (tempFolder == null) {
            tempFolder = Files.createTempDirectory("__fallback__");
        }
        File newTempSubFolder = tempFolder.toFile();

        File newTempFile = new File(newTempSubFolder, uniqueIdProvider.getUniqueId() + "_" + filename);
        if (newTempFile.exists()) {
            if (!newTempFile.delete()) {
                throw new IOException("Unable to delete old tempfile:" + newTempFile);
            }
        }
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
            File newTempSubFolder = createTempSubFolder(projectId);
            return newTempSubFolder.toPath();
        } catch (IOException e) {
            throw new IllegalStateException("Not able to create temp folder for editor", e);
        }
    }

    protected static File createTempSubFolder(String projectId) throws IOException {
        File newTempSubFolder = new File(getEditorRootTempFolder(), projectId);
        if (!newTempSubFolder.exists() && !newTempSubFolder.mkdirs()) {
            throw new IOException("Was not able to create temp folder:" + newTempSubFolder);
        }
        return newTempSubFolder;
    }

    /**
     * Creates an encoding safe filename, means filename without characters not
     * being standard ascii chars...
     * 
     * @param path
     * @param name
     * @return file with normalized name. Will automatically delete on JVM exit
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

    public static File createHiddenEditorFile(LogAdapter logAdapter, File asciidoctorFile, UniqueIdProvider uniqueIdProvider, File baseDir, Path tempFolder, List<AsciidoctorConfigFile> configFiles,
            String rootConfigFolder) throws IOException {
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
        File hiddenEditorFile = createEncodingSafeFile(tempFolder, uniqueIdProvider.getUniqueId() + "_" + TemporaryFileType.HIDDEN_EDITOR_FILE.getPrefix() + asciidoctorFile.getName());

        try {
            String relativePath = calculatePathToFileFromBase(asciidoctorFile, baseDir);
            StringBuilder sb = new StringBuilder();
            sb.append("// origin :").append(asciidoctorFile.getAbsolutePath()).append("\n");
            sb.append("// editor :").append(uniqueIdProvider.getUniqueId()).append("\n");
            sb.append("// basedir:").append(baseDir.getAbsolutePath()).append("\n\n");
            sb.append("// ************************:\n");
            sb.append("// asciidoctorconfig files:\n");
            sb.append("// ************************:\n");
            sb.append("// rootConfigFolder: ").append(rootConfigFolder).append("\n");
            /** append config file information */
            int fc = 1;
            for (AsciidoctorConfigFile configFile : configFiles) {
                sb.append("\n// config file:").append(fc++).append(", location=").append(configFile.getLocation()).append("\n");
                sb.append(configFile.getContentCustomized());
            }
            sb.append("\ninclude::").append(relativePath).append("[]\n");

            FileUtils.writeStringToFile(hiddenEditorFile, sb.toString(), "UTF-8", false);
        } catch (NotInsideCurrentBaseDirException e) {
            /*
             * fallback to orign file - maybe something does not work but at least content
             * will be shown!
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

    public static File getEditorHomeSubFolder() {
        if (homeSubFolder == null) {
            homeSubFolder = new File(System.getProperty("user.home"), ".eclipse-asciidoctor-editor");
            homeSubFolder.mkdirs();
        }
        return homeSubFolder;
    }

    public static int deleteEmptyFolders(final Path destination, boolean onlyDestinationChildren) throws IOException {
        AtomicInteger countDeletedEmptyFolders = new AtomicInteger();
        try {
            Files.find(destination, 30, (path, basicFileAttrs) -> Files.isDirectory(path)).forEach(folderToDelete -> {
                try {
                    boolean shallDelete = !onlyDestinationChildren || !destination.equals(folderToDelete);
                    shallDelete = shallDelete && Files.list(folderToDelete).count() == 0;
                    if (shallDelete) {
                        Files.delete(folderToDelete);
                        countDeletedEmptyFolders.incrementAndGet();
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (RuntimeException e) {
            throw new IOException("Was not able to delete files", e);
        }

        return countDeletedEmptyFolders.get();
    }

    public static int deleteFiles(final Path destination, final Integer daysToKeep) throws IOException {

        Instant retentionFilePeriod = ZonedDateTime.now().minusDays(daysToKeep).toInstant();

        AtomicInteger countDeletedFiles = new AtomicInteger();
        try {
            Files.find(destination, 30, (path, basicFileAttrs) -> basicFileAttrs.lastModifiedTime().toInstant().isBefore(retentionFilePeriod)).forEach(fileToDelete -> {
                try {
                    if (!Files.isDirectory(fileToDelete)) {
                        Files.delete(fileToDelete);
                        countDeletedFiles.incrementAndGet();
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        } catch (RuntimeException e) {
            throw new IOException("Was not able to delete files", e);
        }

        return countDeletedFiles.get();
    }

    public static File getEditorRootTempFolder() {
        if (editorRootTempFolder == null) {
            editorRootTempFolder = new File(getEditorHomeSubFolder(), "tmp");
            editorRootTempFolder.mkdirs();
        }
        return editorRootTempFolder;
    }

    public static void deleteEmptyFoldersAndTempFilesOlderThanDaysAnd(int days) throws IOException {
        Path path = getEditorRootTempFolder().toPath();

        deleteFiles(path, days);

        int emptyFoldersDeleted = 0;
        do {
            emptyFoldersDeleted = deleteEmptyFolders(path, true);
        } while (emptyFoldersDeleted != 0);
    }
}
