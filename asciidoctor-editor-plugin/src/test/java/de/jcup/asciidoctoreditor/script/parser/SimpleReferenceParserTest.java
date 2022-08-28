/*
 * Copyright 2018 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.script.parser;

import static de.jcup.asciidoctoreditor.script.parser.AssertReferences.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctoreditor.script.AsciiDoctorFileReference;

public class SimpleReferenceParserTest {
    private SimpleReferenceParser includeParserToTest;
    private SimpleReferenceParser imageParserToTest;

    @Before
    public void before() {
        includeParserToTest = SimpleReferenceParser.INCLUDE_PARSER;
        imageParserToTest = SimpleReferenceParser.IMAGE_PARSER;
    }

    @Test
    public void include_target_txt_found_include() throws Exception {

        /* prepare */
        String text = "include::label.txt";
        // .............01234567890 123456789012

        /* execute */
        List<AsciiDoctorFileReference> result = includeParserToTest.parse(text);

        /* test */
        /* @formatter:off*/
		assertReferences(result).
			hasReferences(1).
			hasReference("include::label.txt").
				withPosition(0).
				withEnd(17);
		/* @formatter:on*/

    }

    @Test
    public void include_target_txt_new_line_headline1_new_line_include_target2_txt_found_2includes() throws Exception {

        /* prepare */
        String text = "include::label.txt\n= headline1\ninclude::target2.txt";
        // .............01234567890123456789 012345678901 23456789012345678901

        /* execute */
        List<AsciiDoctorFileReference> result = includeParserToTest.parse(text);

        /* test */
        /* @formatter:off*/
		assertReferences(result).
			hasReferences(2).
			hasReference("include::label.txt").
				withPosition(0).
				withEnd(17).
			and().
			hasReference("include::target2.txt").
				withPosition(31).
				withEnd(50);
		/* @formatter:on*/

    }

    @Test
    public void headline1_new_line_include_target_txt_found_include() throws Exception {

        /* prepare */
        String text = "headline1\ninclude::label.txt";
        // .............0123456789 0123456789012345678

        /* execute */
        List<AsciiDoctorFileReference> result = includeParserToTest.parse(text);

        /* test */
        /* @formatter:off*/
		assertReferences(result).
			hasReferences(1).
			hasReference("include::label.txt").
				withPosition(10).
				withEnd(27);
		/* @formatter:on*/

    }

    @Test
    public void headline1_new_line_include_target_SPACE_with_SPACE_spaces_txt_found_include() throws Exception {

        /* prepare */
        String text = "headline1\ninclude::label with spaces.txt";
        // .............0123456789 0123456789012345678

        /* execute */
        List<AsciiDoctorFileReference> result = includeParserToTest.parse(text);

        /* test */
        /* @formatter:off*/
        assertReferences(result).
            hasReferences(1).
            hasReference("include::label with spaces.txt").
                withPosition(10).
                withEnd(39);
        /* @formatter:on*/

    }

    @Test
    public void headline1_new_line_image_target_SPACE_with_SPACE_spaces_png_found_include() throws Exception {

        /* prepare */
        String text = "headline1\nimage::label with spaces.png[]";
        // .............0123456789 01234567890123456789012345678

        /* execute */
        List<AsciiDoctorFileReference> result = imageParserToTest.parse(text);

        /* test */
        /* @formatter:off*/
        assertReferences(result).
            hasReferences(1).
            hasReference("image::label with spaces.png").
                withPosition(10).
                withEnd(38);
        /* @formatter:on*/

    }

}
