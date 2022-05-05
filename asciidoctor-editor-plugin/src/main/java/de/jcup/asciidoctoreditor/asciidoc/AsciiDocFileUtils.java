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
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.Normalizer;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import de.jcup.asciidoctoreditor.LogAdapter;
import de.jcup.asciidoctoreditor.OSUtil;
import de.jcup.asciidoctoreditor.TemporaryFileType;
import de.jcup.asciidoctoreditor.UniqueIdProvider;

public class AsciiDocFileUtils {

    private static Set<PosixFilePermission> posixFilePermissions = PosixFilePermissions.fromString("rw-------");
    private static Set<PosixFilePermission> posixFolderPermissions = PosixFilePermissions.fromString("rwx------");
    private static final FileAttribute<?> posixFileAttributes = PosixFilePermissions.asFileAttribute(posixFilePermissions);
    private static final FileAttribute<?> posixFolderAttributes = PosixFilePermissions.asFileAttribute(posixFolderPermissions);

    private static boolean featureTurnedOff;

    static {
        // a feature toggle for the worst case that somebody has problems with the
        // feature
        if (Boolean.getBoolean("de.jcup.asciidoctoreditor.turnoff.permissioncheck")) {
            featureTurnedOff = true;
            System.out.println(">>turned off permission check");
        }
    }

    public static void ensureFileAvailableAndAccessByUserOnly(File file) throws IOException {
        ensureFileAvailableAndAccessByUserOnly(file, true);
    }

    private static boolean usePosxAttributes = true;

    public static void ensureFileAvailableAndAccessByUserOnly(File file, boolean changeExistingFiles) throws IOException {
        if (featureTurnedOff) {
            return;
        }
        if (file == null) {
            return;
        }

        boolean done = false;
        if (usePosxAttributes) {
            try {
                ensureFileAvailableAndAccessByPosix(file, changeExistingFiles);
                done = true;
            } catch (java.lang.UnsupportedOperationException e) {
                usePosxAttributes = false;
            }
        }

        if (done) {
            return;
        }
        ensureFileAvailableAndAccessWithoutPosix(file, changeExistingFiles);

    }

    private static void ensureFileAvailableAndAccessWithoutPosix(File file, boolean changeExistingFiles) throws IOException {
        if (file.exists()) {
            if (!changeExistingFiles) {
                return;
            }
        }
        /* old style via old API */
        file.setReadable(true, true);
        file.setWritable(true, true);
        
        BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        if (attrs.isDirectory()) {
            file.setExecutable(true, true);
        }

        if (!file.exists()) {
            if (attrs.isDirectory()) {
                file.mkdirs();
            } else {
                file.createNewFile();
            }
        }
    }

    private static void ensureFileAvailableAndAccessByPosix(File file, boolean changeExistingFiles) throws IOException {
        Path path = file.toPath();
        if (file.exists()) {
            if (!changeExistingFiles) {
                return;
            }
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            if (attrs.isDirectory()) {
                Files.setPosixFilePermissions(path, posixFolderPermissions);
            } else if (attrs.isRegularFile()) {
                Files.setPosixFilePermissions(path, posixFilePermissions);
            }
        } else {
            if (file.isDirectory()) {
                Files.createDirectories(path, posixFolderAttributes);
            } else {
                Files.createFile(path, posixFileAttributes);
            }
        }
    }

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
        ensureFileAvailableAndAccessByUserOnly(newTempFile);
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
            File newTempSubFolder = createSelfDeletingTempSubFolderForUser(projectId, "asciidoctor-editor");
            return newTempSubFolder.toPath();
        } catch (IOException e) {
            throw new IllegalStateException("Not able to create temp folder for editor", e);
        }
    }

    protected static File createSelfDeletingTempSubFolderForUser(String projectId, String parentFolderName) throws IOException {
        File parentTempFolder = new File(FileUtils.getTempDirectory(), parentFolderName);
        parentTempFolder.mkdirs();

        File newUserTempFolder = null;
        if (OSUtil.isWindows()) {
            /* on Windows we have something like this : "c:/users/$username/AppData/Local/Temp/asciidoctor-editor/$projectName"
             * so we do not need to add a user specific subfolder inside.
             * On Unix like systems the tmp folder is not user specific
             */
            newUserTempFolder = parentTempFolder;
        }else {
            // no special treatment for parent temp folder - shall be accessible by everybody
            String userName = System.getProperty("user.name");
            if (userName == null || userName.isEmpty()) {
                userName = "fallback-username";
            }
            newUserTempFolder = new File(parentTempFolder, userName);
            
            if (!newUserTempFolder.exists() && !newUserTempFolder.mkdirs()) {
                throw new IOException("Was not able to create folder:" + newUserTempFolder);
            }
        }
        ensureFileAvailableAndAccessByUserOnly(newUserTempFolder);
        newUserTempFolder.deleteOnExit();

        File newTempSubFolder = new File(newUserTempFolder, projectId);
        ensureFileAvailableAndAccessByUserOnly(newTempSubFolder, false);
        newTempSubFolder.deleteOnExit();

        return newTempSubFolder;
    }

    /**
     * Creates an encoding safe filename, means filename without characters not
     * being standard ascii chars...
     * 
     * @param path
     * @param name
     * @return file with normalized name
     */
    public static File createEncodingSafeFile(Path path, String name) throws IOException {

        String fileEncoding = System.getProperty("file.encoding");
        if (!("UTF-8".equalsIgnoreCase(fileEncoding))) {
            /* e.g. cp1252 in windows... */
            name = createEncodingSafeFileName(name);
        }
        File file = new File(path.toFile(), name);
        ensureFileAvailableAndAccessByUserOnly(file);
        return file;
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

            ensureFileAvailableAndAccessByUserOnly(hiddenEditorFile);

            hiddenEditorFile.deleteOnExit();

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
}
