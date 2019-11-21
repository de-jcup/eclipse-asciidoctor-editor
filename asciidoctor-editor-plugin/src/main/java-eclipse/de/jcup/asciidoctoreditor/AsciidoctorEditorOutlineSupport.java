/*
 * Copyright 2018 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor;

import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorValidationPreferenceConstants.*;
import static de.jcup.asciidoctoreditor.util.EclipseUtil.*;

import java.io.File;
import java.util.Collection;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;

import de.jcup.asciidoctoreditor.outline.AsciiDoctorContentOutlinePage;
import de.jcup.asciidoctoreditor.outline.AsciiDoctorEditorTreeContentProvider;
import de.jcup.asciidoctoreditor.outline.AsciiDoctorQuickOutlineDialog;
import de.jcup.asciidoctoreditor.outline.Item;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModel;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModelBuilder;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModelException;
import de.jcup.asciidoctoreditor.script.AsciiDoctorFileReferenceValidator;
import de.jcup.asciidoctoreditor.script.AsciiDoctorMarker;
import de.jcup.asciidoctoreditor.script.parser.validator.AsciiDoctorEditorValidationErrorLevel;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;

public class AsciidoctorEditorOutlineSupport extends AbstractAsciiDoctorEditorSupport {
    private static final AsciiDoctorScriptModel FALLBACK_MODEL = new AsciiDoctorScriptModel();

    private Object monitor = new Object();
    private boolean quickOutlineOpened;
    private AsciiDoctorScriptModelBuilder modelBuilder;
    boolean ignoreNextCaretMove;

    private AsciiDoctorContentOutlinePage outlinePage;

    public AsciidoctorEditorOutlineSupport(AsciiDoctorEditor editor) {
        super(editor);
        this.modelBuilder = new AsciiDoctorScriptModelBuilder();
    }

    /**
     * Opens quick outline
     */
    public void openQuickOutline() {
        synchronized (monitor) {
            if (quickOutlineOpened) {
                /*
                 * already opened - this is in future the anker point for ctrl+o+o...
                 */
                return;
            }
            quickOutlineOpened = true;
        }
        Shell shell = getEditor().getEditorSite().getShell();
        AsciiDoctorScriptModel model = buildModelWithoutValidation();
        AsciiDoctorQuickOutlineDialog dialog = new AsciiDoctorQuickOutlineDialog(getEditor(), shell, "Quick outline");
        dialog.setInput(model);

        dialog.open();
        synchronized (monitor) {
            quickOutlineOpened = false;
        }
    }

    public void openSelectedTreeItemInEditor(ISelection selection, boolean grabFocus) {
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection ss = (IStructuredSelection) selection;
            Object firstElement = ss.getFirstElement();
            if (firstElement instanceof Item) {
                openSelectedItemInEditor(grabFocus, firstElement);
            }
        }
    }

    private void openSelectedItemInEditor(boolean grabFocus, Object firstElement) {
        Item item = (Item) firstElement;
        int offset = item.getOffset();
        int length = item.getLength();
        if (length == 0) {
            /* fall back */
            length = 1;
        }
        /*
         * ignore next caret move - to prevent endless loop between tree and
         * getEditor()...
         */
        ignoreNextCaretMove = true;
        getEditor().selectAndReveal(offset, length);
        if (grabFocus) {
            getEditor().setFocus();
        }
        /*
         * caret movement was ignored for tree so call synchronizer alone here:
         */
        getEditor().synchronizer.onEditorCaretMoved(offset);
    }

    /**
     * Does rebuild the outline - this is done asynchronous
     */
    public void rebuildOutlineAndValidate() {

        String text = getEditor().getDocumentText();
        if (text==null) {
            return;
        }

        IPreferenceStore store = getEditor().getPreferences().getPreferenceStore();

        String errorLevelId = store.getString(VALIDATE_ERROR_LEVEL.getId());
        AsciiDoctorEditorValidationErrorLevel errorLevel = AsciiDoctorEditorValidationErrorLevel.fromId(errorLevelId);

        safeAsyncExec(() -> {
            AsciiDoctorScriptModel model;
            try {
                model = modelBuilder.build(text);
            } catch (AsciiDoctorScriptModelException e) {
                AsciiDoctorEditorUtil.logError("Was not able to build validation model", e);
                model = FALLBACK_MODEL;
            }
            validate(model);

            getOutlinePage().rebuild(model);

            if (model.hasErrors()) {
                int severity;
                if (AsciiDoctorEditorValidationErrorLevel.INFO.equals(errorLevel)) {
                    severity = IMarker.SEVERITY_INFO;
                } else if (AsciiDoctorEditorValidationErrorLevel.WARNING.equals(errorLevel)) {
                    severity = IMarker.SEVERITY_WARNING;
                } else {
                    severity = IMarker.SEVERITY_ERROR;
                }
                getEditor().addErrorMarkers(model, severity);
            }
        });
    }

    private void validate(AsciiDoctorScriptModel model) {
        AsciiDoctorFileReferenceValidator referenceValidator = new AsciiDoctorFileReferenceValidator(getEditor().getImagesPathOrNull());
        AsciiDoctorEditorPreferences preferences = AsciiDoctorEditorPreferences.getInstance();

        File editorFileOrNull = getEditor().getEditorFileOrNull();
        Collection<AsciiDoctorMarker> errors = model.getErrors();

        if (preferences.isIncludeValidationEnabled()) {
            referenceValidator.validate(editorFileOrNull, model.getIncludes(), errors);
        }

        if (preferences.isDiagramValidationEnabled()) {
            String diagramPath = getEditor().getDiagramPathOrNull();
            if (diagramPath != null) {
                File imagesFolder = new File(diagramPath);
                referenceValidator.validate(imagesFolder, model.getDiagrams(), errors);
            }
        }

        if (preferences.isImageValidationEnabled()) {
            String imagesPath = getEditor().getImagesPathOrNull();
            if (imagesPath != null) {
                File imagesFolder = new File(imagesPath);
                referenceValidator.validate(imagesFolder, model.getImages(), errors);
            }
        }

    }

    AsciiDoctorScriptModel buildModelWithoutValidation() {
        String text = getEditor().getDocumentText();

        AsciiDoctorScriptModel model;
        try {
            model = modelBuilder.build(text);
        } catch (AsciiDoctorScriptModelException e) {
            AsciiDoctorEditorUtil.logError("Was not able to build script model", e);
            model = FALLBACK_MODEL;
        }
        return model;
    }

    /**
     * @return outline page, never <code>null</code>. If non exists a new one will
     *         be created
     */
    public AsciiDoctorContentOutlinePage getOutlinePage() {
        if (outlinePage == null) {
            outlinePage = new AsciiDoctorContentOutlinePage(getEditor());
        }
        return outlinePage;
    }

    public void selectItemPointingTo(int position) {
        if (outlinePage == null) {
            return;
        }
        AsciiDoctorEditorTreeContentProvider contentProvider = getOutlinePage().getContentProvider();
        Item item = contentProvider.tryToFindByOffset(position);
        if (item == null) {
            return;
        }
        getOutlinePage().setSelection(new StructuredSelection(item));
    }
}
