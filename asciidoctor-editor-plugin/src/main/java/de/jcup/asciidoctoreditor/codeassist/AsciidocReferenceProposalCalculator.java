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
import java.io.FileFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class AsciidocReferenceProposalCalculator {
    
    private AsciidocReferenceExistingTextCalculator calculator;
    private String prefix;
    private BaseParentDirResolver baseParentDirResolver;
    private CodeAssistFileFilter fileFilter;
    
    /**
     * Creates proposal calculator
     * @param prefix
     * @param baseParentDirResolver
     * @param fileFilter 
     */
    public AsciidocReferenceProposalCalculator(String prefix, BaseParentDirResolver baseParentDirResolver, CodeAssistFileFilter fileFilter) {
        Objects.requireNonNull(prefix);
        Objects.requireNonNull(baseParentDirResolver);
        Objects.requireNonNull(fileFilter);
        this.fileFilter=fileFilter;
        this.prefix=prefix;
        this.baseParentDirResolver=baseParentDirResolver;
        calculator = new AsciidocReferenceExistingTextCalculator(prefix);
    }
    
    public Set<AsciidocReferenceProposalData> calculate(File editorFile, String fullSource, int indexForCtrlSpace) {
        String referenceText = calculator.resolveReferenceTextOrNull(fullSource, indexForCtrlSpace);
        if (referenceText==null) {
            return Collections.emptySet();
        }
        Path path = Paths.get(referenceText.substring(getPrefix().length()));
        
        File parent = baseParentDirResolver.getBaseParentDir(editorFile);
        if (parent==null) {
            return Collections.emptySet();
        }
      
        Path parentPath = path.getParent();
        if (parentPath!=null && !parentPath.toString().isEmpty()) {
            /* means we got something ala include::subdir1/subdir with subdir1/subdir2 as structure - so we are already inside subdir1 in path */ 
            File newParent = parent.toPath().resolve(parentPath).toFile();
            if (!newParent.exists()) {
                return Collections.emptySet();
            }
            parent = newParent;
            path = parentPath.relativize(path);
        }
        
        
        Path separatedParentPath = parent.toPath();
        String search = null; 
        if (path.toString().length() > 0) {
            /* path given */
            File newParent = separatedParentPath.resolve(path).toFile();
            if (newParent.exists()) {
                parent = newParent;
            }else {
                /* only part given? - keep parent as, but mark search*/
                search = path.toString();
            }
        }
        final String toSearch = search;
        
        File[] files = fetchFiles(editorFile, parent, toSearch);
        
        if (files==null) {
            return Collections.emptySet();
        }
        
        return buildProposals(editorFile, files);
    }

    private File[] fetchFiles(File editorFile, File parent, final String toSearch) {
        File[] files = parent.listFiles(new FileFilter() {
            
            @Override
            public boolean accept(File file) {
                if (file.equals(editorFile)){
                    /* filter editor file itself always */
                    return false;
                }
                if (fileFilter!=null) {
                    if (!fileFilter.accept(file)) {
                        return false;
                    }
                }
                if (toSearch!=null) {
                    if (file.getName().startsWith(toSearch)) {
                        return true;
                    }
                    return false;
                }
                return true;
            }
        });
        return files;
    }

    private Set<AsciidocReferenceProposalData> buildProposals(File editorFile, File[] files) {
        Path editorParentPath = baseParentDirResolver.getBaseParentDir(editorFile).toPath();
        Set<AsciidocReferenceProposalData> set = new TreeSet<AsciidocReferenceProposalData>();
        for (File child: files) {
            Path absPathChild = child.toPath();
            int editorParentPathLength = editorParentPath.toString().length();
            if (editorParentPathLength>editorParentPathLength) {
                continue;
            }
            String proposal = getPrefix()+editorParentPath.relativize(absPathChild).toString().replace("\\", "/");
            if (child.isDirectory()) {
                proposal+= "/";
            }else {
                proposal+="[]";
            }
            AsciidocReferenceProposalData asciidocReferenceProposalData = new AsciidocReferenceProposalData(proposal,proposal);
            
            set.add(asciidocReferenceProposalData);
        }
        return set;
    }

    protected String getPrefix() {
        return prefix;
    }


    
}
