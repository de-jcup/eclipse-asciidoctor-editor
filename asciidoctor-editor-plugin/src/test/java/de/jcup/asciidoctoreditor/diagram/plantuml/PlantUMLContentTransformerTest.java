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
package de.jcup.asciidoctoreditor.diagram.plantuml;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctoreditor.ContentTransformerData;

public class PlantUMLContentTransformerTest {

    private PlantUMLContentTransformer transformerToTest;

    @Before
    public void before() {
        transformerToTest = new PlantUMLContentTransformer();
    }

    @Test
    public void no_data_provider_no_outputformat() {
        /* prepare */
        ContentTransformerData data = new ContentTransformerData();
        data.filename = "filename";
        data.origin = "origin";

        /* execute */
        String result = transformerToTest.transform(data);

        /* test */
        assertEquals("[plantuml,filename]\n" + "----\n" + "origin\n" + "----\n" + "", result);
    }

    @Test
    public void data_provider_gives_SVG_so_outputformat_svg() {
        /* prepare */
        ContentTransformerData data = new ContentTransformerData();
        data.filename = "filename";
        data.origin = "origin";

        transformerToTest.setDataProvider(new PlantUMLDataProvider() {

            @Override
            public PlantUMLOutputFormat getOutputFormat() {
                return PlantUMLOutputFormat.SVG;
            }
        });

        /* execute */
        String result = transformerToTest.transform(data);

        /* test */
        assertEquals("[plantuml,filename,svg]\n" + "----\n" + "origin\n" + "----\n" + "", result);
    }

    @Test
    public void data_provider_gives_PNG_so_outputformat_png() {
        /* prepare */
        ContentTransformerData data = new ContentTransformerData();
        data.filename = "filename";
        data.origin = "origin";

        transformerToTest.setDataProvider(new PlantUMLDataProvider() {

            @Override
            public PlantUMLOutputFormat getOutputFormat() {
                return PlantUMLOutputFormat.PNG;
            }
        });

        /* execute */
        String result = transformerToTest.transform(data);

        /* test */
        assertEquals("[plantuml,filename,png]\n" + "----\n" + "origin\n" + "----\n" + "", result);
    }

    @Test
    public void data_provider_gives_TXT_so_outputformat_txt() {
        /* prepare */
        ContentTransformerData data = new ContentTransformerData();
        data.filename = "filename";
        data.origin = "origin";

        transformerToTest.setDataProvider(new PlantUMLDataProvider() {

            @Override
            public PlantUMLOutputFormat getOutputFormat() {
                return PlantUMLOutputFormat.TXT;
            }
        });

        /* execute */
        String result = transformerToTest.transform(data);

        /* test */
        assertEquals("[plantuml,filename,txt]\n" + "----\n" + "origin\n" + "----\n" + "", result);
    }

}
