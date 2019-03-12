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
* PlantUMLKeywordDocumentKeywords is a generated java class. Please look into PlantUMLKeywordsGenerator.java
*
*/
public enum PlantUMLKeywordDocumentKeywords implements DocumentKeyWord {

	
          ATSIGN_ENDDOT("@enddot"),
          ATSIGN_ENDSALT("@endsalt"),
          ATSIGN_ENDUML("@enduml"),
          ATSIGN_STARTDOT("@startdot"),
          ATSIGN_STARTSALT("@startsalt"),
          ATSIGN_STARTUML("@startuml"),
          ACTIVATE("activate"),
          AGAIN("again"),
          ALLOW_MIXING("allow_mixing"),
          ALLOWMIXING("allowmixing"),
          ALSO("also"),
          ALT("alt"),
          AS("as"),
          AUTONUMBER("autonumber"),
          BOTTOM("bottom"),
          BOX("box"),
          BREAK("break"),
          CAPTION("caption"),
          CENTER("center"),
          CREATE("create"),
          CRITICAL("critical"),
          DEACTIVATE("deactivate"),
          DESTROY("destroy"),
          DOWN("down"),
          ELSE("else"),
          ELSEIF("elseif"),
          END("end"),
          ENDIF("endif"),
          ENDWHILE("endwhile"),
          FOOTBOX("footbox"),
          FOOTER("footer"),
          FORK("fork"),
          GROUP("group"),
          HEADER("header"),
          HIDE("hide"),
          HNOTE("hnote"),
          IF("if"),
          IS("is"),
          KILL("kill"),
          LEFT("left"),
          LEGEND("legend"),
          LINK("link"),
          LOOP("loop"),
          MAINFRAME("mainframe"),
          NAMESPACE("namespace"),
          NEWPAGE("newpage"),
          NOTE("note"),
          OF("of"),
          ON("on"),
          OPT("opt"),
          ORDER("order"),
          OVER("over"),
          PACKAGE("package"),
          PAGE("page"),
          PAR("par"),
          PARTITION("partition"),
          REF("ref"),
          REPEAT("repeat"),
          RETURN("return"),
          RIGHT("right"),
          RNOTE("rnote"),
          ROTATE("rotate"),
          SHOW("show"),
          SKIN("skin"),
          SKINPARAM("skinparam"),
          START("start"),
          STOP("stop"),
          TITLE("title"),
          TOP("top"),
          TOP_TO_BOTTOM_DIRECTION("top to bottom direction"),
          UP("up"),
          WHILE("while"),
	;

	private String text;

	private PlantUMLKeywordDocumentKeywords(String text) {
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
		return "This is a keyword representing a 'keyword' in plantuml. Please refer to online documentation for more information";
	}

	@Override
	public String getLinkToDocumentation() {
		return "http://plantuml.com";
	}

}
