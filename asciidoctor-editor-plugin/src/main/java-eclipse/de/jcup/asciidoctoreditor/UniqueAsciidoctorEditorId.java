package de.jcup.asciidoctoreditor;

import java.io.File;

import org.eclipse.core.runtime.IPath;

public class UniqueAsciidoctorEditorId implements UniquePrefixProvider {

    private String uniquePrefix;
    private String originFileLocationPath;

    public UniqueAsciidoctorEditorId(IPath path) {
        if (path != null) {
            File file = path.toFile();
            originFileLocationPath = file.getAbsolutePath();
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
