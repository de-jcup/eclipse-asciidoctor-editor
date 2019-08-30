package de.jcup.asciidoctoreditor.script.formatter;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore 
/**
 * There seems to be a parser written in java for asciidoctor in next future. So stop implementing this. 
 * TODO de-jcup,2019-08-30: Remove impl and test when parser available
 * https://github.com/de-jcup/eclipse-asciidoctor-editor/issues/255
 * @author de-jcup
 *
 */
public class AsciidocBlockScannerTest {
    private AsciidocBlockScanner scannerToTest;

    @Before
    public void before() {
        scannerToTest=new AsciidocBlockScanner();
    }

    @Test
    public void empty_string_contains_no_blocks() {
        /* prepare */
        String line = "";
        
        /* execute */
        List<AsciidocFormatBlock> result = scannerToTest.scan(line);
        
        /* test */
        assertNotNull(result);
        assertEquals(0,result.size());

        assertSourcesAddedLikeOrigin(result,line);
    }
    
    
    @Test
    public void headline() {
        /* prepare */
        String line = "== headline";

        /* execute */
        List<AsciidocFormatBlock> result = scannerToTest.scan(line);

        /* test */
        assertNotNull(result);
        assertEquals(1,result.size());
        AsciidocFormatBlock n = result.iterator().next();
        assertEquals(AsciidocBlockType.HEADLINE, n.blockType);
        assertEquals("== headline", n.source.toString());

        assertSourcesAddedLikeOrigin(result,line);
    }
    
    @Test
    public void textBlock() {
        /* prepare */
        String line = "I am just a text\nwith multipe lines";

        /* execute */
        List<AsciidocFormatBlock> result = scannerToTest.scan(line);

        /* test */
        assertNotNull(result);
        assertEquals(1,result.size());
        AsciidocFormatBlock n = result.iterator().next();
        assertEquals(AsciidocBlockType.TEXT, n.blockType);
        assertEquals("I am just a text\nwith multipe lines", n.source.toString());

        assertSourcesAddedLikeOrigin(result,line);
    }
    
    
    @Test
    public void textBlocks_separated_by_one_empty_line() {
        /* prepare */
        String line = "I am just a text\nwith multipe lines\n\nNew textblock";

        /* execute */
        List<AsciidocFormatBlock> result = scannerToTest.scan(line);

        /* test */
        assertNotNull(result);
        assertEquals(3,result.size());
        Iterator<AsciidocFormatBlock> iterator = result.iterator();
        AsciidocFormatBlock n = iterator.next();
        assertEquals(AsciidocBlockType.TEXT, n.blockType);
        assertEquals("I am just a text\nwith multipe lines\n", n.source.toString());
        
        n = iterator.next();
        assertEquals(AsciidocBlockType.EMPTY_LINE, n.blockType);
        assertEquals("\n", n.source.toString());
        
        n = iterator.next();
        assertEquals(AsciidocBlockType.TEXT, n.blockType);
        assertEquals("New textblock", n.source.toString());

        assertSourcesAddedLikeOrigin(result,line);
    }
    
    @Test
    public void textBlock_separated_by_one_empty_line_before() {
        /* prepare */
        String line = "\nI am just a text\nwith multipe lines";

        /* execute */
        List<AsciidocFormatBlock> result = scannerToTest.scan(line);

        /* test */
        assertNotNull(result);
        assertEquals(2,result.size());
        Iterator<AsciidocFormatBlock> iterator = result.iterator();
        AsciidocFormatBlock n = iterator.next();
        assertEquals(AsciidocBlockType.EMPTY_LINE, n.blockType);
        assertEquals("\n", n.source.toString());
        
        n = iterator.next();
        assertEquals(AsciidocBlockType.TEXT, n.blockType);
        assertEquals("I am just a text\nwith multipe lines", n.source.toString());
        
        assertSourcesAddedLikeOrigin(result,line);
    }
    
    @Test
    public void textBlock_inside_two_empty_lines() {
        /* prepare */
        String line = "\nsomething\n\n";

        /* execute */
        List<AsciidocFormatBlock> result = scannerToTest.scan(line);

        /* test */
        assertNotNull(result);
        assertEquals(3,result.size());
        Iterator<AsciidocFormatBlock> iterator = result.iterator();
        
        AsciidocFormatBlock n = iterator.next();
        assertEquals(AsciidocBlockType.EMPTY_LINE, n.blockType);
        assertEquals("\n", n.source.toString());
        
        n = iterator.next();
        assertEquals(AsciidocBlockType.TEXT,n.blockType);
        assertEquals("something\n", n.source.toString());
        
        n = iterator.next();
        assertEquals(AsciidocBlockType.EMPTY_LINE, n.blockType);
        assertEquals("\n", n.source.toString());

        assertSourcesAddedLikeOrigin(result,line);
    }

    @Test
    public void textBlock_inside_two_empty_lines_indented_with_one_space() {
        /* prepare */
        String line = "\n something\n\n";

        /* execute */
        List<AsciidocFormatBlock> result = scannerToTest.scan(line);

        /* test */
        assertNotNull(result);
        assertEquals(3,result.size());
        Iterator<AsciidocFormatBlock> iterator = result.iterator();
        
        AsciidocFormatBlock n = iterator.next();
        assertEquals(AsciidocBlockType.EMPTY_LINE, n.blockType);
        assertEquals("\n", n.source.toString());
        
        n = iterator.next();
        assertEquals(AsciidocBlockType.TEXT,n.blockType);
        assertEquals(" something\n", n.source.toString());
        
        n = iterator.next();
        assertEquals(AsciidocBlockType.EMPTY_LINE, n.blockType);
        assertEquals("\n", n.source.toString());

        assertSourcesAddedLikeOrigin(result,line);
    }
    
    @Test
    public void empty_line_headline_empty_line_empty_line() {
        /* prepare */
        String line = "\n== headline\n\n\n";
        
        /* execute */
        List<AsciidocFormatBlock> result = scannerToTest.scan(line);
        
        /* test */
        assertNotNull(result);
        assertEquals(4,result.size());
        Iterator<AsciidocFormatBlock> iterator = result.iterator();
        AsciidocFormatBlock n = iterator.next();
        assertEquals("\n", n.source.toString());
        assertEquals(AsciidocBlockType.EMPTY_LINE, n.blockType);
        
        n = iterator.next();
        assertEquals("== headline\n", n.source.toString());
        assertEquals(AsciidocBlockType.HEADLINE, n.blockType);
        
        n = iterator.next();
        assertEquals("\n", n.source.toString());
        assertEquals(AsciidocBlockType.EMPTY_LINE, n.blockType);
        
        n = iterator.next();
        assertEquals("\n", n.source.toString());
        assertEquals(AsciidocBlockType.EMPTY_LINE, n.blockType);
    
        assertSourcesAddedLikeOrigin(result,line);
    }

    private void assertSourcesAddedLikeOrigin(List<AsciidocFormatBlock> result, String line) {
        StringBuilder sb = new StringBuilder();
        for (AsciidocFormatBlock block: result) {
            sb.append(block.source);
        }
        assertEquals(line,sb.toString());
    }
}
