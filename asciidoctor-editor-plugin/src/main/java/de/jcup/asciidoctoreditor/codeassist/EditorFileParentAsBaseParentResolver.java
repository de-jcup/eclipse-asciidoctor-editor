package de.jcup.asciidoctoreditor.codeassist;

import java.io.File;

/**
 * Uses always editor file parent as base parent file
 * @author albert
 *
 */
public class EditorFileParentAsBaseParentResolver implements BaseParentDirResolver{

    @Override
    public File getBaseParentDir(File editorFile) {
        return editorFile.getParentFile();
    }

}
