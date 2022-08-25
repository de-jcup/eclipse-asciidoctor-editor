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

import static org.junit.Assert.*;

import org.junit.Test;

public class ZoomLevelTest {

    @Test
    public void _100_percent_calculates_1_0() {
        assertEquals(Double.valueOf(1.0), ZoomLevel.calculatePercentagefromString("100%"));
        assertEquals(Double.valueOf(1.0), ZoomLevel.calculatePercentagefromString("100  %"));
    }
    
    @Test
    public void _100_percent_constants_correct() {
        assertEquals(Double.valueOf(ZoomLevel.LEVEL_100_PERCENT_VALUE), ZoomLevel.calculatePercentagefromString(ZoomLevel.LEVEL_100_PERCENT_TEXT));
        assertEquals(Double.valueOf(ZoomLevel.LEVEL_100_PERCENT_VALUE), Double.valueOf(1));
    }

    @Test
    public void _33_percent_calculates_0_33() {
        assertEquals(Double.valueOf(0.33), ZoomLevel.calculatePercentagefromString("33%"));
        assertEquals(Double.valueOf(0.33), ZoomLevel.calculatePercentagefromString(" 33 %"));
    }
    
    @Test
    public void null_calculates_null() {
        assertEquals(null, ZoomLevel.calculatePercentagefromString(null));
    }

    @Test
    public void _asdf_calculates_null() {
        assertEquals(Double.valueOf(1.0), ZoomLevel.calculatePercentagefromString("100%"));
        assertEquals(Double.valueOf(1.0), ZoomLevel.calculatePercentagefromString("100  %"));
    }

    @Test
    public void _all_comobox_entries_from_array_return_not_null_on_calculation() {
        for (String item : ZoomLevel.DEFAULT_TEXT_ENTRIES) {
            assertNotNull(ZoomLevel.calculatePercentagefromString(item));
        }
    }

}
