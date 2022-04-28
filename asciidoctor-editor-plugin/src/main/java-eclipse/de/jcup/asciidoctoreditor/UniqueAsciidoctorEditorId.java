package de.jcup.asciidoctoreditor;

import org.eclipse.core.resources.IFile;

public class UniqueAsciidoctorEditorId implements UniquePrefixProvider {

    private String uniquePrefix;
    private String originFileLocationPath;

    public UniqueAsciidoctorEditorId(IFile file) {
        if (file != null) {
            originFileLocationPath = file.getFullPath().toFile().getAbsolutePath();
        } else {
            originFileLocationPath = "fallback_for_missing_file_" + System.nanoTime();
        }
        uniquePrefix = Sha256Support.SHARED_INSTANCE.createChecksum(originFileLocationPath);
    }
    
    @Override
    public String getUniquePrefix() {
        return uniquePrefix;
    }
    
    public String getOriginFileLocationPath() {
        return originFileLocationPath;
    }
    
    @Override
    public String toString() {
        return getUniquePrefix();
    }

}
