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
package de.jcup.asciidoctoreditor.asciidoc;

import java.io.File;
import java.io.FileFilter;

public class AsciiDocFileFilter implements FileFilter {

    /**
     * File filter which accepts all files with wellknown Asciidoc file endings AND
     * also every folder
     */
    public static final AsciiDocFileFilter ASCIIDOC_FILES_AND_FOLDERS = new AsciiDocFileFilter(true);
    /**
     * File filter which accepts all files with wellknown Asciidoc file endings, but
     * nothing else (means also NO folders)
     */
    public static final AsciiDocFileFilter ASCIIDOC_FILES_ONLY = new AsciiDocFileFilter(false);

    private boolean acceptFolders;

    private AsciiDocFileFilter(boolean acceptFolders) {
        this.acceptFolders = acceptFolders;
    }

    static final String[] validFileEndings = new String[] { ".adoc", ".asciidoc", ".asc", ".ad" };

    @Override
    public boolean accept(File file) {
        if (file == null) {
            return false;
        }
        if (acceptFolders && file.isDirectory()) {
            return true;
        }
        if (file.isDirectory()) {
            return false;
        }
        if (!hasValidFileEnding(file)) {
            return false;
        }
        return true;
    }

    public static boolean hasValidFileEnding(File file) {
        if (file == null) {
            return false;
        }
        String fileName = file.getName();
        return hasValidFileEnding(fileName);
    }

    public static boolean hasValidFileEnding(String fileName) {
        if (fileName == null) {
            return false;
        }
        for (String validFileEnding : validFileEndings) {
            if (fileName.endsWith(validFileEnding)) {
                return true;
            }
        }

        return false;
    }

}