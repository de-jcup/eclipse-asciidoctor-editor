package de.jcup.asciidoctoreditor;

public interface EclipseDevelopmentSettings {
	
	public static final boolean DEBUG_TOOLBAR_ENABLED = Boolean
			.parseBoolean(System.getProperty("asciidoctor.editor.debug.toolbar"));

}