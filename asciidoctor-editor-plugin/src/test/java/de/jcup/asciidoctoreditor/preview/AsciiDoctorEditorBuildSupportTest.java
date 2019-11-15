/*
 * Copyright 2018 Albert Tregnaghi
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package de.jcup.asciidoctoreditor.preview;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocFileUtils;

@RunWith(Parameterized.class)
public class AsciiDoctorEditorBuildSupportTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { AsciiDocFileUtils.createTempFolderForId("my-doc-project").toFile().getAbsolutePath(),
                        "images/diagram.svg" },
                { AsciiDocFileUtils.createTempFolderForId("My Project has spaces").toFile().getAbsolutePath(),
                        "images/diagram.svg" },
                { AsciiDocFileUtils.createTempFolderForId("MyProjectHa$ReservedPattern(Characters)").toFile()
                        .getAbsolutePath(), "images/diagram.svg" },
                { AsciiDocFileUtils.createTempFolderForId("My %Project%").toFile().getAbsolutePath(),
                        "images/diagram.svg" }
        });
    }

    private String absolutePath;
    private String relativePath;

    public AsciiDoctorEditorBuildSupportTest(String absolutePath, String relativePath) {
        this.absolutePath = absolutePath;
        this.relativePath = relativePath;
    }

    @Test
    public void should_transform_absolute_path_in_relative_path_in_html() throws Exception {
        AsciiDoctorEditor asciiDoctorEditor = mock(AsciiDoctorEditor.class, Mockito.RETURNS_DEEP_STUBS);
        when(asciiDoctorEditor.getWrapper().getTempFolder()).thenReturn(Paths.get(absolutePath));
        AsciiDoctorEditorBuildSupport editorBuildSupport = new AsciiDoctorEditorBuildSupport(asciiDoctorEditor);

        String url = Paths.get(absolutePath, relativePath).toUri().getRawPath().substring(1);
        String htmlWithRelativePaths = editorBuildSupport
                .transformAbsolutePathesToRelatives(String.format("<img src=\"%s\"></img>", url.toString()));

        assertEquals("src attribute should only contains the relative file path",
                String.format("<img src=\"%s\"></img>", relativePath), htmlWithRelativePaths);
    }
}
