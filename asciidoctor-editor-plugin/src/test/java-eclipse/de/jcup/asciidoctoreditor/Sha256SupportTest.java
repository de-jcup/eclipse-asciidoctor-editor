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

    @Test(expected=IllegalArgumentException.class)
    public void null_string_raises_exception() {
        supportToTest.createChecksum(null);
    }
}
