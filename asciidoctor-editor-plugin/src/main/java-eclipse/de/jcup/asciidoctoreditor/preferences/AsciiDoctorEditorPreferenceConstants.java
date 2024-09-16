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
public enum AsciiDoctorEditorPreferenceConstants implements PreferenceIdentifiable {

    P_EDITOR_MATCHING_BRACKETS_ENABLED("matchingBrackets"),

    P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION("highlightBracketAtCaretLocation"),

    P_EDITOR_ENCLOSING_BRACKETS("enclosingBrackets"),

    P_EDITOR_MATCHING_BRACKETS_COLOR("matchingBracketsColor"),

    P_EDITOR_AUTO_CREATE_END_BRACKETSY("autoCreateEndBrackets"),

    P_LINK_OUTLINE_WITH_EDITOR("linkOutlineWithEditor"),

    P_LINK_EDITOR_WITH_PREVIEW("linkEditorWithPreview"),
    
    P_LINK_BETWEEN_EDITOR_AND_PREVIEW_USES_TEXT_SELECTION_AS_FALLBACK("linkEditorAndPreviewUsesTextSelectionAsFallback"),
    
    P_CODE_ASSIST_ADD_KEYWORDS("codeAssistAddsKeyWords"),

    P_CODE_ASSIST_ADD_SIMPLEWORDS("codeAssistAddsSimpleWords"),

    P_TOOLTIPS_ENABLED("toolTipsEnabled"),

    P_EDITOR_NEWEDITOR_PREVIEW_LAYOUT("newEditorPreviewLayout"),

    P_EDITOR_EXTERNAL_PREVIEW_AUTOREFRESH_IN_SECONDS("externalPreviewAutorefreshInSeconds"),
    
    P_EDITOR_EXTERNAL_PREVIEW_AUTOREFRESH_ENABLED("externalPreviewAutorefresEnabled"),

    P_EDITOR_AUTOBUILD_FOR_EXTERNAL_PREVIEW_ENABLED("autoBuildForExternalPreviewEnabled"),

    P_EDITOR_TOC_LEVELS("tocLevels"),

    P_USE_INSTALLED_ASCIIDOCTOR_ENABLED("useInstalledAsciidoctorEnabled"),

    P_INSTALLED_ASCIICDOCTOR_ARGUMENTS("installedAsciidoctorArguments"),

    P_PATH_TO_INSTALLED_ASCIICDOCTOR("pathToInstalledAsciidoctor"),

    P_SHOW_ASCIIDOC_CONSOLE_ON_ERROR_OUTPUT("showConsoleOnErrorOutput"),
    

    P_ASP_SERVER_MIN_PORT("aspServerMinPort"),

    P_ASP_SERVER_MAX_PORT("aspServerMaxPort"),

    P_ASP_SERVER_LOGS_SHOWN_AS_MARKER_IN_EDITOR("aspLogsShownAsMarkerInEditor"),

    P_ASP_SERVER_OUTPUT_SHOWN_IN_CONSOLE("aspServerOutputShownInConsole"),

    P_ASP_COMMUNICATION_SHOWN_IN_CONSOLE("aspCommunicationShownInConsole"),

    P_CODE_ASSIST_DYNAMIC_FOR_INCLUDES("dynamicIncludeCodeAssistEnabled"),

    P_CODE_ASSIST_DYNAMIC_FOR_IMAGES("dynamicImageCodeAssistEnabled"),

    P_CODE_ASSIST_DYNAMIC_FOR_PLANTUML_MACRO("dynamicPlantumlMacroCodeAssistEnabled"),

    P_CODE_ASSIST_DYNAMIC_FOR_DITAA_MACRO("dynamicDitaaMacroCodeAssistEnabled"),

    P_PATH_TO_JAVA_BINARY_FOR_ASP_LAUNCH("pathToJavaBinaryForASPLaunch"),

    P_OUTLINE_GROUPING_ENABLED_PER_DEFAULT("outlineGroupingEnabledPerDefault"),

    P_AUTOCREATE_INITIAL_CONFIGFILE("autoCreateInitialAsciidocConfigFile"),

    P_DAYS_TO_KEEP_TEMPFILES("daysToKeepTempFiles"), 
    
    P_CUSTOM_ENV_ENTRIES_DATA("customEnviromentEntriesData"),
    
    P_CUSTOM_ENV_ENTRIES_ENABLED("customEnviromentEntriesEnabled"),
    
    P_CUSTOM_ATTRIBUTES_DATA("customAttributesData"),
    
    P_CUSTOM_ATTRIBUTES_ENABLED("customAttributesEnabled"), 
    
    P_TOC_VISIBLE_ON_NEW_EDITORS_PER_DEFAULT("tocVisibleOnNewEditorsPerDefault"),
    

    ;

    private String id;

    private AsciiDoctorEditorPreferenceConstants(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
