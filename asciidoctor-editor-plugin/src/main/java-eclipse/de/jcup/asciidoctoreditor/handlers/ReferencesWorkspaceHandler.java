/*
 * Copyright 2020 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.asciidoctoreditor.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDoctorWrapper;
import de.jcup.asciidoctoreditor.outline.Item;
import de.jcup.asciidoctoreditor.outline.ItemType;
import de.jcup.asciidoctoreditor.provider.AsciiDoctorProviderContext;
import de.jcup.asciidoctoreditor.search.FindAsciidocfileReferencesQuery;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;
import de.jcup.eclipse.commons.EclipseResourceHelper;

public class ReferencesWorkspaceHandler extends AbstractHandler {

    private static final Object NO_RESULT = null;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        internalExecute(event);
        return NO_RESULT;

    }

    private void internalExecute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection) {
            handleSelectionAction(selection);
            return;
        }
        if (!(selection instanceof ITextSelection)) {
            handleEditorAction();
            return;
        }

        ITextSelection textSelection = (ITextSelection) selection;
        String text = textSelection.getText();
        if (text == null || text.trim().isEmpty()) {
            handleEditorAction();
            return;
        }

        IEditorPart editor = HandlerUtil.getActiveEditor(event);
        if (!(editor instanceof AsciiDoctorEditor)) {
            return;
        }
        /* asciidoctor editor found */
        AsciiDoctorEditor asciidocEditor = (AsciiDoctorEditor) editor;
        Item found = asciidocEditor.getOutlineSupport().getOutlinePage().getContentProvider().tryToFindByOffset(((ITextSelection) selection).getOffset());
        if (found == null) {
            return;
        }
        if (ItemType.INCLUDE.equals(found.getItemType())) {
            handleInclude(asciidocEditor, found);
            return;
        }

    }

    private void handleInclude(AsciiDoctorEditor editor, Item found) throws ExecutionException {
        String filePath = found.getFilePathOrNull();
        if (filePath == null) {
            return;
        }
        AsciiDoctorWrapper wrapper = editor.getWrapper();
        if (wrapper==null) {
            return;
        }
        AsciiDoctorProviderContext context = wrapper.getContext();
        if (context==null) {
            return;
        }
        File baseDir = context.getBaseDir();
        if (baseDir==null) {
            return;
        }
        File file = new File(baseDir, filePath);
        triggerSearchFor(file);
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
        if (editorFile == null) {
            return;
        }
        NewSearchUI.runQueryInBackground(new FindAsciidocfileReferencesQuery(editorFile));
    }

}