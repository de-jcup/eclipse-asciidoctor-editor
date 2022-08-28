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

import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferenceConstants.*;
import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorSyntaxColorPreferenceConstants.*;
import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorValidationPreferenceConstants.*;
import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorPlantUMLEditorSyntaxColorPreferenceConstants.*;
import static de.jcup.asciidoctoreditor.ui.AsciiDoctorEditorColorConstants.*;
import static de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil.*;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.jcup.asciidoctoreditor.PreviewLayout;
import de.jcup.asciidoctoreditor.asciidoc.ASPServerAdapter;
import de.jcup.asciidoctoreditor.script.parser.validator.AsciiDoctorEditorValidationErrorLevel;
import de.jcup.asciidoctoreditor.toolbar.ZoomLevel;

/**
 * Class used to initialize default preference values.
 */
public class AsciiDoctorEditorPreferenceInitializer extends AbstractPreferenceInitializer {

    public void initializeDefaultPreferences() {
        AsciiDoctorEditorPreferences preferences = getPreferences();
        IPreferenceStore store = preferences.getPreferenceStore();

        /* Outline */
        store.setDefault(P_LINK_OUTLINE_WITH_EDITOR.getId(), true);

        /* Preview */
        store.setDefault(P_LINK_EDITOR_WITH_PREVIEW.getId(), true);

        /* ++++++++++++ */
        /* + Brackets + */
        /* ++++++++++++ */
        /* bracket rendering configuration */
        store.setDefault(P_EDITOR_MATCHING_BRACKETS_ENABLED.getId(), true);
        store.setDefault(P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION.getId(), false);
        store.setDefault(P_EDITOR_ENCLOSING_BRACKETS.getId(), false);
        store.setDefault(P_EDITOR_AUTO_CREATE_END_BRACKETSY.getId(), true);

        /* bracket color */
        preferences.setDefaultColor(P_EDITOR_MATCHING_BRACKETS_COLOR, GRAY_JAVA);

        /* +++++++++++++++ */
        /* + Code assist + */
        /* +++++++++++++++ */
        store.setDefault(P_CODE_ASSIST_ADD_KEYWORDS.getId(), true);
        store.setDefault(P_CODE_ASSIST_ADD_SIMPLEWORDS.getId(), true);

        store.setDefault(P_CODE_ASSIST_DYNAMIC_FOR_INCLUDES.getId(), true);
        store.setDefault(P_CODE_ASSIST_DYNAMIC_FOR_IMAGES.getId(), true);
        store.setDefault(P_CODE_ASSIST_DYNAMIC_FOR_DITAA_MACRO.getId(), true);
        store.setDefault(P_CODE_ASSIST_DYNAMIC_FOR_PLANTUML_MACRO.getId(), true);

        store.setDefault(P_AUTOCREATE_INITIAL_CONFIGFILE.getId(), true);

        /* ++++++++++++ */
        /* + Tooltips + */
        /* ++++++++++++ */
        store.setDefault(P_TOOLTIPS_ENABLED.getId(), true);

        /* +++++++++++ */
        /* + Preview + */
        /* +++++++++++ */
        store.setDefault(P_EDITOR_NEWEDITOR_PREVIEW_LAYOUT.getId(), PreviewLayout.VERTICAL.getId());
        store.setDefault(P_EDITOR_TOC_LEVELS.getId(), 6);
        store.setDefault(P_EDITOR_AUTOBUILD_FOR_EXTERNAL_PREVIEW_REFRESH_IN_SECONDS.getId(), 3);
        store.setDefault(P_EDITOR_AUTOBUILD_FOR_EXTERNAL_PREVIEW_ENABLED.getId(), false);// per default auto refresh is turned off
        store.setDefault(P_USE_INSTALLED_ASCIIDOCTOR_ENABLED.getId(), false);
        store.setDefault(P_INSTALLED_ASCIICDOCTOR_ARGUMENTS.getId(), "-r asciidoctor-diagram\n--no-header-footer\n");
        store.setDefault(P_SHOW_ASCIIDOC_CONSOLE_ON_ERROR_OUTPUT.getId(), true);

        /* ++++++++++++++ */
        /* + ASP server + */
        /* ++++++++++++++ */
        store.setDefault(P_ASP_SERVER_MIN_PORT.getId(), ASPServerAdapter.DEFAULT_MIN_PORT);
        store.setDefault(P_ASP_SERVER_MAX_PORT.getId(), ASPServerAdapter.DEFAULT_MAX_PORT);
        store.setDefault(P_ASP_SERVER_LOGS_SHOWN_AS_MARKER_IN_EDITOR.getId(), true);
        store.setDefault(P_ASP_SERVER_OUTPUT_SHOWN_IN_CONSOLE.getId(), false);
        store.setDefault(P_ASP_COMMUNICATION_SHOWN_IN_CONSOLE.getId(), false);

        /* +++++++++++++++++ */
        /* + Editor Colors + */
        /* +++++++++++++++++ */
        preferences.setDefaultColor(COLOR_NORMAL_TEXT, BLACK);
        preferences.setDefaultColor(COLOR_PLANTUML_NORMAL_TEXT, BLACK);

        preferences.setDefaultColor(COLOR_ASCIIDOCTOR_HEADLINES, ASCIIDOC_HEADLINE_HTML);

        preferences.setDefaultColor(COLOR_TEXT_BLOCKS, CADET_BLUE);
        preferences.setDefaultColor(COLOR_COMMENT, GREEN_JAVA);

        preferences.setDefaultColor(COLOR_ASCIIDOCTOR_COMMAND, MIDDLE_BLUE);
        preferences.setDefaultColor(COLOR_KNOWN_VARIABLES, DARK_GRAY);
        preferences.setDefaultColor(COLOR_TEXT_BOLD, BLACK);
        preferences.setDefaultColor(COLOR_TEXT_ITALIC, BLACK);

        preferences.setDefaultColor(COLOR_DELIMITERS, LIGHT_THEME_LIGHT_BLUE);

        preferences.setDefaultColor(COLOR_PLANTUML_KEYWORD, KEYWORD_DEFAULT_PURPLE);
        preferences.setDefaultColor(COLOR_PLANTUML_NOTE, CADET_BLUE);
        preferences.setDefaultColor(COLOR_PLANTUML_COMMENT, GREEN_JAVA);
        preferences.setDefaultColor(COLOR_PLANTUML_PREPROCESSOR, DARK_GRAY);
        preferences.setDefaultColor(COLOR_PLANTUML_SKINPARAMETER, DARK_GRAY);
        preferences.setDefaultColor(COLOR_PLANTUML_DIVIDER, DARK_GREEN);
        preferences.setDefaultColor(COLOR_PLANTUML_ARROW, DARK_GREEN);
        preferences.setDefaultColor(COLOR_PLANTUML_LABEL, BRIGHT_RED);
        preferences.setDefaultColor(COLOR_PLANTUML_TYPE, KEYWORD_DEFAULT_PURPLE);
        preferences.setDefaultColor(COLOR_PLANTUML_COLOR, DARK_BLUE);
        preferences.setDefaultColor(COLOR_PLANTUML_DOUBLESTRING, ROYALBLUE);

        /* ++++++++++++++ */
        /* + Validation + */
        /* ++++++++++++++ */
        store.setDefault(VALIDATE_ERROR_LEVEL.getId(), AsciiDoctorEditorValidationErrorLevel.WARNING.getId());
        store.setDefault(VALIDATE_INCLUDES.getId(), true);
        store.setDefault(VALIDATE_IMAGES.getId(), true);
        store.setDefault(VALIDATE_DIAGRAMS.getId(), true);
        store.setDefault(VALIDATE_URLS.getId(), true);

        store.setDefault(P_OUTLINE_GROUPING_ENABLED_PER_DEFAULT.getId(), true);

        /* ++++++++++++ */
        /* + PlantUML +*/
        /* ++++++++++++ */
        store.setDefault(AsciiDoctorPlantUMLEditorPreferenceConstants.P_DEFAULT_ZOOM_LEVEL.getId(), ZoomLevel.LEVEL_100_PERCENT_TEXT);
        
        /* +++++++++++++++++++ */
        /* + Temporary files +*/
        /* +++++++++++++++++++ */
        store.setDefault(AsciiDoctorEditorPreferenceConstants.P_DAYS_TO_KEEP_TEMPFILES.getId(), 2);
    }

}
