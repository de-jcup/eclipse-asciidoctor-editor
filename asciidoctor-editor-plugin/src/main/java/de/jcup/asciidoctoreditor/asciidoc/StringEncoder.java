package de.jcup.asciidoctoreditor.asciidoc;

public interface StringEncoder {

    /**
     * Encodes given string
     * @param string
     * @return encoded string - or <code>null</code> if given string was <code>null</code>
     */
    String encode(String string);

}