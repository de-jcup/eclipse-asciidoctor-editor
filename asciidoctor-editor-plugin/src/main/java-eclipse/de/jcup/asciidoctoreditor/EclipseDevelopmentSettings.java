package de.jcup.asciidoctoreditor;

public interface EclipseDevelopmentSettings {
	/**
	 * Debug feature toggle - if enabled html prefix is always rebuild. Interesting when changing css files etc.
	 * In Production we create the HTML prefix only ONE time (so reducing unnecessary file IO)
	 */
	public static final boolean DEBUG_RELOAD_HTML_PREFIX = Boolean
			.parseBoolean(System.getProperty("asciidoctor.editor.debug.reload.htmlprefix"));

}