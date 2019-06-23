package de.jcup.asciidoctoreditor.codeassist;

public interface EnableStateResolver {

    default boolean isDisabled() {
        return !isEnabled();
    }

    boolean isEnabled();

}
