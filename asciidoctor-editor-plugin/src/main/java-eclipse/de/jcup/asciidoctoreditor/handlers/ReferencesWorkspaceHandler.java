package de.jcup.asciidoctoreditor.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.handlers.HandlerUtil;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.search.FindAsciidocfileReferencesQuery;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;
import de.jcup.eclipse.commons.EclipseResourceHelper;

public class ReferencesWorkspaceHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection) {
            handleSelectionAction(selection);
        } else {
            handleEditorAction();
        }

        return null;
    }

    private void handleEditorAction() throws ExecutionException {
        AsciiDoctorEditor editor = AsciiDoctorEditorUtil.findActiveAsciidoctorEditorOrNull();
        if (editor == null) {
            return;
        }
        File editorFile = editor.getEditorFileOrNull();
        triggerSearchFor(editorFile);
        
       
    }


    private void handleSelectionAction(ISelection selection) throws ExecutionException {
        IStructuredSelection ssel = (IStructuredSelection) selection;
        Object first = ssel.getFirstElement();
        if (first instanceof IFile) {
            IFile file = (IFile) first;
            try {
                triggerSearchFor(EclipseResourceHelper.DEFAULT.toFile(file));
            } catch (CoreException e) {
                throw new ExecutionException("Was not able to search for selectedFile:" + file, e);
            }
        }
    }
    private void triggerSearchFor(File editorFile) {
        NewSearchUI.runQueryInBackground(new FindAsciidocfileReferencesQuery(editorFile));
    }

}