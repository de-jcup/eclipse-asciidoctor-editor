package de.jcup.asciidoctoreditor.provider;

public abstract class AbstractAsciiDoctorProvider {

    private AsciiDoctorProviderContext context;

    AbstractAsciiDoctorProvider(AsciiDoctorProviderContext context){
        if (context==null ){
            throw new IllegalArgumentException("context may never be null!");
        }
        this.context=context;
    }
    
    AsciiDoctorProviderContext getContext() {
        return context;
    }

    protected abstract void reset() ;
}
