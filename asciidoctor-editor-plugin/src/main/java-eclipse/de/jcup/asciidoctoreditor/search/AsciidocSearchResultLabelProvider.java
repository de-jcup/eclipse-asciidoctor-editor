package de.jcup.asciidoctoreditor.search;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import de.jcup.asciidoctoreditor.search.AsciidocSearchResultModel.ResourceElement;
import de.jcup.asciidoctoreditor.search.AsciidocSearchResultModel.ResourceElement.ResourceLineElement;
import de.jcup.asciidoctoreditor.search.AsciidocSearchResultModel.ResourceElement.ResourceLineElement.ResourceLineContentElement;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;

public class AsciidocSearchResultLabelProvider extends BaseLabelProvider implements IStyledLabelProvider, IColorProvider,ITableLabelProvider, ILabelProvider{

    @Override
    public Color getForeground(Object element) {
        return null;
    }

    @Override
    public Color getBackground(Object element) {
        return null;
    }

    @Override
    public StyledString getStyledText(Object element) {
        StyledString styled = new StyledString();
        if (element instanceof ResourceElement) {
            ResourceElement re = (ResourceElement) element;
            styled.append(re.getLabel());
        }else if (element instanceof ResourceLineElement) {
            ResourceLineElement rle = (ResourceLineElement)element;
            styled.append("Line:"+rle.getLineNumber());
        }else if (element instanceof ResourceLineContentElement) {
            ResourceLineContentElement rle = (ResourceLineContentElement)element;
            styled.append(rle.getText());
        }else {
            styled.append("->"+element);
        }
        return styled;
    }
    
    @Override
    public String getText(Object element) {
        StringBuilder styled = new StringBuilder();
        if (element instanceof ResourceElement) {
            ResourceElement re = (ResourceElement) element;
            styled.append(re.getLabel());
        }else if (element instanceof ResourceLineElement) {
            ResourceLineElement rle = (ResourceLineElement)element;
            styled.append("Line:"+rle.getLineNumber());
        }else if (element instanceof ResourceLineContentElement) {
            ResourceLineContentElement rle = (ResourceLineContentElement)element;
            styled.append(rle.getText());
        }else {
            styled.append("->"+element);
        }
        return styled.toString();
    }

    @Override
    public Image getImage(Object element) {
        return null;
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        return "xxx-.."+element;
    }

   

}
