package de.jcup.asciidoctoreditor.model;

import java.io.File;

import de.jcup.asciidoctoreditor.RootParentFinder;

public class RootParentFinderFileAdapter implements RootParentFinder{

    private File rootParent;

    public RootParentFinderFileAdapter(File rootParent) {
        this.rootParent=rootParent;
    }
    
    @Override
    public File findRootParent() {
        return rootParent;
    }

}
