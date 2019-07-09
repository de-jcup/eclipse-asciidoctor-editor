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
