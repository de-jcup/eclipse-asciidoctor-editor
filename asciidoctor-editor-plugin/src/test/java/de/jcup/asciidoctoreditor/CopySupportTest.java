/*
 * Copyright 2020 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class CopySupportTest {

    @Test
    public void base_folder1_files_convert_to_target_files() {
        /* prepare */
        File originBase = new File("./parent1/");
        File targetBase = new File("./parent2/");

        File sourceFile1 = new File(originBase, "test1.puml");
        File sourcefile2 = new File(originBase, "test2.puml");
        File sourceFile3 = new File(originBase, "sub1/test3.puml");

        /* execute */
        CopySupport support = new CopySupport(originBase, targetBase);
        List<File> result = support.createTargetFiles(Arrays.asList(sourceFile1, sourcefile2, sourceFile3));

        /* test */
        File expectedTargetFile1 = new File(targetBase, "test1.puml");
        File expectedTargetFile2 = new File(targetBase, "test2.puml");
        File expectedTargetFile3 = new File(targetBase, "sub1/test3.puml");

        Iterator<File> it = result.iterator();
        assertEquals(expectedTargetFile1.toString(), it.next().toString());
        assertEquals(expectedTargetFile2.toString(), it.next().toString());
        assertEquals(expectedTargetFile3.toString(), it.next().toString());

    }

}
