package de.jcup.asciidoctoreditor.script;

/**
 * Implementation informa about details for text selection in asciidoctor documents
 * @author albert
 *
 */
public interface AsciidoctorTextSelectable {

    /**
     * @return start position
     */
    public int getSelectionStart();
    
    /**
     * 
     * @return length of selection (starting from position)
     */
    public int getSelectionLength();

    /**
     * @return real offset (not for selection, but for element itself inside document) - can differ to selection start but must not
     */
    public int getPosition();


}
