package de.jcup.asciidoctoreditor.document;

public interface PlainJavaCharacterScanner {

	/**
	 * The value returned when this scanner has read EOF.
	 */
	public static final int EOF= -1;

	/**
	 * Provides rules access to the legal line delimiters. The returned
	 * object may not be modified by clients.
	 *
	 * @return the legal line delimiters
	 */
	char[][] getLegalLineDelimiters();

	/**
	 * Returns the column of the character scanner.
	 *
	 * @return the column of the character scanner
	 */
	int getColumn();

	/**
	 * Returns the next character or EOF if end of file has been reached
	 *
	 * @return the next character or EOF
	 */
	int read();

	/**
	 * Rewinds the scanner before the last read character.
	 */
	void unread();

	void rewind();

	void startTracing();
}
