package de.jcup.asciidoctoreditor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class InstalledJavaBinaryPathResolverTest {

    private SystemAccess systemAccess;
    private InstalledJavaBinaryPathResolver resolverToTest;

    @Before
    public void before() {

        systemAccess = mock(SystemAccess.class);

        resolverToTest = new InstalledJavaBinaryPathResolver(systemAccess);
    }

    @Test
    public void when_java_home_not_set_resolved_is_empty() {
        assertEquals("", resolverToTest.resolvePathToJavaBinary());
    }

    @Test
    public void when_java_home_set_but_does_not_contain_a_java_binary_resolved_is_empty() {
        /* prepare */
        String userHome = System.getProperty("user.home"); // folder exists, but there is no java...
        when(systemAccess.getProperty("java.home")).thenReturn(userHome);

        /* test */
        assertEquals("", resolverToTest.resolvePathToJavaBinary());
    }

    @Test
    public void when_java_home_set_and_does_contain_a_java_binary_resolved_is_empty() throws Exception {
        /* prepare */
        String realJavaHome = System.getProperty("java.home");
        when(systemAccess.getProperty("java.home")).thenReturn(realJavaHome);

        /* check preconditions */
        String os = System.getProperty("os.name").toLowerCase();
        
        String fileName = os.indexOf("window")!=-1 ? "java.exe" : "java";
        File binFolder = new File(realJavaHome,"bin");
        File file = new File(binFolder, fileName);
        assertTrue("Illegal state: java binary no exists:"+file, file.exists());

        /* test */
        assertEquals(file.toPath().toRealPath().toString(), resolverToTest.resolvePathToJavaBinary());
    }

}
