package de.jcup.asciidoctoreditor.provider;

public enum ImageHandlingMode{
	/**
	 * Transform attribute imagesdir, copy images and define also outputdir (for diagram generation )to target image dir in temp project
	 */
	IMAGESDIR_FROM_PREVIEW_DIRECTORY,

	/** Special variant for project having no 'imagesdir' set, but using relative path instead */
	RELATIVE_PATHES,
	
	/** Variant to keep generated diagram files - will be used by plantuml editor */
	STORE_DIAGRAM_FILES_LOCAL,
	;
	
}