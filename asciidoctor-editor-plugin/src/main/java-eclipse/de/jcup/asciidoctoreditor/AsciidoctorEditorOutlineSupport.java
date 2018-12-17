package de.jcup.asciidoctoreditor;

import static de.jcup.asciidoctoreditor.EclipseUtil.*;
import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorValidationPreferenceConstants.*;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;

import de.jcup.asciidoctoreditor.outline.AsciiDoctorContentOutlinePage;
import de.jcup.asciidoctoreditor.outline.AsciiDoctorQuickOutlineDialog;
import de.jcup.asciidoctoreditor.outline.Item;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModel;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModelBuilder;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModelException;
import de.jcup.asciidoctoreditor.script.parser.validator.AsciiDoctorEditorValidationErrorLevel;

public class AsciidoctorEditorOutlineSupport extends AbstractAsciiDoctorEditorSupport{
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
                 * already opened - this is in future the anker point for
                 * ctrl+o+o...
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
                Item item = (Item) firstElement;
                int offset = item.getOffset();
                int length = item.getLength();
                if (length == 0) {
                    /* fall back */
                    length = 1;
                }
                /*
                 * ignore next caret move - to prevent endless loop between tree
                 * and getEditor()...
                 */
                ignoreNextCaretMove = true;
                getEditor().selectAndReveal(offset, length);
                if (grabFocus) {
                    getEditor().setFocus();
                }
                /*
                 * caret movement was ignored for tree so call synchronizer
                 * alone here:
                 */
                getEditor().synchronizer.onEditorCaretMoved(offset);
            }
        }
    }

    /**
     * Does rebuild the outline - this is done asynchronous
     */
    public void rebuildOutline() {

        String text = getEditor().getDocumentText();

        IPreferenceStore store = getEditor().getPreferences().getPreferenceStore();

        boolean validateGraphviz = store.getBoolean(VALIDATE_GRAPHVIZ.getId());
        String errorLevelId = store.getString(VALIDATE_ERROR_LEVEL.getId());
        AsciiDoctorEditorValidationErrorLevel errorLevel = AsciiDoctorEditorValidationErrorLevel.fromId(errorLevelId);

        if (validateGraphviz) {
            modelBuilder.setGraphVizCheckSupport(CheckGraphviz.INSTANCE);
        } else {
            modelBuilder.setGraphVizCheckSupport(null);
        }
        safeAsyncExec(() -> {
            AsciiDoctorScriptModel model;
            try {
                model = modelBuilder.build(text);
            } catch (AsciiDoctorScriptModelException e) {
                AsciiDoctorEditorUtil.logError("Was not able to build validation model", e);
                model = FALLBACK_MODEL;
            }

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

    public AsciiDoctorContentOutlinePage getOutlinePage() {
        if (outlinePage == null) {
            outlinePage = new AsciiDoctorContentOutlinePage(getEditor());
        }
        return outlinePage;
    }
}
