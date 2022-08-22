package de.jcup.asciidoctoreditor;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SystemAccessTest {

    private SystemAccess accessToTest;

    @Before
    public void before() {
        accessToTest = new SystemAccess();
    }
    
    @Test
    public void user_home_key_returns_system_property_value_use_home() {
        /* prepare */
        String userHomeDirectlyAsProperty = System.getProperty("user.home");
        assertNotNull(userHomeDirectlyAsProperty);
        
        /* execute */
        String result = accessToTest.getProperty("user.home");
        
        /* test */
        assertEquals(userHomeDirectlyAsProperty,result);
    }

}
