package de.jcup.asciidoctoreditor.search;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

public class FileTableContentProvider implements IStructuredContentProvider {

	private final Object[] EMPTY_ARR= new Object[0];

	private AsciidocSearchResultPage fPage;
	private AbstractTextSearchResult fResult;

	public FileTableContentProvider(AsciidocSearchResultPage page) {
		fPage= page;
	}

    @Override
    public Object[] getElements(Object inputElement) {
        return EMPTY_ARR;
    }

}