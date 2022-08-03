package de.jcup.asciidoctoreditor.globalmodel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.jcup.asciidoctoreditor.LogAdapter;

public class GlobalAsciidocModel {
    
    public static GlobalAsciidocModelBuilder builder() {
        return new GlobalAsciidocModelBuilder();
    }

    private Map<File, AsciidocFile> fileTofileMap = new HashMap<>();

    public AsciidocFile getAsciidocFileOrNull(File startFile, LogAdapter logAdapter) {
        if (startFile==null) {
            throw new IllegalArgumentException("Given start file to for global model creation must not be null!");
        }
        if (logAdapter==null) {
            throw new IllegalArgumentException("Log adapter may not be null!");
        }
        File absoluteFile;
        try {
            absoluteFile = startFile.getCanonicalFile();
            return fileTofileMap.get(absoluteFile);
        } catch (IOException e) {
            logAdapter.logError("Was not able to fetch asciidocfile for "+startFile, e);
            return null;
        }
    }
    
    
    private File transformToAbsoluteFile(File file) throws IOException {
        File absolutePathTargetFile = file.getCanonicalFile();
        return absolutePathTargetFile;
    }
    

    /**
     * 
     * @return a (new) list of all asciidoc files inside the model 
     */
    public List<AsciidocFile> getAllAsciidocFiles() {
        return new ArrayList<>(fileTofileMap.values());
    }


    AsciidocFile registerNewAsciidocFile(File relativeFile) throws IOException {
        AsciidocFile asciidocFile = new AsciidocFile();
        File absoluteFile = asciidocFile.file = transformToAbsoluteFile(relativeFile);
        fileTofileMap.put(absoluteFile, asciidocFile);
        return asciidocFile;
    }

 
}
