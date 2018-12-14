package de.jcup.asciidoctoreditor.provider;

import org.asciidoctor.Asciidoctor;

public interface AsciiDoctorProvider {

    Asciidoctor getAsciiDoctor(boolean installed);

}