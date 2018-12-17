package de.jcup.asciidoctoreditor.provider;

import org.asciidoctor.Asciidoctor;

public interface AsciiDoctorInstanceProvider {

    Asciidoctor getAsciiDoctor(boolean installed);

}