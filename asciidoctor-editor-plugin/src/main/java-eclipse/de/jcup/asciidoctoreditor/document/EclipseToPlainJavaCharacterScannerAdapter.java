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

import org.eclipse.jface.text.rules.ICharacterScanner;

public class EclipseToPlainJavaCharacterScannerAdapter implements PlainJavaCharacterScanner{

	private ICharacterScanner delegate;
	private int rewindCounter;

	public EclipseToPlainJavaCharacterScannerAdapter(ICharacterScanner scanner){
		if (scanner==null){
			throw new IllegalArgumentException("scanner may not be null!");
		}
		this.delegate=scanner;
	}

	
	@Override
	public char[][] getLegalLineDelimiters() {
		return delegate.getLegalLineDelimiters();
	}

	@Override
	public int getColumn() {
		return delegate.getColumn();
	}

	@Override
	public int read() {
		int read = delegate.read();
		if (read != ICharacterScanner.EOF){
			rewindCounter++;
		}
		return read;
	}

	@Override
	public void unread() {
		moveback();
	}


	/**
	 * Rewind the underlying character scanner to its former position
	 */
	public void rewind(){
		while (rewindCounter>0){
			moveback();
		}
	}
	
	protected void moveback() {
		rewindCounter--;
		delegate.unread();
	}


	@Override
	public void startTracing() {
		/* not implemented for this variant */
	}
}
