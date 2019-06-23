package de.jcup.asciidoctoreditor.codeassist;

import java.io.File;
import java.util.LinkedHashSet;

public class CodeAssistFileFilter{
    
    private LinkedHashSet<String> accepted;
    private boolean ignoreCase;

    public CodeAssistFileFilter(String ...acceptedFileEndings) {
        accepted = new LinkedHashSet<String>();
        for (String acceptedFileEnding:acceptedFileEndings) {
            if (acceptedFileEnding==null) {
                continue;
            }
            accepted.add(acceptedFileEnding);
        }
    }
    
    public CodeAssistFileFilter ignoreCase() {
        LinkedHashSet<String> acceptedNew= new LinkedHashSet<String>();
        this.ignoreCase=true;
        for (String acceptedCase: accepted) {
            acceptedNew.add(acceptedCase.toLowerCase());
        }
        accepted=acceptedNew;
        return this;
    }

    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        return acceptFile(file);
    }

    private boolean acceptFile(File file) {
        String name = file.getName();
        int lastIndex = name.lastIndexOf('.');
        if (lastIndex==-1) {
            return false;
        }
        if (accepted.isEmpty()) {
            /* nothing defined , so always true, we keep everything*/
            return true;
        }
        String fileEnding = name.substring(lastIndex);
        if (ignoreCase) {
            fileEnding=fileEnding.toLowerCase();
        }
        return accepted.contains(fileEnding);
    }

}
