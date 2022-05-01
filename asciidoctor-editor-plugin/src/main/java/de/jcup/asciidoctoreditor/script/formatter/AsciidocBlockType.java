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

import java.util.function.Function;

public enum AsciidocBlockType {
    EMPTY_LINE(AsciidocBlockScanMethods::isNewLine, false),

    TABLE(AsciidocBlockScanMethods::isTableMarker, true),

    HEADLINE(AsciidocBlockScanMethods::isHeadlineMarker, false),

    SIMPLE_COMMENT(AsciidocBlockScanMethods::isSingleLineComment, false),

    BLOCK(AsciidocBlockScanMethods::isBlockMarker, true),

    COMMAND(AsciidocBlockScanMethods::isCommandMarker, false),

    META_INFO(AsciidocBlockScanMethods::isMetaInfoMarker, false),

    VARIABLE(AsciidocBlockScanMethods::isVariableMarker, false),

    TEXT(AsciidocBlockScanMethods::isTextOnly, true),

    UNKNOWN(null, false),;

    private Function<String, Boolean> function;
    private boolean multipleLines;

    private AsciidocBlockType(Function<String, Boolean> function, boolean multipleLines) {
        this.function = function;
        this.multipleLines = multipleLines;
    }

    public boolean isMultipleLines() {
        return multipleLines;
    }

    public boolean isIdentifiedBy(String line) {
        if (function == null) {
            return false;
        }
        Boolean r = function.apply(line);
        if (r == null) {
            return false;
        }
        return r.booleanValue();
    }

    public static AsciidocBlockType determineType(String line) {
        for (AsciidocBlockType type : AsciidocBlockType.values()) {
            if (type.isIdentifiedBy(line)) {
                return type;
            }
        }
        return null;
    }

}
