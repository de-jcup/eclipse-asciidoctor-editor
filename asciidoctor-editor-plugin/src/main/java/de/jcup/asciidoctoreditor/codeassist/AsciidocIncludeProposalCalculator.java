package de.jcup.asciidoctoreditor.codeassist;

import java.io.File;
import java.io.FileFilter;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class AsciidocIncludeProposalCalculator {
    
    private AsciidocIncludeExistingTextCalculator calculator = new AsciidocIncludeExistingTextCalculator();
    
    public Set<AsciidocIncludeProposalData> calculate(File editorFile, String fullSource, int indexForCtrlSpace) {
        String inspect = calculator.resolveIncludeTextOrNull(fullSource, indexForCtrlSpace);
        if (inspect==null) {
            return Collections.emptySet();
        }
        String path = inspect.substring("include::".length());
        
       
        File parent = editorFile.getParentFile();
        String editorParentPath = parent.getAbsolutePath()+"/";
        String search = null;
        if (path.length()>0) {
            /* path given */
            File f = new File(parent.getAbsolutePath()+"/"+path);
            if (f.exists()) {
                parent = f;
            }else {
                /* only part given? - keep parent as, but mark search*/
                search = path;
            }
        }
        final String toSearch = search;
        
        File[] files = parent.listFiles(new FileFilter() {
            
            @Override
            public boolean accept(File pathname) {
                if (toSearch!=null) {
                    if (pathname.getName().startsWith(toSearch)) {
                        return true;
                    }
                    return false;
                }
                if (pathname.isDirectory()) {
                    return true;
                }
                return pathname.getName().endsWith(".adoc");
            }
        });
        
        if (files==null) {
            return Collections.emptySet();
        }
        Set<AsciidocIncludeProposalData> set = new TreeSet<AsciidocIncludeProposalData>();
        for (File child: files) {
            if (child.equals(editorFile)){
                continue;
            }
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
