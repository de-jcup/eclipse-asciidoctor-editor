package de.jcup.asciidoctoreditor;

import java.io.File;
import java.io.IOException;

public class InstalledJavaBinaryPathResolver {
    
    private SystemAccess access;

    public InstalledJavaBinaryPathResolver(SystemAccess access) {
        this.access=access;
    }
    
    public String resolvePathToJavaBinary() {
        String javaHome = access.getProperty("java.home");
        if (javaHome == null || javaHome.isEmpty()) {
            return "";
        }
        File binFolder = new File(javaHome,"bin");

        String javaFileName = null;
        if (OSUtil.isWindows()) {
            javaFileName="java.exe";
        }else {
            javaFileName="java";
        }
        File javaFile = new File(binFolder, javaFileName);
        if (! javaFile.exists()) {
            return "";
        }
        
        try {
            return javaFile.getCanonicalPath();
        } catch (IOException e) {
            return "";
        }
    }
}
