package de.jcup.asciidoctoreditor.codeassist;

public class AsciidocIncludeExistingTextCalculator {
    
    public String resolveIncludeTextOrNull(String fullSource, int indexForCtrlSpace) {
        int index = indexForCtrlSpace;
        if (fullSource.length()<=index) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        while (index>=0) {
            char c= fullSource.charAt(index--);
            if (Character.isWhitespace(c)) {
                break;
            }
            sb.insert(0, c);
        }
        String inspected = sb.toString();
        if (inspected.startsWith("include::")) {
            return inspected;
        }
        return null;
        
    }
    
}