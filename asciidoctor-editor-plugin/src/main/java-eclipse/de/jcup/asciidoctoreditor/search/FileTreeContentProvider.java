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
package de.jcup.asciidoctoreditor.search;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.Match;

public class FileTreeContentProvider implements ITreeContentProvider {

    private final Object[] EMPTY_ARR = new Object[0];

    protected AbstractTextSearchResult result;
    protected AsciidocSearchResultPage page;
    protected AbstractTreeViewer viewer;

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