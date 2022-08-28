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
package de.jcup.asciidoctoreditor;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class Sha256SupportTest {

    private Sha256Support supportToTest;

    @Before
    public void before() {
        supportToTest = new Sha256Support();
    }

    @Test
    public void sha_256_checksum_for_abc_can_be_calculated() {
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", supportToTest.createChecksum("abc"));
    }

    @Test
    public void empty_string_has_sha_256_checksum() {
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", supportToTest.createChecksum(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void null_string_raises_exception() {
        supportToTest.createChecksum(null);
    }
}
