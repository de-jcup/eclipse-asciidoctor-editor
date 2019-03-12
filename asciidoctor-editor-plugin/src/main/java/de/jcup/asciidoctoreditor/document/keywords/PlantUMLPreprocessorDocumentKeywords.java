/*
 * Copyright 2019 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.document.keywords;

import de.jcup.eclipse.commons.keyword.DocumentKeyWord;

/**
* 
* PlantUMLPreprocessorDocumentKeywords is a generated java class. Please look into PlantUMLKeywordsGenerator.java
*
*/
public enum PlantUMLPreprocessorDocumentKeywords implements DocumentKeyWord {

	
          NOT_DEFINE("!define"),
          NOT_DEFINELONG("!definelong"),
          NOT_ELSE("!else"),
          NOT_ENDDEFINELONG("!enddefinelong"),
          NOT_ENDIF("!endif"),
          NOT_EXIT("!exit"),
          NOT_IF("!if"),
          NOT_IFDEF("!ifdef"),
          NOT_IFNDEF("!ifndef"),
          NOT_INCLUDE("!include"),
          NOT_PRAGMA("!pragma"),
          NOT_UNDEF("!undef"),
	;

	private String text;

	private PlantUMLPreprocessorDocumentKeywords(String text) {
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public boolean isBreakingOnEof() {
		return true;
	}


	@Override
	public String getTooltip() {
		return "This is a keyword representing a 'preprocessor' in plantuml. Please refer to online documentation for more information";
	}

	@Override
	public String getLinkToDocumentation() {
		return "http://plantuml.com";
	}

}
