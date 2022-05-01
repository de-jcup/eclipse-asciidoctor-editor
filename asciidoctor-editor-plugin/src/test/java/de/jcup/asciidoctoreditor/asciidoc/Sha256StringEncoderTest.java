/*
 * Copyright 2021 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.asciidoc;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class Sha256StringEncoderTest {

    private Sha256StringEncoder toTest;

    @Before
    public void before() {
        toTest = new Sha256StringEncoder();
    }

    @Test
    public void slash_de_slash_jcup_xyz_encoded_as_expected() {
        assertEquals("076b86a67a73b20ef213c34fb81ff4d8855081e8dc0ba3abd99d5560bc146afb", toTest.encode("/de/jcup/xyz/"));
    }

    @Test
    public void null_returns_null() {
        assertEquals(null, toTest.encode(null));
    }

}
