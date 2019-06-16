package de.jcup.asciidoctoreditor.script;

import java.io.File;

public class AsciiDoctorScriptModelIncludeValidator {

    private static final String INCLUDE = "include::";

    public void validate(AsciiDoctorScriptModel model, File editorFile) {
        if (editorFile==null) {
            return;
        }
        for (AsciiDoctorInclude include: model.getIncludes()) {
            String target = include.getTarget();
            if (target.startsWith(INCLUDE)) {
                target=target.substring(INCLUDE.length());
            }
            File file = new File(editorFile.getParentFile().getAbsolutePath()+File.separatorChar+target);
            if (! file.exists()) {
                AsciiDoctorMarker marker = new AsciiDoctorMarker(include.getPosition(), include.getEnd(), "Include failure, file not found:"+file.getAbsolutePath());
                model.getErrors().add(marker);
            }else if (file.canExecute() && file.isDirectory()) {
                AsciiDoctorMarker marker = new AsciiDoctorMarker(include.getPosition(), include.getEnd(), "Include points to a directory not a file:"+file.getAbsolutePath());
                model.getErrors().add(marker);
            }
        }
    }
}
