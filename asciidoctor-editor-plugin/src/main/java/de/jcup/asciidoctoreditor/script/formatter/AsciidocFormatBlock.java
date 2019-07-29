package de.jcup.asciidoctoreditor.script.formatter;

public class AsciidocFormatBlock {
    StringBuilder source = new StringBuilder();
    AsciidocBlockType blockType;


    @Override
    public String toString() {
        return (blockType!=null ? blockType.name(): "null" ) + ":"+source.toString();
    }
}
