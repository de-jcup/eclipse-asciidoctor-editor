package de.jcup.asciidoctoreditor.script.formatter;

public class AsciidocBlockScanMethods {

    public static Boolean isNewLine(String line) {
        return line.isEmpty() || line.startsWith("\n");
    }

    public static Boolean isBlockMarker(String line) {
        return line.startsWith("----");
    }
    
    public static Boolean isMetaInfoMarker(String line) {
        return line.startsWith("[");
    }

    public static Boolean isTableMarker(String line) {
        return line.startsWith("|==");
    }

    public static Boolean isSingleLineComment(String line) {
        return line.trim().startsWith("//");
    }

    public static Boolean isHeadlineMarker(String line) {
        return line.indexOf("= ") != -1;
    }

    public static Boolean isVariableMarker(String line) {
        return line.startsWith(":");
    }

    public static Boolean isCommandMarker(String line) {
        if (line.indexOf("::") == -1) {
            return false;
        }
        /* could be command - e.g. include:: or plantuml:: etc. */
        char before = 'a';
        for (char c : line.toCharArray()) {
            if (c == ' ' || c == '"' || c == '\'') {
                return false;
            }
            if (c == ':') {
                if (before == ':') {
                    return true;
                }
            }
            before = c;
        }
        return false;
    }

    public static Boolean isTextOnly(String line) {
        boolean textOnly = true;
        textOnly = textOnly && !isNewLine(line);
        textOnly = textOnly && !isTableMarker(line);
        textOnly = textOnly && !isBlockMarker(line);
        textOnly = textOnly && !isHeadlineMarker(line);
        textOnly = textOnly && !isHeadlineMarker(line);
        textOnly = textOnly && !isVariableMarker(line);
        textOnly = textOnly && !isCommandMarker(line);

        return textOnly;

    }
}
