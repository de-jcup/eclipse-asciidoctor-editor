package de.jcup.asciidoctoreditor.script.formatter;

/**
 * Formats given text to max column size
 * @author albert
 *
 */
public class AsciiDoctorSimpleTextFormatter {

    public String format(String origin, AsciiDoctorFormatterConfig config) {
        if (origin == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        State state = State.INIT;
        StringBuilder current = new StringBuilder();
        char[] array = origin.toCharArray();
        int max = array.length;
        for (int i = 0; i < max; i++) {
            char c = array[i];
            if (state == State.NEWLINE) {
                sb.append(current);
                current = new StringBuilder();
                state = State.INIT;
            }
            current.append(c);
            if (c == '\n') {
                state = State.NEWLINE;
                continue;
            }
            if (state == State.LINE_COMMENT) {
                continue;
            }
            String string = current.toString();
            if (string.trim().startsWith("//")) {
                state = State.LINE_COMMENT;
                continue;
            }
            if (string.length() == config.maxColumn) {
                int next = i + 1;
                boolean charIsWhitespace = Character.isWhitespace(c);
                boolean nextCharIsWhitespace = charIsWhitespace || next <= max && Character.isWhitespace(array[next]);
                if (nextCharIsWhitespace) {
                    current.append("\n");
                    state = State.NEWLINE;
                } else {
                    /* we must go back... */
                    String allowedText = "";
                    for (int m = string.length() - 1; m > 0; m--) {
                        char charAt = string.charAt(m);
                        if (Character.isWhitespace(charAt)) {
                            if (string.length() > m + 1) {
                                allowedText = string.substring(0, m + 1);
                            } else {
                                allowedText = string.substring(0, m);
                            }
                            break;
                        }
                    }
                    sb.append(allowedText);
                    sb.append("\n");

                    current = new StringBuilder();
                    String newOne = string.substring(allowedText.length());
                    current.append(newOne);
                    state = State.SIMPLE_TEXT;
                }
                continue;
            }
        }
        if (current.length() > 0) {
            sb.append(current);
        }
        return sb.toString().trim();
    }

    private enum State {

        INIT,

        NEWLINE,

        SIMPLE_TEXT,

        LINE_COMMENT,

        MULTI_LINE_COMMENT,

        BLOCK,

        HEADLINE,

    }

}
