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
package de.jcup.asciidoctoreditor.provider;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

import de.jcup.asciidoctoreditor.asciidoc.AsciiDocFileFilter;

public class AsciiDoctorBaseDirectoryProvider extends AbstractAsciiDoctorProvider {
    private static FileFilter ADOC_FILE_FILTER = new AsciiDocFileFilter(false);
    private Map<File, File> baseDirCache = new HashMap<>();

    AsciiDoctorBaseDirectoryProvider(AsciiDoctorProviderContext context) {
        super(context);
    }

    private File tempFolder;

    public void setTempFolder(File tempFolder) {
        this.tempFolder = tempFolder;
    }

    public File getTempFolder() {
        if (tempFolder == null) {
            tempFolder = new File(System.getProperty("java.io.tmpdir"));
        }
        return tempFolder;
    }

    private File findBaseDirNotCached(File startFrom) {
        getContext().getLogAdapter().resetTimeDiff();
        File file = resolveUnSaveBaseDir(startFrom);
        File tempFolder = getTempFolder();
        if (tempFolder.equals(file)) {
            /*
             * this is a fuse - we got this situation with
             * https://github.com/de-jcup/eclipse-asciidoctor-editor/issues/97 . It will be
             * fixed, but preventing those situations is very important. So this exception
             * will break cycles and is also check in a test case
             */
            throw new IllegalStateException("Tempfolder may never be the base dir folder!\nTempfolder:" + tempFolder.getAbsolutePath());
        }
        getContext().getLogAdapter().logTimeDiff("findBaseDirNotCached, started from:" + startFrom + ", result:" + file);
        return file;
    }

    private File resolveUnSaveBaseDir(File dir) {
        // very simple approach just go up until no longer any asciidoc files
        // are found
        // if no longer .adoc files assume this is the end and use directory
        if (dir == null) {
            return new File(".");// should not happen but fall back...
        }
        File parentFile = dir.getParentFile();
        if (getTempFolder().equals(parentFile)) {
            /*
             * when we come to our base temp folder this will be a stop at all - avoid
             * effects occurred in issue_97
             */
            return dir;
        }
        if (containsADocFiles(parentFile)) {
            return findBaseDirNotCached(parentFile);
        }
        return dir;
    }

    private File findCachedBaseDirOrStartSearch(File startFrom) {
        if (startFrom == null) {
            throw new IllegalStateException("No start from defined - but must be!");
        }
        File cachedBaseDir = baseDirCache.get(startFrom);
        if (cachedBaseDir == null) {
            cachedBaseDir = findBaseDirNotCached(startFrom);
            baseDirCache.put(startFrom, cachedBaseDir);
        }
        return cachedBaseDir;
    }

    private boolean containsADocFiles(File dir) {
        if (!dir.isDirectory()) {
            return false;
        }
        File[] files = dir.listFiles(ADOC_FILE_FILTER);
        if (files.length == 0) {
            return false;
        }
        return true;
    }

    public File findBaseDir() {
        File asciiDocFile = getContext().getAsciiDocFile();
        if (asciiDocFile == null) {
            throw new IllegalStateException("No asciidoc file set!");
        }
        return findCachedBaseDirOrStartSearch(asciiDocFile.getParentFile());
    }

    public void reset() {
        baseDirCache.clear();
    }
}
