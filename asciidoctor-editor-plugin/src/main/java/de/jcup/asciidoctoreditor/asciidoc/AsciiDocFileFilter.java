package de.jcup.asciidoctoreditor.asciidoc;

import java.io.File;
import java.io.FileFilter;

public class AsciiDocFileFilter implements FileFilter {
    
    private boolean acceptFolders;
    
    public AsciiDocFileFilter(boolean acceptFolders) {
        this.acceptFolders=acceptFolders;
    }

	static final String[] validFileEndings = new String[] {".adoc", ".asciidoc", ".asc",".ad"};
	@Override
	public boolean accept(File file) {
		if (file == null) {
			return false;
		}
		if (acceptFolders && file.isDirectory()) {
		    return true;
		}
		if (!file.isFile()) {
		    return false;
		}
		if(!hasValidFileEnding(file)) {
			return false;
		}
		return true;
	}
	
	
	private boolean hasValidFileEnding(File file) {
		String fileName = file.getName();
		for (String validFileEnding : validFileEndings) {
			if (fileName.endsWith(validFileEnding)) {
				return true;
			}
		}
		
		return false;
	}

}