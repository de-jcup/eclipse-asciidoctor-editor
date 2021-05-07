package de.jcup.asciidoctoreditor.preview;

import org.eclipse.core.runtime.jobs.Job;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDoctorBackendType;

public class AsciidocBuildAndPreviewJobFactory {
    
    private AsciiDoctorEditor editor;
    
    AsciidocBuildAndPreviewJobFactory(AsciiDoctorEditor editor){
        this.editor=editor;
    }
   
    public Job createBuildAndPreviewJob(AsciiDoctorBackendType backend, boolean internalPreview, BuildDoneListener buildDoneListener, String name) {
     
        AsciidocEditorPreviewBuildRunnnable runnable = new AsciidocEditorPreviewBuildRunnnable();
        runnable.editor=editor;
        runnable.buildDoneListener=buildDoneListener;
        runnable.backend=backend;
        runnable.internalPreview = internalPreview;
       
        return Job.create(name, runnable);
    }


}
