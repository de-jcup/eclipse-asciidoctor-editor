package de.jcup.asciidoctoreditor.search;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.search.ui.text.Match;

public class FileTableContentProvider implements IStructuredContentProvider {

	private final Object[] EMPTY_ARR= new Object[0];


	public FileTableContentProvider(AsciidocSearchResultPage page) {
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
        if (inputElement instanceof AsciidocSearchResultModel) {
            AsciidocSearchResultModel model = (AsciidocSearchResultModel) inputElement;
            return model.getFlatResourceElements();
        }
        if (inputElement instanceof AsciidocSearchResultElement) {
            AsciidocSearchResultElement element = (AsciidocSearchResultElement) inputElement;
            return element.getChildren();
        }
        return EMPTY_ARR;
    }

}