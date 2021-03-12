package de.jcup.asciidoctoreditor.preview;

import java.util.regex.Pattern;

import org.eclipse.core.runtime.jobs.Job;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDoctorBackendType;

public class AsciidocBuildAndPreviewJobFactory {
    
    private AbsolutePathPatternFactory patternFactory = new AbsolutePathPatternFactory();
    private AsciiDoctorEditor editor;
    private Pattern tempFolderPattern;
    
    AsciidocBuildAndPreviewJobFactory(AsciiDoctorEditor editor){
        this.editor=editor;
        this.tempFolderPattern= patternFactory.createRemoveAbsolutePathToTempFolderPattern(editor.getWrapper());
    }
    
    public Job createBuildAndPreviewJob(AsciiDoctorBackendType backend, boolean internalPreview, BuildDoneListener buildDoneListener, String name) {
        
        AsciidocEditorPreviewBuildRunnnable runnable = new AsciidocEditorPreviewBuildRunnnable();
        runnable.buildDoneListener=buildDoneListener;
        runnable.backend=backend;
        runnable.internalPreview = internalPreview;
        runnable.editor=editor;
        runnable.tempFolderPattern=tempFolderPattern;
        
        return Job.create(name, runnable);
    }


}
