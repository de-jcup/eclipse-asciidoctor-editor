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
