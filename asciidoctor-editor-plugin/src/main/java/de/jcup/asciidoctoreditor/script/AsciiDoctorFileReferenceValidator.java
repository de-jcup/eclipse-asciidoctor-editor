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
import java.nio.file.Paths;
import java.util.Collection;

public class AsciiDoctorFileReferenceValidator {
    
    /**
     * Validates given references, will add markers into error when not valid
     * @param baseFile
     * @param references
     * @param errors
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
        File folder =baseFile;
        if (!folder.isDirectory()) {
            folder=folder.getParentFile();
        }
        for (AsciiDoctorFileReference reference: references) {
            String target = reference.getFilePath();
            File file = folder.toPath().resolve(Paths.get(target)).toFile();
            String problem = null;
            if (! file.exists()) {
                problem = ".. references not existing file:"+file.getAbsolutePath();
            }else if (file.isDirectory()) {
                problem= "..  points to a directory not a file:"+file.getAbsolutePath();
            }
            
            if (problem!=null) {
                AsciiDoctorMarker marker = new AsciiDoctorMarker(reference.getPosition(), reference.getEnd(), ValidationConstants.VALIDATION_FAILED+reference.getTargetPrefix()+problem);
                errors.add(marker);
            }
        }
    }

}
