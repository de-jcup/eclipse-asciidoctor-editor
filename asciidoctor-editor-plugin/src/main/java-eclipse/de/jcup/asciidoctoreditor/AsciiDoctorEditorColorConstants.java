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
package de.jcup.asciidoctoreditor;

import org.eclipse.swt.graphics.RGB;

public interface AsciiDoctorEditorColorConstants {
	
	public static final RGB GRAY_JAVA = rgb(192,192,192);//grey as in java
	
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

	public static final RGB OUTLINE_ITEM__TYPE = rgb(149, 125, 71); // same
																	// as
																	// java
																	// outline
																	// string
	public static final RGB MIDDLE_GRAY = rgb(128,128,128);
	public static final RGB MIDDLE_GREEN = rgb(0, 128, 0);
	public static final RGB MIDDLE_BROWN = rgb(128, 128, 0);
	public static final RGB MIDDLE_RED = rgb(128, 0, 0);
	public static final RGB MIDDLE_ORANGE = rgb(255,128,64);

	public static final RGB DARK_GREEN = rgb(0, 64, 0);
	public static final RGB TASK_DEFAULT_RED = rgb(128, 0, 0);

	public static final RGB BLACK = rgb(0, 0, 0);
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

	public static final RGB TASK_CYAN = rgb(0,128,128);
	
	/**
	 * A special dark cyan color for here doc on dark themes
	 */
	public static final RGB DARK_THEME_HEREDOC = rgb(49,98,98);

	/**
	 * A special dark cyan color for here string on dark themes
	 */
	public static final RGB DARK_THEME_HERESTRING = rgb(88,98,98);
	
	
	/**
	 * A special light blue color for here string on white themes
	 */
	public static final RGB LIGHT_THEME_HERESTRING = rgb(108,163,253);
	
	

	
	
	public static RGB rgb(int r, int g, int b) {
		return new RGB(r, g, b);
	}
}
