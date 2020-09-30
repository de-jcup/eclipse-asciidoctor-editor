package de.jcup.asciidoctoreditor.diagram.plantuml;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctoreditor.TestResourcesLoader;

public class PlantUMLLocalIncludeHierarchySearchTest {

    private PlantUMLLocalIncludeHierarchySearch search;

    @Before
    public void before() {
        search = new PlantUMLLocalIncludeHierarchySearch();
    }

    @Test
    public void a_text_without_local_includes_returns_empty_list() throws Exception {

        /* prepare */
        /* @formatter:off */
        String plantUml="@startuml context\n" + 
                "!includeurl https://raw.githubusercontent.com/RicardoNiepel/C4-PlantUML/release/1-0/C4_Container.puml\n" + 
                "\n" + 
                "Person(administrator, \"Administrator\")\n" + 
                "System(jenkins, \"Jenkins\")\n" + 
                "Rel(administrator, jenkins, \"Administrates Jenkins\", \"SSH\")\n" + 
                "\n" + 
                "@enduml";
        /* @formatter:on */

        /* execute */
        List<File> found = search.searchLocalIncludes(plantUml);

        /* test */
        assertEquals(0, found.size());
    }

    @Test
    public void test1_puml_is_resolved_together_with_it_sub_includes() throws Exception{
        
        /* prepare */
        /* @formatter:off */
        String plantUml="@startuml context\n" + 
                "!include test1.puml\n" + 
                "\n" + 
                "@enduml";
        /* @formatter:on */
       search.setBaseFolder(TestResourcesLoader.assertTestFile("test1.puml").getParentFile());
       /* execute */ 
       List<File> found = search.searchLocalIncludes(plantUml);
       
       
       /* test */
       assertEquals(3,found.size());
       Iterator<File> it = found.iterator();
       File file1 = it.next();
       File file2 = it.next();
       File file3 = it.next();
       
       assertEquals("test1.puml",file1.getName());
       assertEquals("test2.puml",file2.getName());
       assertEquals("test3.puml",file3.getName());
       assertEquals("sub1",file3.getParentFile().getName());
       
    }

}
