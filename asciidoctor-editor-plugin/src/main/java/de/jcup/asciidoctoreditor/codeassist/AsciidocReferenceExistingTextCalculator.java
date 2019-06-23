package de.jcup.asciidoctoreditor.codeassist;

import java.util.Objects;

public class AsciidocReferenceExistingTextCalculator {

    private String prefix;

    /**
     * Creates calculator
     * @param prefix - prefix to use for determination (e.g. "include::" for includes
     */
    public AsciidocReferenceExistingTextCalculator(String prefix) {
        Objects.requireNonNull(prefix);
        this.prefix=prefix;
    }
    
    /**
     * Resolves full include text
     * @param fullSource
     * @param posBeforeCtrlSpace - is index+1...
     * @return full reference text or <code>null</code> when not available
     */
    public String resolveReferenceTextOrNull(String fullSource, int posBeforeCtrlSpace) {
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
        if (inspected.startsWith(getPrefix())) {
            return inspected;
        }
        return null;
        
    }
    
    public String getPrefix() {
        return prefix;
    }

}