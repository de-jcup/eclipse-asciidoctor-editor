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

import java.util.ArrayList;
import java.util.List;

public class AsciidocBlockScanner {

    private class Context {
        List<AsciidocFormatBlock> asciidocFormatBlocks = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        private AsciidocFormatBlock asciidocFormatBlock;

        public void addLine(String line, boolean appendNewLine) {
            AsciidocBlockType type = AsciidocBlockType.determineType(line);
            current.append(line);
            if (appendNewLine) {
                current.append("\n");
            }
            if (type.isMultipleLines()) {
                if (asciidocFormatBlock== null || asciidocFormatBlock.blockType == null) {
                    /* means not inside a block */
                    closeOldBlockAndStartBlock(type);
                    return;
                } else {
                    if (type == asciidocFormatBlock.blockType) {
                        /* same blockType so this is a closing one */
                        closeOldBlock();
                        return;
                    } else {
                        /* we just add until same blockType, means closing */
                        return;
                    }
                }
            } else {
                /* single line */
                closeOldBlockAndStartSingleLine(type);
            }
        }

        private void closeOldBlock() {
            if (asciidocFormatBlock != null) {
                asciidocFormatBlock.source.append(current.toString());
                asciidocFormatBlocks.add(asciidocFormatBlock);
            }
            current = new StringBuilder();
            asciidocFormatBlock = null;
        }

        private void closeOldBlockAndStartSingleLine(AsciidocBlockType type) {
            if (asciidocFormatBlock != null) {
                asciidocFormatBlocks.add(asciidocFormatBlock);
            }
            asciidocFormatBlock = new AsciidocFormatBlock();
            asciidocFormatBlock.blockType = type;
            asciidocFormatBlock.source.append(current.toString());
            asciidocFormatBlocks.add(asciidocFormatBlock);
            current = new StringBuilder();
            asciidocFormatBlock = null;
        }

        private void closeOldBlockAndStartBlock(AsciidocBlockType type) {
            if (asciidocFormatBlock != null) {
                asciidocFormatBlock.source.append(current.toString());
                asciidocFormatBlocks.add(asciidocFormatBlock);
                current = new StringBuilder();
            }
            asciidocFormatBlock = new AsciidocFormatBlock();
            asciidocFormatBlock.blockType = type;
        }

        public void closeBlock() {
            if (asciidocFormatBlock != null) {
                asciidocFormatBlock.source.append(current.toString());
                asciidocFormatBlocks.add(asciidocFormatBlock);
                current = new StringBuilder();
            }
        }
    }

    public List<AsciidocFormatBlock> scan(String source) {
        Context context = new Context();
        
        StringBuilder sb = new StringBuilder();
        for (char c: source.toCharArray()) {
            if (c=='\n') {
                context.addLine(sb.toString(), true);
                sb=new StringBuilder();
                continue;
            }
            sb.append(c);
        }
        if (sb.length()>0 ) {
            context.addLine(sb.toString(), false);
        }
        context.closeBlock();
        return context.asciidocFormatBlocks;
    }

}
