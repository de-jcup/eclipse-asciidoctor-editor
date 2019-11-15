package de.jcup.asciidoctoreditor.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
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

import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;
import de.jcup.asciidoctoreditor.search.AsciidocSearchResultModel.ResourceElement;
import de.jcup.asciidoctoreditor.search.AsciidocSearchResultModel.ResourceElement.ResourceLineElement;
import de.jcup.asciidoctoreditor.search.AsciidocSearchResultModel.ResourceElement.ResourceLineElement.ResourceLineContentElement;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class AsciidocSearchResultPage extends AbstractTextSearchViewPage implements ISearchResultPage {

    public AsciidocSearchResultPage(){
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
        viewer.setContentProvider(new FileTreeContentProvider(this,viewer));
        viewer.addDoubleClickListener(new IDoubleClickListener() {
            
            @Override
            public void doubleClick(DoubleClickEvent event) {
                ISelection selection = getViewer().getSelection();
                if (selection instanceof IStructuredSelection) {
                    IStructuredSelection iss = (IStructuredSelection) selection;
                    Object first = iss.getFirstElement();
                    if (! (first instanceof AsciidocSearchResultElement)) {
                        return;
                    }
                    int start=0;
                    int length =0;
                    AsciidocSearchResultElement element = (AsciidocSearchResultElement) first;
                    if (element instanceof ResourceLineContentElement) {
                        ResourceLineContentElement rlce = (ResourceLineContentElement) first;
                        element = rlce.getParent();
                        start  =rlce.getOffset();
                        length = rlce.getText().length();
                    }
                    if (element instanceof ResourceLineElement) {
                        element = element.getParent();
                    }
                    if (element instanceof ResourceElement) {
                        ResourceElement re = (ResourceElement) element;
                        IResource resource = re.getResource();
                        if (resource instanceof IFile) {
                            IFile file = (IFile) resource;
                            try {
                                IWorkbenchPage page = EclipseUtil.getActivePage();
                                IEditorPart editor = IDE.openEditor(page, file);
                                if (editor instanceof AbstractTextEditor) {
                                    AbstractTextEditor ae = (AbstractTextEditor) editor;
                                    ae.selectAndReveal(start, length);
                                }
                            } catch (PartInitException e) {
                                AsciiDoctorEditorUtil.logError("Was not able to open editor file of:"+file,  e);
                            }
                        }
                    }
                }
            }
        });

    }

    @Override
    protected void configureTableViewer(TableViewer viewer) {
        viewer.setLabelProvider(new AsciidocSearchResultLabelProvider());
        viewer.setContentProvider(new FileTableContentProvider(this));

    }
    

}
