package de.jcup.asciidoctoreditor.codeassist;

import java.io.File;

import org.eclipse.ui.IEditorPart;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.eclipse.commons.ui.EclipseUtil;

/**
 * Uses always editor file parent as base parent file
 * @author albert
 *
 */
public class DiagramBaseParentResolver implements BaseParentDirResolver{

    @Override
    public File getBaseParentDir(File editorFile) {
        
        IEditorPart editor = EclipseUtil.getActiveEditor();
        if (editor instanceof AsciiDoctorEditor) {
            return getBaseDirFromEditor(editorFile,(AsciiDoctorEditor)editor);
        }
        return null;
    }

    private File getBaseDirFromEditor(File editorFile, AsciiDoctorEditor editor) {
        String path = editor.getDiagramPathOrNull();
        if (path==null) {
            /* no diagram dir set, so fallback to current folder*/
            return editorFile.getParentFile();
        }
        return new File(path);
    }

}
