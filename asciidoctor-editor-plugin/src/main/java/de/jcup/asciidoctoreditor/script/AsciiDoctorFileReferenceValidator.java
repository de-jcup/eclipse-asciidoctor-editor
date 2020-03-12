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
package de.jcup.asciidoctoreditor.script;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public class AsciiDoctorFileReferenceValidator {

    /**
     * Validates given references, will add error markers when not valid. Validation means that the absolute file is calculated and checked for existence.
     * 
     * @param referenceBaseFileOrFolder - depends on kind of given reference collection: For image references it should be either the imagesDir (if set ) or the current editorfile, for diagrams the diagram path, for includes the 
     * always the editor file. 
     * @param references collection containing file references to check if valid by
     *                   given base file
     * @param errors  collection where found errors are added
     */
    public void validate(File referenceBaseFileOrFolder, Collection<AsciiDoctorFileReference> references, Collection<AsciiDoctorMarker> errors) {
        if (errors == null) {
            return;
        }
        if (referenceBaseFileOrFolder == null) {
            return;
        }
        if (!referenceBaseFileOrFolder.exists()) {
            return;
        }
        File referenceBaseDirectory = referenceBaseFileOrFolder;
        if (!referenceBaseDirectory.isDirectory()) {
            referenceBaseDirectory = referenceBaseDirectory.getParentFile();
        }
        for (AsciiDoctorFileReference reference : references) {

            /* ------------------------ */
            /* calculate expected file */
            /* ------------------------ */
            String filePathAsString = reference.getFilePath();

            Path referenceBaseDirPath = referenceBaseDirectory.toPath();
            Path filePath = Paths.get(filePathAsString);

            /* here we resolve the referenced file by given reference base directory */
            File expectedFile = referenceBaseDirPath.resolve(filePath).toFile();

            /* -------------------------------- */
            /* check file exists and is file ... */
            /* -------------------------------- */
            String problem = null;
            if (!expectedFile.exists()) {
                problem = ".. references not existing file:" + expectedFile.getAbsolutePath();
            } else if (expectedFile.isDirectory()) {
                problem = ".. points to a directory not a file:" + expectedFile.getAbsolutePath();
            }

            if (problem != null) {
                AsciiDoctorMarker marker = new AsciiDoctorMarker(reference.getPosition(), reference.getEnd(), ValidationConstants.VALIDATION_FAILED + reference.getTargetPrefix() + problem);
                errors.add(marker);
            }
        }
    }

}
