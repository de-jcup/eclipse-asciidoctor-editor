package de.jcup.asciidoctoreditor.document;

import de.jcup.asciidoctoreditor.EndlessLoopPreventer;

public class FormattedTextFinder {

	private static final int MAX_LOOPS = 10000;
	private char[] starting;
	private char[] ending;

	private static boolean TRACE_MODE = Boolean.getBoolean("de.jcup.trace.formattedtextfinder");

	public FormattedTextFinder(String start, String end) {
		this.starting = start.toCharArray();
		this.ending = end.toCharArray();
	}

	public boolean isFound(PlainJavaCharacterScanner adapter) {
		if (TRACE_MODE) {
			adapter.startTracing();
		}
		boolean isFound = find(adapter);
		if (!isFound) {
			adapter.rewind();
		}
		return isFound;
	}

	private boolean find(PlainJavaCharacterScanner scanner) {
		/* if not first column check content before is valid for start */
		if (scanner.getColumn() > 0) {
			/* Check data before */
			scanner.unread();
			int before = scanner.read();
			boolean acceptableBefore = isSpace(before) || isLineBreak(before, scanner);
			if (!acceptableBefore) {
				return false;
			}
		}
		return findFromValidTextBefore(scanner);

	}

	private boolean findFromValidTextBefore(PlainJavaCharacterScanner scanner) {
		int read = scanner.read();

		if (isEOF(read) || isLineBreak(read,
				scanner)) { /* file/line end reached - so not found */
			return false;
		}
		/*
		 * iterate over next chars - they must start with star sequence check
		 * for beginning sequence
		 */
		for (char s : starting) {
			if (s != read) {
				return false;
			}
			read = scanner.read();
		}
		if (isSpace(read)) {
			return false;
		}
		return findFromValidStartSequence(scanner, read);
	}

	private boolean findFromValidStartSequence(PlainJavaCharacterScanner scanner, int read) {
		/*
		 * start sequence is found and valid here. Now iterate over next
		 * characters and try to find a valid end sequence.
		 */
		int index = 0;
		int lastCharBeforeEnding = ' '; // just to init

		EndlessLoopPreventer endlessLoopPreventer = new EndlessLoopPreventer(MAX_LOOPS);
		
		while (true) {
			/* while(true)... we must ensure plugin does never freeze eclipse */
			endlessLoopPreventer.assertNoEndlessLoop();

			if (isTerminatingLine(read)) {
				return false;
			}
			/* check if the current ending part is same as read one */
			char current = (char) read;
			if (ending[index] == current) {
				index++;

				if (index == ending.length) {
					/* okay ending found */
					return isValidEndsequence(scanner, lastCharBeforeEnding);
				}

			} else {
				/* other character not endsequence, reset lookup*/
				lastCharBeforeEnding = read;
				index = 0;
			}
			read = scanner.read();
		}
	}

	private boolean isValidEndsequence(PlainJavaCharacterScanner scanner, int lastCharBeforeEnding) {
		/* check if we got no space etc - means is a valid end sequence */
		int read;
		if (isSpace(lastCharBeforeEnding)) {
			return false;
		}
		read = scanner.read();
		if (isEOF(read)) {
			/*
			 * no space after end sequence possible because EOF, no scanner
			 * unread necessary - we are still on end
			 */
			return true;
		}
		if (isTerminatingLine(read) || isSpace(read)) {
			/* remove the line terminator for token */
			scanner.unread();
			return true;
		}
		/* no space at the end - so not valid end sequence */
		return false;
	}

	private boolean isTerminatingLine(int read) {
		return isEOF(read) || isEOL(read);
	}

	private boolean isEOL(int read) {
		return read == '\n' || read == '\r';
	}

	private boolean isEOF(int read) {
		return read == PlainJavaCharacterScanner.EOF;
	}

	private boolean isSpace(int read) {
		return read == 32 || read == 255;
	}

	private boolean isLineBreak(int read, PlainJavaCharacterScanner adapter) {
		if (read == '\r' || read == '\n') {
			/*
			 * keep it simple, we just want to know if this is a line break -
			 * \r\n is handled.. thats enough
			 */
			return true;
		}
		return false;
	}

}
