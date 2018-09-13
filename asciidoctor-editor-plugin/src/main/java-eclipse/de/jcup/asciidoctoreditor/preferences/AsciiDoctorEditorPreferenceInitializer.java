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

import static de.jcup.asciidoctoreditor.AsciiDoctorEditorColorConstants.*;
import static de.jcup.asciidoctoreditor.AsciiDoctorEditorUtil.*;
import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferenceConstants.*;
import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorSyntaxColorPreferenceConstants.*;
import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorPlantUMLEditorSyntaxColorPreferenceConstants.*;
import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorValidationPreferenceConstants.*;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.jcup.asciidoctoreditor.PreviewLayout;
import de.jcup.asciidoctoreditor.script.parser.validator.AsciiDoctorEditorValidationErrorLevel;

/**
 * Class used to initialize default preference values.
 */
public class AsciiDoctorEditorPreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		AsciiDoctorEditorPreferences preferences = getPreferences();
		IPreferenceStore store = preferences.getPreferenceStore();

		/* Outline */
		store.setDefault(P_LINK_OUTLINE_WITH_EDITOR.getId(), true);

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

		store.setDefault(P_CODE_ASSIST_ADD_KEYWORDS.getId(), true);
		store.setDefault(P_CODE_ASSIST_ADD_SIMPLEWORDS.getId(), true);

		/* ++++++++++++ */
		/* + Tooltips + */
		/* ++++++++++++ */
		store.setDefault(P_TOOLTIPS_ENABLED.getId(), true);

		/* +++++++++++ */
		/* + Preview + */
		/* +++++++++++ */
		store.setDefault(P_EDITOR_NEWEDITOR_PREVIEW_LAYOUT.getId(), PreviewLayout.VERTICAL.getId());
		store.setDefault(P_EDITOR_TOC_LEVELS.getId(), 6);
		store.setDefault(P_EDITOR_AUTOREFRESH_EXTERNAL_BROWSER_IN_SECONDS.getId(), 0); // per default auto refresh is turned off
		store.setDefault(P_EDITOR_AUTOBUILD_FOR_EXTERNAL_PREVIEW_ENABLED.getId(), false);
		
		/* +++++++++++++++++ */
		/* + Editor Colors + */
		/* +++++++++++++++++ */
		preferences.setDefaultColor(COLOR_NORMAL_TEXT, BLACK);
		preferences.setDefaultColor(COLOR_PLANTUML_NORMAL_TEXT, BLACK);

		preferences.setDefaultColor(COLOR_ASCIIDOCTOR_HEADLINES, ASCIIDOC_HEADLINE_HTML);

		preferences.setDefaultColor(COLOR_TEXT_BLOCKS, CADET_BLUE);
		preferences.setDefaultColor(COLOR_COMMENT, GREEN_JAVA);

		preferences.setDefaultColor(COLOR_INCLUDE_KEYWORD, LINK_DEFAULT_BLUE);

		preferences.setDefaultColor(COLOR_ASCIIDOCTOR_COMMAND, MIDDLE_BLUE);
		preferences.setDefaultColor(COLOR_KNOWN_VARIABLES, DARK_GRAY);
		preferences.setDefaultColor(COLOR_TEXT_BOLD, BLACK);
		preferences.setDefaultColor(COLOR_HYPERLINK, DARK_BLUE);
		preferences.setDefaultColor(COLOR_TEXT_ITALIC, BLACK);

		preferences.setDefaultColor(COLOR_PLANTUML_KEYWORD, KEYWORD_DEFAULT_PURPLE);
		preferences.setDefaultColor(COLOR_PLANTUML_NOTE, CADET_BLUE);
		preferences.setDefaultColor(COLOR_PLANTUML_COMMENT, GREEN_JAVA);
		preferences.setDefaultColor(COLOR_PLANTUML_PREPROCESSOR, DARK_GRAY);
		preferences.setDefaultColor(COLOR_PLANTUML_SKINPARAMETER, DARK_GRAY);
		preferences.setDefaultColor(COLOR_PLANTUML_TYPE, KEYWORD_DEFAULT_PURPLE);
		preferences.setDefaultColor(COLOR_PLANTUML_COLOR, DARK_BLUE);
		preferences.setDefaultColor(COLOR_PLANTUML_DOUBLESTRING, ROYALBLUE);

		/* ++++++++++++++ */
		/* + Validation + */
		/* ++++++++++++++ */
		store.setDefault(VALIDATE_GRAPHVIZ.getId(), true);

		store.setDefault(VALIDATE_ERROR_LEVEL.getId(), AsciiDoctorEditorValidationErrorLevel.WARNING.getId());
	}

}
