/*
 * Copyright 2022 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.toolbar;

public class ZoomLevel {

    public static final double LEVEL_100_PERCENT_VALUE=1;
    public static final String LEVEL_100_PERCENT_TEXT = "100 %";
    /* @formatter:off */
    public final static String[] DEFAULT_TEXT_ENTRIES = { 
            "25 %", 
            "50 %", 
            LEVEL_100_PERCENT_TEXT, 
            "150 %", 
            "200 %", 
            "300 %", 
            "400 %" };
    /* @formatter:on */

    /**
     * Calculates percentage from string. For example: "100%" will return "1.0", "50% will return "0.5" etc.
     * When calculation is not possible, <code>null</code> is returned.
     * @param text
     * @return percentage or <code>null</code>
     */
    public static Double calculatePercentagefromString(String text) {
        if (text == null) {
            return null;
        }
        String[] splitted = text.split("%");
        if (splitted.length < 1) {
            return null;
        }
        String valueOnly = splitted[0].trim();
        Integer value = Integer.parseInt(valueOnly);

        double percentage = ((double) value) / 100;

        return percentage;
    }
}
