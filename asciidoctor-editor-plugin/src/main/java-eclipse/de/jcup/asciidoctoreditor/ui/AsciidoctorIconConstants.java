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
package de.jcup.asciidoctoreditor.ui;

public class AsciidoctorIconConstants {

    public static final String PATH_ICON_ASCIIDOCTOR_EDITOR = commonPath("asciidoctor-editor.png");
    public static final String PATH_OUTLINE_ICON_HEADLINE = outlinePath("headline.gif");
    public static final String PATH_OUTLINE_ICON_INCLUDE = outlinePath("gotoobj_tsk.png");
    public static final String PATH_OUTLINE_ICON_ERROR = outlinePath("error_tsk.png");
    public static final String PATH_OUTLINE_ICON_INFO = outlinePath("info_tsk.png");
    public static final String PATH_OUTLINE_ICON_INLINE_ANCHOR = outlinePath("inline_anchor.gif");
    public static final String PATH_OUTLINE_ICON_IMAGE = outlinePath("image.gif");
    public static final String PATH_OUTLINE_ICON_DITAA = commonPath("ditaa-asciidoctor-editor.png");
    public static final String PATH_OUTLINE_ICON_PLANTUML = commonPath("plantuml-asciidoctor-editor.png");

    static String outlinePath(String name) {
        return "icons/outline/" + name;
    }

    private static String commonPath(String name) {
        return "icons/" + name;
    }

}
