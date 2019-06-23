package de.jcup.asciidoctoreditor.codeassist;

import java.io.File;

/**
 * Resolves base parent directory by a given editor file
 * @author albert
 *
 */
public interface BaseParentDirResolver {

    public File getBaseParentDir(File editorFile);
}
