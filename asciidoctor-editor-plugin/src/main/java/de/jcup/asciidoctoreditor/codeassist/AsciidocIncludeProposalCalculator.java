package de.jcup.asciidoctoreditor.codeassist;

import java.io.File;
import java.io.FileFilter;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class AsciidocIncludeProposalCalculator {
    
    private static final char SEPARATOR = File.separatorChar;
    private AsciidocIncludeExistingTextCalculator calculator = new AsciidocIncludeExistingTextCalculator();
    
    
    public AsciidocIncludeProposalCalculator() {
    }
    
    public Set<AsciidocIncludeProposalData> calculate(File editorFile, String fullSource, int indexForCtrlSpace) {
        String inspect = calculator.resolveIncludeTextOrNull(fullSource, indexForCtrlSpace);
        if (inspect==null) {
            return Collections.emptySet();
        }
        String path = inspect.substring("include::".length());
        
        File parent = editorFile.getParentFile();
      
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
        
        File[] files = parent.listFiles(new FileFilter() {
            
            @Override
            public boolean accept(File pathname) {
                if (pathname.equals(editorFile)){
                    /* filter editor file itself always */
                    return false;
                }
                if (toSearch!=null) {
                    if (pathname.getName().startsWith(toSearch)) {
                        return true;
                    }
                    return false;
                }
                return true;
            }
        });
        
        if (files==null) {
            return Collections.emptySet();
        }
        
        String editorParentPath = editorFile.getParentFile().getAbsolutePath()+SEPARATOR;
        Set<AsciidocIncludeProposalData> set = new TreeSet<AsciidocIncludeProposalData>();
        for (File child: files) {
            String absPathChild = child.getAbsolutePath();
            String include = "include::"+absPathChild.substring(editorParentPath.length());
            if (child.isDirectory()) {
                include+="/";
            }else {
                include+="[]";
            }
            AsciidocIncludeProposalData asciidocIncludeProposalData = new AsciidocIncludeProposalData(include,include);
            
            set.add(asciidocIncludeProposalData);
        }
        return set;
    }


    
}
