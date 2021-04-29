package de.jcup.asciidoctoreditor.asciidoc;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class AsciidoctorConfigFileTest {

    private File file;
    private AsciidoctorConfigFile configFileToTest;

    @Before
    public void before() {
        /* prepare */
        file = new File("./asciidoctorFile");
        
        /* execute*/
        configFileToTest = new AsciidoctorConfigFile("content1", file.toPath());
    }
    
    @Test
    public void configfile_getAbsolutePath_is_string_representation_to_file() {
        assertEquals(file.toPath(),configFileToTest.getLocation());
    }
    
    @Test
    public void configfile_getAsciidoctorconfigdir_represents_parent_folder_of_configfile() {
        assertEquals(file.getParentFile().getAbsolutePath(),configFileToTest.getAsciidoctorconfigdir());
    }
    
    @Test
    public void config_file_content_contains_only_origin_parts() {
        assertEquals("content1",configFileToTest.getContent());
    }
    
    @Test
    public void config_file_content_customized_contains_configdir_variable_with_value_at_the_beginning() {
        /* execute */
        String firstLine = configFileToTest.getContentCustomized().split("\n")[0];
        
        /* test */
        assertEquals(":asciidoctorconfigdir: "+file.getParentFile().getAbsolutePath(),firstLine);
    }
    
    @Test
    public void config_file_content_customized__to_map_contains_configdir_variable_with_value_at_the_beginning() {
        
        /* execute */
        Map<String, String> map = configFileToTest.toContentCustomizedMap();
        
        /* test */
        assertEquals(file.getParentFile().getAbsolutePath(), map.get("asciidoctorconfigdir"));
    }

}
