package de.jcup.asciidoctoreditor.provider;

import org.asciidoctor.Asciidoctor;

import de.jcup.asciidoctoreditor.AsciiDoctorOSGIWrapper;
import de.jcup.asciidoctoreditor.InstalledAsciidoctor;

public class DefaultAsciidoctorProvider implements AsciiDoctorProvider{
    private static Asciidoctor asciidoctorInstalled;
    private static Asciidoctor asciidoctorEmbedded;
    
    public DefaultAsciidoctorProvider(){
        asciidoctorInstalled = new InstalledAsciidoctor();
        asciidoctorEmbedded = AsciiDoctorOSGIWrapper.INSTANCE.getAsciidoctor();
    }
    
    @Override
    public Asciidoctor getAsciiDoctor(boolean installed){
        if (installed){
            return asciidoctorInstalled;
        }else{
            return asciidoctorEmbedded;
        }
    }
}