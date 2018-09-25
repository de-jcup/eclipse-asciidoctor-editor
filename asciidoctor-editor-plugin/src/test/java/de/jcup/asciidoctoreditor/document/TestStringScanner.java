/*
 * Copyright 2018 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
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
