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
package de.jcup.asciidoctoreditor.asciidoc;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class AsciiDocConfigFileSupportTest {

    private AsciiDocConfigFileSupport supportToTest;
    private Path p1Test1AdocFile;
    private Path p1Test2AdocFile;
    private Path project1Path;
    private Path project2Path;
    private Path p1Test3AdocFile;
    private Path p1AsciidocConfigFile1;
    private Path p1AsciidocConfigFile2;
    private Path p2Test1AdocFile;
    private Path p2AsciidocConfigFile1;
    private Path p2Test2AdocFile;
    private Path p2Test3AdocFile;
    private Path p2AsciidocConfigFile2;

    @Before
    public void no_adoc_before() {
        File asciidoctorconfigfilesFolder = new File("./src/test/resources/asciidoctorconfigfiles");
        /* project1 - uses config files without .adoc ending */
        project1Path = new File(asciidoctorconfigfilesFolder, "project1").toPath();

        p1Test1AdocFile = project1Path.resolve("test1.adoc");
        p1AsciidocConfigFile1 = project1Path.resolve(".asciidoctorconfig");
        p1Test2AdocFile = project1Path.resolve("subfolder1/test2.adoc");

        p1Test3AdocFile = project1Path.resolve("subfolder2/test3.adoc");
        p1AsciidocConfigFile2 = p1Test3AdocFile.getParent().resolve(".asciidoctorconfig");

        /* project2 - uses config files with .adoc ending */
        project2Path = new File(asciidoctorconfigfilesFolder, "project2").toPath();
        p2Test1AdocFile = project2Path.resolve("test1.adoc");
        p2AsciidocConfigFile1 = project2Path.resolve(".asciidoctorconfig.adoc");
        p2Test2AdocFile = project2Path.resolve("subfolder1/test2.adoc");

        p2Test3AdocFile = project2Path.resolve("subfolder2/test3.adoc");
        p2AsciidocConfigFile2 = p2Test3AdocFile.getParent().resolve(".asciidoctorconfig.adoc");

        supportToTest = new AsciiDocConfigFileSupport(asciidoctorconfigfilesFolder.toPath().getParent());
    }

    @Test
    public void no_adoc_when_project1_folder_is_also_root_we_can_fetch_still_one_config_for_test1() {
        /* prepare */
        supportToTest = new AsciiDocConfigFileSupport(project1Path);

        /* execute */
        List<AsciidoctorConfigFile> files = supportToTest.collectConfigFiles(p1Test1AdocFile);

        /* test */
        assertEquals(1, files.size());
        AsciidoctorConfigFile configFile = files.iterator().next();
        assertEquals(p1AsciidocConfigFile1.toString(), configFile.getLocation().toString());
        assertEquals(":my-var1: project1-root\n", configFile.getContent());
    }

    @Test
    public void no_adoc_fetchConfigurations_test1_file_project1() {
        /* execute */
        List<AsciidoctorConfigFile> files = supportToTest.collectConfigFiles(p1Test1AdocFile);

        /* test */
        assertEquals(1, files.size());
        AsciidoctorConfigFile configFile = files.iterator().next();
        assertEquals(p1AsciidocConfigFile1.toString(), configFile.getLocation().toString());
        assertEquals(":my-var1: project1-root\n", configFile.getContent());
    }

    @Test
    public void no_adoc_fetchConfigurations_test2_file_project1() {
        /* execute */
        List<AsciidoctorConfigFile> files = supportToTest.collectConfigFiles(p1Test2AdocFile);

        /* test */
        assertEquals(1, files.size());
        AsciidoctorConfigFile configFile = files.iterator().next();
        assertEquals(p1AsciidocConfigFile1.toString(), configFile.getLocation().toString());
        assertEquals(":my-var1: project1-root\n", configFile.getContent());
    }

    @Test
    public void no_adoc_fetchConfigurations_test3_file_project1() {
        /* execute */
        List<AsciidoctorConfigFile> files = supportToTest.collectConfigFiles(p1Test3AdocFile);

        /* test */
        assertEquals(2, files.size());
        Iterator<AsciidoctorConfigFile> iterator = files.iterator();

        AsciidoctorConfigFile configFile1 = iterator.next();
        assertEquals(p1AsciidocConfigFile1.toString(), configFile1.getLocation().toString());
        assertEquals(":my-var1: project1-root\n", configFile1.getContent());

        AsciidoctorConfigFile configFile2 = iterator.next();
        assertEquals(p1AsciidocConfigFile2.toString(), configFile2.getLocation().toString());
        assertEquals(":my-var1: project1-subfolder2\n", configFile2.getContent());
    }

    @Test
    public void with_adoc_fetchConfigurations_test1_file_project1() {
        /* execute */
        List<AsciidoctorConfigFile> files = supportToTest.collectConfigFiles(p2Test1AdocFile);

        /* test */
        assertEquals(1, files.size());
        AsciidoctorConfigFile configFile = files.iterator().next();
        assertEquals(p2AsciidocConfigFile1.toString(), configFile.getLocation().toString());
        assertEquals(":my-var1: project1-root\n", configFile.getContent());
    }

    @Test
    public void with_adoc_fetchConfigurations_test2_file_project1() {
        /* execute */
        List<AsciidoctorConfigFile> files = supportToTest.collectConfigFiles(p2Test2AdocFile);

        /* test */
        assertEquals(1, files.size());
        AsciidoctorConfigFile configFile = files.iterator().next();
        assertEquals(p2AsciidocConfigFile1.toString(), configFile.getLocation().toString());
        assertEquals(":my-var1: project1-root\n", configFile.getContent());
    }

    @Test
    public void with_adoc_fetchConfigurations_test3_file_project1() {
        /* execute */
        List<AsciidoctorConfigFile> files = supportToTest.collectConfigFiles(p2Test3AdocFile);

        /* test */
        assertEquals(2, files.size());
        Iterator<AsciidoctorConfigFile> iterator = files.iterator();

        AsciidoctorConfigFile configFile1 = iterator.next();
        assertEquals(p2AsciidocConfigFile1.toString(), configFile1.getLocation().toString());
        assertEquals(":my-var1: project1-root\n", configFile1.getContent());

        AsciidoctorConfigFile configFile2 = iterator.next();
        assertEquals(p2AsciidocConfigFile2.toString(), configFile2.getLocation().toString());
        assertEquals(":my-var1: project1-subfolder2\n", configFile2.getContent());
    }

    @Test
    public void calculate_when_config_map_is_empty_origin_returned() {
        /* prepare */
        Map<String, String> map = new LinkedHashMap<>();

        /* execute */
        String result = supportToTest.calculate("{other1}/my1/{other2}", map);

        /* test */
        assertEquals("{other1}/my1/{other2}", result);
    }

    @Test
    public void calculate_when_config_map_contains_keys_origin_is_replaced_with_values() {
        /* prepare */
        Map<String, String> map = new LinkedHashMap<>();
        map.put("other1", "x1");
        map.put("other2", "x2");

        /* execute */
        String result = supportToTest.calculate("{other1}/my1/{other2}", map);

        /* test */
        assertEquals("x1/my1/x2", result);
    }

}
