package de.jcup.asciidoctoreditor.codeassist;

import java.io.File;
import java.io.FileFilter;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class AsciidocReferenceProposalCalculator {
    
    private static final char SEPARATOR = File.separatorChar;
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
        String path = referenceText.substring(getPrefix().length());
        
        File parent = baseParentDirResolver.getBaseParentDir(editorFile);
        if (parent==null) {
            return Collections.emptySet();
        }
      
        int lastDirIndex = path.lastIndexOf(SEPARATOR);
        if (lastDirIndex!=-1) {
            /* means we got something ala include::subdir1/subdir with subdir1/subdir2 as structure - so we are already inside subdir1 in path */ 
            String subPathBefore = path.substring(0,lastDirIndex);
            File newParent = new File(parent.getAbsolutePath()+SEPARATOR+subPathBefore);
            if (!newParent.exists()) {
                return Collections.emptySet();
            }
            parent=newParent;
            path = path.substring(lastDirIndex+1);
        }
        
        
        String separatedParentPath = parent.getAbsolutePath()+SEPARATOR;
        String search = null; 
        if (path.length()>0) {
            /* path given */
            File newParent = new File(separatedParentPath+path);
            if (newParent.exists()) {
                parent = newParent;
            }else {
                /* only part given? - keep parent as, but mark search*/
                search = path;
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
        String editorParentPath = baseParentDirResolver.getBaseParentDir(editorFile).getAbsolutePath()+SEPARATOR;
        Set<AsciidocReferenceProposalData> set = new TreeSet<AsciidocReferenceProposalData>();
        for (File child: files) {
            String absPathChild = child.getAbsolutePath();
            int editorParentPathLength = editorParentPath.length();
            if (editorParentPathLength>editorParentPathLength) {
                continue;
            }
            String proposal = getPrefix()+absPathChild.substring(editorParentPathLength);
            if (child.isDirectory()) {
                proposal+="/";
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
