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
import de.jcup.asciidoctoreditor.diagram.plantuml.PlantUMLFileEndings;

public class AsciiDoctorBaseDirectoryProvider extends AbstractAsciiDoctorProvider {
    private static final int MAX_ACCPTED_EMPTY_FOLDERS = 3;

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

    private File findProjectBaseDirNotCached(File startFrom) {
        return findProjectBaseDirNotCached(startFrom, startFrom, 0);
    }

    private File findProjectBaseDirNotCached(File startFrom, File potential, int potentialSearchDeeperCount) {
        getContext().getLogAdapter().resetTimeDiff();
        File file = resolveUnSaveProjectBaseDir(startFrom, potential, potentialSearchDeeperCount);
        File tempFolder = getTempFolder();
        if (tempFolder.equals(file) || tempFolder.equals(potential)) {
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

    private File resolveUnSaveProjectBaseDir(File dir, File potential, int potentialDeepness) {
        /* @formatter:on */
        // gradle-or-mavenproject
        // .project ?
        // .gradle
        // .pom.xml
        // sub-project-doc1
        // .project
        // src/doc/book.adoc
        // chapters/
        // chapter01/
        // first-parts-chapter01.adoc
        // sub-project-doc2
        // .project
        // src/doc/book.adoc
        // chapters/
        // chapter01/
        // first-parts-chapter01.adoc
        // sub-project-doc3
        // .project
        // src/doc/shared
        // chapters/
        // chapter01/
        // headlines.adoc
        /* @formatter:off */
        // Simple approach: just go up until no longer any asciidoc files
        // are found - accept 3 folders without .adoc files between.
        // 
        // if no longer .adoc files assume this is the end and use directory
        if (dir == null) {
            return new File(".");// should not happen but fall back...
        }
        File parentFile = dir.getParentFile();
        if (parentFile==null) {
            return dir;
        }
        if (getTempFolder().equals(parentFile)) {
            /*
             * when we come to our base temp folder this will be a stop at all - avoid
             * effects occurred in issue_97
             */
            return potential;
        }
        if (containsADocFiles(parentFile)) {
            return findProjectBaseDirNotCached(parentFile,parentFile,0);
        }else {
            /* parent file could be empty...*/
            if (potentialDeepness<MAX_ACCPTED_EMPTY_FOLDERS) {
                return findProjectBaseDirNotCached(parentFile,potential,potentialDeepness+1);
            }else {
                if (potential!=null) {
                    return potential;
                }else {
                    return dir;
                }
            }
        }
    }

    private File findCachedProjectBaseDirOrStartSearch(File startFrom) {
        if (startFrom == null) {
            throw new IllegalStateException("No start from defined - but must be!");
        }
        File cachedProjectBaseDir = baseDirCache.get(startFrom);
        if (cachedProjectBaseDir == null) {
            cachedProjectBaseDir = findProjectBaseDirNotCached(startFrom);
            baseDirCache.put(startFrom, cachedProjectBaseDir);
        }
        return cachedProjectBaseDir;
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

    public File findProjectBaseDir() {
        File asciiDocFile = getContext().getAsciiDocFile();
        File editorFileOrNull = getContext().getEditorFileOrNull();

        /*
         * in case of plantuml we use the parent folder as base directory. Avoids
         * problems with includes!
         */
        if (PlantUMLFileEndings.isPlantUmlFile(editorFileOrNull)) {
            return editorFileOrNull.getParentFile();
        }
        if (asciiDocFile == null) {
            throw new IllegalStateException("No asciidoc file set!");
        }
        return findCachedProjectBaseDirOrStartSearch(asciiDocFile.getParentFile());
    }

    public void reset() {
        baseDirCache.clear();
    }
}
