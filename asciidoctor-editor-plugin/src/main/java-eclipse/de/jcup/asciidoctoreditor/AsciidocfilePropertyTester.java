package de.jcup.asciidoctoreditor;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;

import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;

public class AsciidocfilePropertyTester extends PropertyTester {

    private static final String IS_ASCIIDOC_FILE = "isAsciidocFile";

    /* we use same setup as done inside  content-type="de.jcup.asciidoctoreditor.content.asciidoc", means:
     * asciidoc,adoc,asc,ad
     */
    public AsciidocfilePropertyTester() {
    }

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (!(receiver instanceof IFile)) {
            /* not supported */
            return false;
        }
        IFile file = (IFile) receiver;
        if (IS_ASCIIDOC_FILE.contentEquals(property)) {
            return testIsAsciidocfile(file);
        }
        return false;
    }

    private boolean testIsAsciidocfile(IFile file) {
       String extension = file.getFileExtension();
       return AsciiDoctorEditorUtil.isAsciidocFileExtension(extension);
    }

    

}
