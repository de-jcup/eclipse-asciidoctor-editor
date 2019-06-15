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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ColorManager {
	private static ColorManager standalone;
	private Map<RGB, Color> fColorTable = new HashMap<>(10);

	public ColorManager() {

	}

	/**
	 * @return color manager for standalone SWT programs, never <code>null</code>. 
	 * @throws IllegalStateException when no standalone color manager set but used
	 */
	public static ColorManager getStandalone() {
		if (standalone==null){
			throw new IllegalStateException("no standalone color manager set.");
		}
		return standalone;
	}

	/**
	 * Set color manager for standalone SWT programs
	 * @param standalone
	 */
	public static void setStandalone(ColorManager standalone) { // NO_UCD (test only)
		ColorManager.standalone = standalone;
	}

	public void dispose() {
		Iterator<Color> e = fColorTable.values().iterator();
		while (e.hasNext()) {
			e.next().dispose();
		}
	}

	public Color getColor(RGB rgb) {
		Color color = fColorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}

}
