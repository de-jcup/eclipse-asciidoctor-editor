package de.jcup.asciidoctoreditor;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class HTMLOutputParserTest {
    
    private AsciidoctorHTMLOutputParser toTest;

    @Before
    public void before() {
        toTest = new AsciidoctorHTMLOutputParser();
    }

    @Test
    public void img_src_start_but_no_end_nothing_found() {
        /* prepare */
        String html ="<html>\n<img src=\"></html>";
        
        /* execute */
        Set<String> found = toTest.findImageSourcePathes(html);
        
        /* test */
        assertEquals(0,found.size());
        
    }
    @Test
    public void only_img_src_no_string_start_nothing_found() {
        /* prepare */
        String html ="<html>\n<img src=";
        
        /* execute */
        Set<String> found = toTest.findImageSourcePathes(html);
        
        /* test */
        assertEquals(0,found.size());
        
    }
    @Test
    public void only_img_src_start_nothing_found() {
        /* prepare */
        String html ="<html>\n<img src=\"";
        
        /* execute */
        Set<String> found = toTest.findImageSourcePathes(html);
        
        /* test */
        assertEquals(0,found.size());
        
    }
    
    @Test
    public void img_src_start_found_twice_bot_not_correct_ended_one_found_containing_other_stuff_between() {
        /* prepare */
        String html ="<html>\n<img src=\"img src=\"></html>";
        
        /* execute */
        Set<String> found = toTest.findImageSourcePathes(html);
        
        /* test */
        assertEquals(1,found.size());
        assertEquals("img src=", found.iterator().next());
        
    }
    
    
    @Test
    public void one_img_src_found() {
        /* prepare */
        String html ="<html>\n<img src=\"/tmp/xyz/val/zz/image1.jpg\"></html>";
        
        /* execute */
        Set<String> found = toTest.findImageSourcePathes(html);
        
        /* test */
        assertEquals(1,found.size());
        assertEquals("/tmp/xyz/val/zz/image1.jpg", found.iterator().next());
        
    }
    
    @Test
    public void two_img_src_diff_path_two_found() {
        /* prepare */
        String html ="<html>\n<img src=\"/tmp/xyz/val/zz/image1.jpg\"><div></div><img src=\"/tmp/xyz/val/zz/image2.jpg\"></html>";
        
        /* execute */
        Set<String> found = toTest.findImageSourcePathes(html);
        
        /* test */
        assertEquals(2,found.size());
        Iterator<String> iterator = found.iterator();
        assertEquals("/tmp/xyz/val/zz/image1.jpg", iterator.next());
        assertEquals("/tmp/xyz/val/zz/image2.jpg", iterator.next());
        
    }
    
    @Test
    public void two_img_src_but_same_path_one_found() {
        /* prepare */
        String html ="<html>\n<img src=\"/tmp/xyz/val/zz/image1.jpg\"><div></div><img src=\"/tmp/xyz/val/zz/image1.jpg\"></html>";
        
        /* execute */
        Set<String> found = toTest.findImageSourcePathes(html);
        
        /* test */
        assertEquals(1,found.size());
        assertEquals("/tmp/xyz/val/zz/image1.jpg", found.iterator().next());
        
    }
    
    
    @Test
    public void xyz_src_not_found() {
        /* prepare */
        String html ="<html>\n<xyz src=\"/tmp/xyz/val/zz/image1.jpg\"></html>";
        
        /* execute */
        Set<String> found = toTest.findImageSourcePathes(html);
        
        /* test */
        assertEquals(0,found.size());
        
    }

}
