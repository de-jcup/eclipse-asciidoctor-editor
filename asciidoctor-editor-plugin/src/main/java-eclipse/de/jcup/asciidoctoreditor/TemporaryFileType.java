package de.jcup.asciidoctoreditor;

public enum TemporaryFileType{
	ORIGIN(""),
	INTERNAL_PREVIEW("internal_"),
	EXTERNAL_PREVIEW("preview_")
	;
	private String prefix;
	
	private TemporaryFileType(String prefix){
		this.prefix=prefix;
	}
	
	public String getPrefix() {
		return prefix;
	}
}