package de.jcup.asciidoctoreditor.script;

public interface AsciiDoctorScriptModelBuilder {

    /**
     * Parses given script and creates a asciidoc file model
     * 
     * @param asciidoctorScript
     * @return a simple model with some information about asciidoc file
     * @throws AsciiDoctorScriptModelException
     */
    AsciiDoctorScriptModel build(String asciidoctorScript) throws AsciiDoctorScriptModelException;

}