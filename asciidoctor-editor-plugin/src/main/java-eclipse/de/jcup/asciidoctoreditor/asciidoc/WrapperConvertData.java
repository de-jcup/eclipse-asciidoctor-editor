package de.jcup.asciidoctoreditor.asciidoc;

import java.io.File;

import de.jcup.asciidoctoreditor.EditorType;

public class WrapperConvertData {
	public EditorType targetType = EditorType.ASCIIDOC;
	public File asciiDocFile;
	public long editorId;
	public boolean useHiddenFile;
	public File editorFileOrNull;
    public boolean internalPreview;
}