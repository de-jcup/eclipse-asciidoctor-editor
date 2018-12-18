package de.jcup.asciidoctoreditor;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class AsciiDocFileUtilsTest {

    @Test
    public void test() {
       /* prepare */
       File file = new File("./");
       File asciiDocFile = new File(file,"basefolder/sub1/sub2/sub3/test.adoc");
       File baseDir = new File(file,"basefolder");
       
       /* execute */
       String path = AsciiDocFileUtils.calculatePathToFileFromBase(asciiDocFile, baseDir);
    
       /* test */
       assertEquals("sub1/sub2/sub3/test.adoc",path);
    }

}
