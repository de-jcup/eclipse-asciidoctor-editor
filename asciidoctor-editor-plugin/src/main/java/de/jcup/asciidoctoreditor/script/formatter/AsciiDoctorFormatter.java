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
package de.jcup.asciidoctoreditor.script.formatter;

import java.util.List;

public class AsciiDoctorFormatter {
    private AsciidocBlockScanner blockScanner = new AsciidocBlockScanner();
    private AsciiDoctorSimpleTextFormatter simpleTextFormatter = new AsciiDoctorSimpleTextFormatter();

    public String format(String origin, AsciiDoctorFormatterConfig config) {
        if (origin == null) {
            return "";
        }
        List<AsciidocFormatBlock> blocks = blockScanner.scan(origin);
        StringBuilder sb = new StringBuilder();
        for (AsciidocFormatBlock block : blocks) {
            handleBlock(block,config);
            sb.append(block.source);
        }
        return sb.toString();
    }

    private void handleBlock(AsciidocFormatBlock block, AsciiDoctorFormatterConfig config) {
        if (block.blockType == AsciidocBlockType.TEXT) {
            block.source=new StringBuilder().append(simpleTextFormatter.format(block.source.toString(), config));
        }
    }

}
