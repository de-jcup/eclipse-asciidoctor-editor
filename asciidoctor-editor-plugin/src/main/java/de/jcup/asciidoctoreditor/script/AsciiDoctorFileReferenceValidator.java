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
    
    private String imageDir;

    /**
     * Set image directory to use for validation. 
     * @param imageDir directory as string or <code>null</code>
     */
    public void setImageDir(String imageDir) {
        this.imageDir = imageDir;
    }

    /**
     * Validates given references, will add error markers when not valid
     * @param baseFile
     * @param references collection containing file references to check if valid by given base file 
     * @param errors collection where found errors are added
     */
    public void validate(File baseFile, Collection<AsciiDoctorFileReference> references, Collection<AsciiDoctorMarker> errors) {
        if (errors==null) {
            return;
        }
        if (baseFile==null) {
            return;
        }
        if (! baseFile.exists()) {
            return;
        }
        File baseDir =baseFile;
        if (!baseDir.isDirectory()) {
            baseDir=baseDir.getParentFile();
        }
        for (AsciiDoctorFileReference reference: references) {
            
            /* ------------------------ */
            /* calculate expected file */
            /* ------------------------ */
            String filePathAsString = reference.getFilePath();
            
            File expectedFile = null;
            
            Path baseDirPath = baseDir.toPath();
            Path filePath = Paths.get(filePathAsString);
            
            if(imageDir != null && reference.isImageReference()) {
                /* resolve path by given image directory - means absolute file path is calculated by given image directory */
                expectedFile = baseDirPath.resolve(imageDir).resolve(filePath).toFile();
            }else {
                /* here we resolve the file by base dir only */
                expectedFile = baseDirPath.resolve(filePath).toFile();
            }
            
            /* -------------------------------- */
            /* check file exists and is file ...*/
            /* -------------------------------- */
            String problem = null;
            if (! expectedFile.exists()) {
                problem = ".. references not existing file:"+expectedFile.getAbsolutePath();
            }else if (expectedFile.isDirectory()) {
                problem= "..  points to a directory not a file:"+expectedFile.getAbsolutePath();
            }
            
            if (problem!=null) {
                AsciiDoctorMarker marker = new AsciiDoctorMarker(reference.getPosition(), reference.getEnd(), ValidationConstants.VALIDATION_FAILED+reference.getTargetPrefix()+problem);
                errors.add(marker);
            }
        }
    }

}
