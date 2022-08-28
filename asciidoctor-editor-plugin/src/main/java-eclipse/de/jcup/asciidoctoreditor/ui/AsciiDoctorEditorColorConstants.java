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
package de.jcup.asciidoctoreditor.ui;

import org.eclipse.swt.graphics.RGB;

public interface AsciiDoctorEditorColorConstants {

    public static final RGB GRAY_JAVA = rgb(192, 192, 192);// grey as in java

    public static final RGB GREEN_JAVA = rgb(63, 127, 95); // green
                                                           // as
                                                           // java
                                                           // eclipse
                                                           // default
                                                           // for
                                                           // comments
    public static final RGB LINK_DEFAULT_BLUE = rgb(63, 63, 191); // blue as
                                                                  // java
                                                                  // eclipse
                                                                  // default
                                                                  // for
                                                                  // links
    public static final RGB KEYWORD_DEFAULT_PURPLE = rgb(127, 0, 85); // purple
                                                                      // as
                                                                      // java
                                                                      // eclipse
                                                                      // default
                                                                      // for
                                                                      // return
    /*
     * same as java default string in eclipse
     */
    public static final RGB STRING_DEFAULT_BLUE = rgb(42, 0, 255);
    /* royal blue - http://www.rapidtables.com/web/color/blue-color.htm */
    public static final RGB ROYALBLUE = rgb(65, 105, 225);
    /* steel blue - http://www.rapidtables.com/web/color/blue-color.htm */
    public static final RGB STEELBLUE = rgb(70, 130, 180);

    /* cadetblue - http://www.rapidtables.com/web/color/blue-color.htm */
    public static final RGB CADET_BLUE = rgb(95, 158, 160);

    public static final RGB MIDDLE_BLUE = rgb(0, 128, 255);

    public static final RGB OUTLINE_ITEM__TYPE = rgb(149, 125, 71); // same
                                                                    // as
                                                                    // java
                                                                    // outline
                                                                    // string
    public static final RGB MIDDLE_GRAY = rgb(128, 128, 128);
    public static final RGB MIDDLE_GREEN = rgb(0, 128, 0);
    public static final RGB MIDDLE_BROWN = rgb(128, 128, 0);
    public static final RGB MIDDLE_RED = rgb(128, 0, 0);
    public static final RGB DARK_THEME_LIHT_RED = rgb(200, 50, 50);
    public static final RGB MIDDLE_ORANGE = rgb(255, 128, 64);

    public static final RGB DARK_GREEN = rgb(0, 64, 0);
    public static final RGB TASK_DEFAULT_RED = rgb(128, 0, 0);

    public static final RGB BLACK = rgb(0, 0, 0);
    public static final RGB ASCIIDOC_HEADLINE_HTML = rgb(186, 57, 37); // #ba3925
    public static final RGB RED = rgb(170, 0, 0);
    public static final RGB GREEN = rgb(0, 170, 0);
    public static final RGB BROWN = rgb(170, 85, 0);
    public static final RGB BLUE = rgb(0, 0, 170);
    public static final RGB MAGENTA = rgb(170, 0, 170);
    public static final RGB CYANN = rgb(0, 170, 170);
    public static final RGB GRAY = rgb(170, 170, 170);
    public static final RGB DARK_THEME_GRAY = rgb(97, 97, 97);
    public static final RGB DARK_GRAY = rgb(85, 85, 85);
    public static final RGB BRIGHT_RED = rgb(255, 85, 85);
    public static final RGB BRIGHT_GREEN = rgb(85, 255, 85);
    public static final RGB YELLOW = rgb(255, 255, 85);
    public static final RGB ORANGE = rgb(255, 165, 0); // http://www.rapidtables.com/web/color/orange-color.htm
    public static final RGB BRIGHT_BLUE = rgb(85, 85, 255);
    public static final RGB MEDIUM_CYAN = rgb(0, 128, 192);
    public static final RGB DARK_BLUE = rgb(0, 64, 128);
    public static final RGB BRIGHT_MAGENTA = rgb(255, 85, 255);

    public static final RGB BRIGHT_CYAN = rgb(85, 255, 255);
    public static final RGB WHITE = rgb(255, 255, 255);

    public static final RGB TASK_CYAN = rgb(0, 128, 128);

    public static final RGB LIGHT_GRAY = rgb(240, 240, 240);

    /**
     * A special dark cyan color for here doc on dark themes
     */
    public static final RGB DARK_THEME_CYAN = rgb(49, 98, 98);

    /**
     * A special dark cyan color for here string on dark themes
     */
    public static final RGB DARK_THEME_BRIGHT_CYAN = rgb(88, 98, 98);

    /**
     * A special light blue color for here string on white themes
     */
    public static final RGB LIGHT_THEME_LIGHT_BLUE = rgb(108, 163, 253);

    public static final RGB DARKTHEME_DEFAULT_TEXT_BOLD = rgb(245, 121, 0);
    public static final RGB DARKTHEME_DEFAULT_TEXT_ITALIC = rgb(196, 160, 0);
    public static final RGB DARKTHEME_DEFAULT_BLOCK_TEXT = rgb(136, 138, 133);
    public static final RGB DARKTHEME_DEFAULT_TEXT = rgb(192, 192, 192);
    public static final RGB DARKTHEME_DEFAULT_HEADLINE = rgb(191, 63, 63);
    public static final RGB DARKTHEME_DEFAULT_COMMENTS = rgb(63, 127, 95);
    public static final RGB DARKTHEME_DEFAULT_COMMANDS = rgb(77, 154, 6);
    public static final RGB DARKTHEME_DEFAULT_KNOWN_VARIABLES = rgb(138, 226, 52);

    public static final RGB DARK_THEME_LIGHT_GREEN = rgb(79, 191, 63);
    public static final RGB DARK_THEME_MEDIUM_ORANGE = rgb(193, 125, 17);// C17D11
    public static final RGB DARK_THEME_MEDIUM_BLUE = rgb(52, 101, 164);// #34 65 A4
    public static final RGB DARK_THEME_LIGHT_BLUE = rgb(114, 159, 207);

    public static final RGB DARK_THEME_PINK = rgb(191, 63, 172); // BF3FAC 

    public static RGB rgb(int r, int g, int b) {
        return new RGB(r, g, b);
    }
}
