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
package de.jcup.asciidoctoreditor.script.parser;

public class HereStringContext {

	CodePosSupport codePosSupport;
	StringBuilder content;
	StringBuilder partScan;

	private Character lastCharacter;
	int hereStringTokenStart=-1;
	int hereStringPos;
	int hereStringTokenEnd;
	Character stringIdentifier;
	private Character lastCharacterBefore;
	boolean firstCharExceptWhitespacesCheck;
	
	public HereStringContext(CodePosSupport codePosSupport){
		if (codePosSupport==null){
			throw new IllegalArgumentException("codePosSupport may not be null!");
		}
		this.codePosSupport=codePosSupport;
		this.partScan=new StringBuilder();
		this.content=new StringBuilder();
	}
	
	public void moveToNewEndPosition(int newPos) {
		codePosSupport.moveToPos(newPos);
	}

	public int getHereStringPos() {
		return codePosSupport.getInitialStartPos();
	}

	public Character getCharacterAtPosOrNull(int pos) {
		if (codePosSupport==null){
			return null;
		}
		return codePosSupport.getCharacterAtPosOrNull(pos);
	}
	
	public String getContent() {
		if (content.length()==0){
			return "";
		}
		char lastContentChar = content.charAt(content.length() - 1);

		if (Character.isWhitespace(lastContentChar)) {
			/* remove last whitespace */
			int contentLength = content.length() - 1;
			return content.substring(0, contentLength);
		} else {
			return content.toString();
		}
	}

	public boolean isNoHereStringFound() {
		return hereStringTokenStart == -1;
	}

	public void setLastCharacter(Character ca) {
		this.lastCharacterBefore = this.lastCharacter;
		this.lastCharacter=ca;
	}
	
	public Character getLastCharacter() {
		return lastCharacter;
	}
	
	public Character getLastCharacterBefore() {
		return lastCharacterBefore;
	}

	public boolean isEscaped() {
		if (lastCharacterBefore==null){
			return false;
		}
		return lastCharacterBefore.charValue()=='\\';
	}
	
	@Override
	public String toString() {
		return "HereStringContext: last:"+lastCharacter+",before:"+lastCharacterBefore+", partScan:"+partScan+", content="+content;
	}

}
