package de.jcup.asciidoctoreditor.codeassist;

public class AsciidocIncludeExistingTextCalculator {
    
    /**
     * Resolves full include texzt
     * @param fullSource
     * @param posBeforeCtrlSpace - is index+1...
     * @return full include text or <code>null</code> when not available
     */
    public String resolveIncludeTextOrNull(String fullSource, int posBeforeCtrlSpace) {
        int index = posBeforeCtrlSpace-1;
        if (fullSource.length()<=index) {
            index = fullSource.length()-1;
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