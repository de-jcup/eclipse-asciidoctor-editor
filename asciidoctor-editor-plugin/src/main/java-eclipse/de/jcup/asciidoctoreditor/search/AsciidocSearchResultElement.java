package de.jcup.asciidoctoreditor.search;

public interface AsciidocSearchResultElement {

    /**
     * @return parent element or <code>null</code>
     */
    public AsciidocSearchResultElement getParent();
    
    public Object[] getChildren();
}
