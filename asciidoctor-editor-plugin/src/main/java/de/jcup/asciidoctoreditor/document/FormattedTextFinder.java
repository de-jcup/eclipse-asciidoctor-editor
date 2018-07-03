package de.jcup.asciidoctoreditor.document;

public class FormattedTextFinder {

	private char[] starting;
	private char[] ending;
	
	private static boolean TRACE_MODE = Boolean.getBoolean("de.jcup.trace.formattedtextfinder") || true;
	
	public FormattedTextFinder(String start, String end) {
		this.starting=start.toCharArray();
		this.ending=end.toCharArray();
	}

	public boolean isFound(PlainJavaCharacterScanner adapter) {
		if (TRACE_MODE){
			adapter.startTracing();
		}
		boolean isFound = internalIsFormattedText(adapter);
		if (!isFound){
			adapter.rewind();
		}
		return isFound;
	}
	
	private boolean internalIsFormattedText(PlainJavaCharacterScanner scanner) {
		if (scanner.getColumn()>0){
			/* Check data before */
			scanner.unread();
			int before = scanner.read();
			boolean acceptableBefore = isSpace(before) || isLineBreak(before, scanner);
			if (!acceptableBefore) {
				return false;
			}
		}
		while (true){
			int read = scanner.read();
			
			if (isEOF(read)){
				return false;
			}
			if (isLineBreak(read, scanner)){
				return false;
			}
			
			for (char s: starting){
				if (s!=read){
					return false;
				}
				read= scanner.read();
			}
			if (isSpace(read)){
				return false;
			}
			int index =0;
			int lastCharBeforeEnding=' '; // just to init
			while (true){
				if (isTerminatingLine(read)){
					return false;
				}
				/* check if the current ending part is same as read one */
				char current = (char) read;
				if (ending[index]==current){
					
					index++;
					
					if (index==ending.length){
						
						/* okay ending found and is correct - check if we got no space*/
						if (isSpace(lastCharBeforeEnding)){
							return false;
						}
						read=scanner.read();
						if (isEOF(read)){
							return true;
						}
						if (isTerminatingLine(read) || isSpace(read)){
							/* remove the line terminator for token */
							scanner.unread();
							return true;
						}
						/* no space at the end - so not end sequence */
						return false;
					}
					
				}else{
					lastCharBeforeEnding=read;
					index=0;
				}
				read = scanner.read();
			}
			
		}
	
		
	}

	protected boolean isTerminatingLine(int read) {
		return isEOF(read) || isEOL(read);
	}

	private boolean isEOL(int read) {
		return read == '\n' || read =='\r';
	}

	protected boolean isEOF(int read) {
		return read ==PlainJavaCharacterScanner.EOF;
	}

	protected boolean isSpace(int read) {
		return read==32 || read==255;
	}

	private boolean isLineBreak(int read, PlainJavaCharacterScanner adapter) {
		if (read=='\r' || read =='\n'){
			/* keep it simple, we just want to know if this is a line break - \r\n is handled.. thats enough */
			return true;
		}
		return false;
	}

}
