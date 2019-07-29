package de.jcup.asciidoctoreditor.script.formatter;

import java.util.function.Function;

public enum AsciidocBlockType {
    EMPTY_LINE(AsciidocBlockScanMethods::isNewLine,false),

    TABLE(AsciidocBlockScanMethods::isTableMarker,true),

    HEADLINE(AsciidocBlockScanMethods::isHeadlineMarker,false),

    SIMPLE_COMMENT(AsciidocBlockScanMethods::isSingleLineComment,false),

    BLOCK(AsciidocBlockScanMethods::isBlockMarker,true),

    COMMAND(AsciidocBlockScanMethods::isCommandMarker,false),

    META_INFO(AsciidocBlockScanMethods::isMetaInfoMarker,false),

    VARIABLE(AsciidocBlockScanMethods::isVariableMarker,false),

    TEXT(AsciidocBlockScanMethods::isTextOnly,true),

    UNKNOWN(null,false),;

    private Function<String, Boolean> function;
    private boolean multipleLines;

    private AsciidocBlockType(Function<String, Boolean> function, boolean multipleLines) {
        this.function = function;
        this.multipleLines=multipleLines;
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
