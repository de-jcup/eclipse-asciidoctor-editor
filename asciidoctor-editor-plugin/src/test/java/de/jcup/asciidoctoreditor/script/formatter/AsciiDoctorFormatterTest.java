/*
 * Copyright 2020 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.script.formatter;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.jcup.asciidoctoreditor.TestScriptLoader;

@Ignore
/**
 * There seems to be a parser written in java for asciidoctor in next future. So
 * stop implementing this. TODO de-jcup,2019-08-30: Remove impl and test when
 * parser available
 * https://github.com/de-jcup/eclipse-asciidoctor-editor/issues/255
 * 
 * @author de-jcup
 *
 */
public class AsciiDoctorFormatterTest {

    private AsciiDoctorFormatter formatterToTest;
    private AsciiDoctorFormatterConfig config;

    @Before
    public void before() {
        formatterToTest = new AsciiDoctorFormatter();
        config = new AsciiDoctorFormatterConfig();
    }

    @Test
    public void test1_lorem_ipsum() throws Exception {
        /* prepare */
        config.maxColumn = 80;

        /* execute + test */
        assertEquals(loadFeatureFile("feature-255-01-expected-when-max-column-80.adoc"), formatterToTest.format(loadFeatureFile("feature-255-01-origin.adoc"), config));
    }

    @Test
    public void test2_block_inside() throws Exception {
        /* prepare */
        config.maxColumn = 80;

        /* execute + test */
        assertEquals(loadFeatureFile("feature-255-02-expected-when-max-column-80.adoc"), formatterToTest.format(loadFeatureFile("feature-255-02-origin.adoc"), config));
    }

    private String loadFeatureFile(String filename) throws IOException {
        return TestScriptLoader.loadScriptFromTestScripts("features/feature-255-formatter/" + filename);
    }

}
