package de.jcup.asciidoctoreditor.search;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.Match;

public class FileTreeContentProvider implements ITreeContentProvider {

    private final Object[] EMPTY_ARR = new Object[0];

    private AbstractTextSearchResult result;
    private AsciidocSearchResultPage page;
    private AbstractTreeViewer viewer;

    FileTreeContentProvider(AsciidocSearchResultPage page, AbstractTreeViewer viewer) {
        this.page = page;
        this.viewer = viewer;
    }
    
    

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof AsciidocSearchResult) {
            AsciidocSearchResult result = (AsciidocSearchResult) inputElement;
            inputElement = result.getModel();
        } else if (inputElement instanceof Match) {
            Match match = (Match) inputElement;
            inputElement = match.getElement();
        }
        if (inputElement instanceof AsciidocSearchResultElement) {
            AsciidocSearchResultElement element = (AsciidocSearchResultElement) inputElement;
            return element.getChildren();
        }
        return EMPTY_ARR;
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof AsciidocSearchResult) {
            AsciidocSearchResult result = (AsciidocSearchResult) parentElement;
            parentElement = result.getModel();
        } else if (parentElement instanceof Match) {
            Match match = (Match) parentElement;
            parentElement = match.getElement();
        }
        if (parentElement instanceof AsciidocSearchResultElement) {
            AsciidocSearchResultElement element = (AsciidocSearchResultElement) parentElement;
            return element.getChildren();
        }
        return EMPTY_ARR;
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof AsciidocSearchResultElement) {
            AsciidocSearchResultElement e = (AsciidocSearchResultElement) element;
            return e.getParent();
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

}