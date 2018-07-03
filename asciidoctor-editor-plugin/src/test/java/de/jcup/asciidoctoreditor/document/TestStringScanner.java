package de.jcup.asciidoctoreditor.document;

public class TestStringScanner implements PlainJavaCharacterScanner{

	private char[][] lineDelimters = new char[][]{new char[]{'\n'}};
	int pos;
	private char[] text;
	private boolean traceEnabled;
	
	public TestStringScanner(String text){
		this.text=text.toCharArray();
	}
	
	@Override
	public char[][] getLegalLineDelimiters() {
		return lineDelimters;
	}

	@Override
	public int getColumn() {
		return pos;
	}

	@Override
	public int read() {
		if (traceEnabled){
			System.out.print("read [\t\t"+pos+"]:");
		}
		if (pos>=text.length){
			if (traceEnabled){
				System.out.println("EOF");
			}
			return EOF;
		}
		char value = text[pos++];
		if (traceEnabled){
			System.out.println(value);
		}
		return value;
	}

	@Override
	public void unread() {
		pos--;// we do not handle wrong position here
		if (traceEnabled){
			System.out.print("unread [\t\t"+pos+"]");
		}
	}

	@Override
	public void rewind() {
		if (traceEnabled){
			System.out.print("start rewind ["+pos+"]");
		}
		while (pos>0){
			unread();
		}
		
	}

	@Override
	public void startTracing() {
		traceEnabled=true;
	}
	
	

}
