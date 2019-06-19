package de.jcup.asciidoctoreditor.preferences;

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
 

/**
 * Constant definitions for plug-in preferences
 */
public enum AsciiDoctorEditorPreferenceConstants implements PreferenceIdentifiable{

	
	P_EDITOR_MATCHING_BRACKETS_ENABLED("matchingBrackets"),
	P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION("highlightBracketAtCaretLocation"),
	P_EDITOR_ENCLOSING_BRACKETS("enclosingBrackets"),
	P_EDITOR_MATCHING_BRACKETS_COLOR("matchingBracketsColor"),
	P_EDITOR_AUTO_CREATE_END_BRACKETSY("autoCreateEndBrackets"),
	
	P_USE_PREVIEW_IMAGEDIRECTORY("usePreviewImageDirectory"),
	P_LINK_OUTLINE_WITH_EDITOR("linkOutlineWithEditor"),
	P_LINK_EDITOR_WITH_PREVIEW("linkEditorWithPreview"),

	P_CODE_ASSIST_ADD_KEYWORDS("codeAssistAddsKeyWords"),
	P_CODE_ASSIST_ADD_SIMPLEWORDS("codeAssistAddsSimpleWords"),
	
	P_TOOLTIPS_ENABLED("toolTipsEnabled"),
	
	P_EDITOR_NEWEDITOR_PREVIEW_LAYOUT("newEditorPreviewLayout"),
	
	P_EDITOR_AUTOREFRESH_EXTERNAL_BROWSER_IN_SECONDS("autoRefreshExternalBrowserInSeconds"),
	
	P_EDITOR_TOC_LEVELS("tocLevels"),
	
	P_EDITOR_AUTOBUILD_FOR_EXTERNAL_PREVIEW_ENABLED("autoBuildForExternalPreviewEnabled"),
	
	P_USE_INSTALLED_ASCIIDOCTOR_ENABLED("useInstalledAsciidoctorEnabled"),
	
	P_INSTALLED_ASCIICDOCTOR_ARGUMENTS("installedAsciidoctorArguments"),

	P_PATH_TO_INSTALLED_ASCIICDOCTOR("pathToInstalledAsciidoctor"),
	
	P_SHOW_ASCIIDOC_CONSOLE_ON_ERROR_OUTPUT("showConsoleOnErrorOutput"),
	
	P_ASP_SERVER_PORT("aspServerPort"), 
	
	P_ASP_SERVER_LOGS_SHOWN_AS_MARKER_IN_EDITOR("aspLogsShownAsMarkerInEditor"),
	
	P_CODE_ASSIST_DYNAMIC_FOR_INCLUDES("dynamicIncludeCodeAssistEnabled"), 
	
	P_PATH_TO_JAVA_FOR_ASP_LAUNCH("pathToJavaForASPLaunch"),
	
	;

	private String id;

	private AsciiDoctorEditorPreferenceConstants(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
}
