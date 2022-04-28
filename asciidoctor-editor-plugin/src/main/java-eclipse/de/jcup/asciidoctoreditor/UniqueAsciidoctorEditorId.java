package de.jcup.asciidoctoreditor;

import org.eclipse.core.resources.IFile;

public class UniqueAsciidoctorEditorId {

    private String uniquePrefix;

    public UniqueAsciidoctorEditorId(IFile file) {
        String fileLocationPath = null;
        if (file != null) {
            fileLocationPath = file.getFullPath().toFile().getAbsolutePath();
        } else {
            fileLocationPath = "fallback_" + System.nanoTime();
        }
        uniquePrefix = Sha256Support.SHARED_INSTANCE.createChecksum(fileLocationPath);
    }
    
    public String getUniquePrefix() {
        return uniquePrefix;
    }
    @Override
    public String toString() {
        return getUniquePrefix();
    }

}
