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
package de.jcup.asciidoctoreditor.toolbar;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class PlantUMLExampleDiagramTest {

    @Test
    public void every_entry_has_correct_path_and_file_does_exist() throws IOException {
        StringBuilder problems = new StringBuilder();
        for (PlantUMLExampleDiagram diagram : PlantUMLExampleDiagram.values()) {
            String path = diagram.getPath();
            File file = new File("./" + path);
            if (!file.exists()) {
                problems.append(diagram.name() + " has not correct file path! '" + file.getCanonicalPath() + "' does not exists!\n");
            }
        }

        if (problems.length() > 0) {
            fail(problems.toString());
        }

    }

}
