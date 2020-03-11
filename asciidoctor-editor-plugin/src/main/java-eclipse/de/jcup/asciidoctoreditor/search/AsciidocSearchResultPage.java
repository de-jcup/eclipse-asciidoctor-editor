package de.jcup.asciidoctoreditor.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import de.jcup.asciidoctoreditor.search.AsciidocSearchResultModel.ResourceElement;
import de.jcup.asciidoctoreditor.search.AsciidocSearchResultModel.ResourceElement.ResourceLineElement;
import de.jcup.asciidoctoreditor.search.AsciidocSearchResultModel.ResourceElement.ResourceLineElement.ResourceLineContentElement;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class AsciidocSearchResultPage extends AbstractTextSearchViewPage implements ISearchResultPage {

    public AsciidocSearchResultPage() {
    }

    @Override
    protected void elementsChanged(Object[] objects) {
        getViewer().refresh();
    }

    @Override
    protected void clear() {
        getViewer().refresh();

    }

    @Override
    protected StructuredViewer getViewer() {
        return super.getViewer();
    }

    @Override
    protected void configureTreeViewer(TreeViewer viewer) {
        viewer.setLabelProvider(new AsciidocSearchResultLabelProvider());
        viewer.setContentProvider(new FileTreeContentProvider(this, viewer));
        viewer.addDoubleClickListener(new AsciidocSearchResultElementDoubleClickListener());

    }

    @Override
    protected void configureTableViewer(TableViewer viewer) {
        viewer.setLabelProvider(new AsciidocSearchResultLabelProvider());
        viewer.setContentProvider(new FileTableContentProvider(this));
        viewer.addDoubleClickListener(new AsciidocSearchResultElementDoubleClickListener());
    }

    private class AsciidocSearchResultElementDoubleClickListener implements IDoubleClickListener {

        private class SelectionContext {
            int start = 0;
            int length = 0;
            boolean selectionDone = false;

            public void selectClickTarget(ResourceLineContentElement rlce) {

                selectionDone = true;

                start = rlce.getOffset();
                length = rlce.getText().length();
            }

            public void useFirstLineElementChildWhenNotAlreadySelected(ResourceLineElement element) {
                if (selectionDone) {
                    return;
                }
                ResourceLineElement line = (ResourceLineElement) element;
                Object[] children = line.getChildren();
                if (children == null || children.length == 0) {
                    return;
                }
                Object child = children[0];
                if (child instanceof ResourceLineContentElement) {
                    ResourceLineContentElement rlce = (ResourceLineContentElement) children[0];
                    selectClickTarget(rlce);
                }
            }

            public void useFirstResourceElementChildrenWhenNotAlreadySelected(ResourceElement re) {
                if (selectionDone) {
                    return;
                }
                Object[] children = re.getChildren();
                if (children == null || children.length == 0) {
                    return;
                }
                Object child = children[0];
                if (child instanceof ResourceLineElement) {
                    useFirstLineElementChildWhenNotAlreadySelected((ResourceLineElement) child);
                }

            }
        }

        @Override
        public void doubleClick(DoubleClickEvent event) {
            ISelection selection = getViewer().getSelection();
            if (!(selection instanceof IStructuredSelection)) {
                return;
            }
            IStructuredSelection iss = (IStructuredSelection) selection;
            Object first = iss.getFirstElement();
            if (!(first instanceof AsciidocSearchResultElement)) {
                return;
            }
            SelectionContext context = new SelectionContext();
            AsciidocSearchResultElement element = (AsciidocSearchResultElement) first;
            if (element instanceof ResourceLineContentElement) {
                context.selectClickTarget((ResourceLineContentElement) first);
                element = element.getParent();
            }
            if (element instanceof ResourceLineElement) {
                context.useFirstLineElementChildWhenNotAlreadySelected((ResourceLineElement) element);
                element = element.getParent();
            }
            if (element instanceof ResourceElement) {
                ResourceElement re = (ResourceElement) element;
                context.useFirstResourceElementChildrenWhenNotAlreadySelected(re);

                IResource resource = re.getResource();
                if (resource instanceof IFile) {
                    IFile file = (IFile) resource;
                    try {
                        IWorkbenchPage page = EclipseUtil.getActivePage();
                        IEditorPart editor = IDE.openEditor(page, file);
                        if (editor instanceof AbstractTextEditor) {
                            AbstractTextEditor ae = (AbstractTextEditor) editor;
                            ae.selectAndReveal(context.start, context.length);
                        }
                    } catch (PartInitException e) {
                        AsciiDoctorEditorUtil.logError("Was not able to open editor file of:" + file, e);
                    }
                }
            }
        }
    }

}
