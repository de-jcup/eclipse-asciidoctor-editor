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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class ColorUtil {

	/**
	 * Returns a web color in format "#RRGGBB"
	 * 
	 * @param color
	 * @return web color as string
	 */
	public static String convertToHexColor(Color color) {
		if (color == null) {
			return null;
		}
		return convertToHexColor(color.getRGB());
	}

	public static String convertToHexColor(RGB rgb) {
		if (rgb == null) {
			return null;
		}
		String hex = String.format("#%02x%02x%02x", rgb.red, rgb.green, rgb.blue);
		return hex;
	}
}
