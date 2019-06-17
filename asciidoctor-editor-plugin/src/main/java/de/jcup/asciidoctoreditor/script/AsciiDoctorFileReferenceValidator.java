package de.jcup.asciidoctoreditor.script;

import java.io.File;
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
            File file = new File(folder.getAbsolutePath()+File.separatorChar+target);
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
