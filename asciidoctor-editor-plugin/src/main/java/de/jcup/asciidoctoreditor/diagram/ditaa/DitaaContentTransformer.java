/*
 * Copyright 2018 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.diagram.ditaa;

import de.jcup.asciidoctoreditor.AbstractContentTransformer;
import de.jcup.asciidoctoreditor.ContentTransformerData;
import de.jcup.asciidoctoreditor.diagram.plantuml.PlantUMLOutputFormat;

public class DitaaContentTransformer extends AbstractContentTransformer {

    @Override
    protected String saveTransform(ContentTransformerData data) {
        StringBuilder sb = new StringBuilder();
        if (data.origin != null) {
            sb.append("[ditaa,");
            sb.append(PlantUMLOutputFormat.SVG.getAsciiDocFormatString());
            sb.append("]\n----\n");
            sb.append(data.origin);
            sb.append("\n----\n");
        }
        return sb.toString();
    }

    @Override
    public boolean isTransforming(Object data) {
        return true;
    }

}
