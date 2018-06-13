/*
 * Copyright 2017 Albert Tregnaghi
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

/**
* 
* PlantUMLTypeDocumentKeywords is a generated java class. Please look into PlantUMLKeywordsGenerator.java
*
*/
public enum PlantUMLTypeDocumentKeywords implements DocumentKeyWord {

	
          ABSTRACT("abstract"),
          ACTOR("actor"),
          AGENT("agent"),
          ARTIFACT("artifact"),
          BOUNDARY("boundary"),
          CARD("card"),
          CLASS("class"),
          CLOUD("cloud"),
          COMPONENT("component"),
          CONTROL("control"),
          DATABASE("database"),
          ENTITY("entity"),
          ENUM("enum"),
          FOLDER("folder"),
          FRAME("frame"),
          INTERFACE("interface"),
          NODE("node"),
          OBJECT("object"),
          PARTICIPANT("participant"),
          RECT("rect"),
          STATE("state"),
          STORAGE("storage"),
          USECASE("usecase"),
	;

	private String text;

	private PlantUMLTypeDocumentKeywords(String text) {
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
		return "This is a keyword representing a 'type' in plantuml. Please refer to online documentation for more information";
	}

	@Override
	public String getLinkToDocumentation() {
		return "http://plantuml.com";
	}

}
