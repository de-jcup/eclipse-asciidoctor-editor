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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;
import de.jcup.asciidoctoreditor.search.AsciidocSearchResultModel.ResourceElement;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class AsciidocSearchResult extends AbstractTextSearchResult implements IEditorMatchAdapter, IFileMatchAdapter {

    private static final ImageDescriptor IMAGE_DESCRIPTOR = EclipseUtil.createImageDescriptor("/icons/asciidoctor-editor.png", AsciiDoctorEditorActivator.PLUGIN_ID);
    private ISearchQuery query;
    private AsciidocSearchResultModel model;

    AsciidocSearchResult(ISearchQuery query) {
        this.query = query;
        this.model = new AsciidocSearchResultModel();
    }

    public AsciidocSearchResultModel getModel() {
        return model;
    }

    @Override
    public String getLabel() {
        return query.getLabel();
    }

    @Override
    public String getTooltip() {
        return getLabel();
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return IMAGE_DESCRIPTOR;
    }

    @Override
    public ISearchQuery getQuery() {
        return query;
    }

    @Override
    public IEditorMatchAdapter getEditorMatchAdapter() {
        return this;
    }

    @Override
    public IFileMatchAdapter getFileMatchAdapter() {
        return this;
    }

    /*
     * --- File matcher ----
     */
    @Override
    public Match[] computeContainedMatches(AbstractTextSearchResult result, IFile file) {
        return getMatchesForFile(file);
    }

    private Match[] getMatchesForFile(IFile file) {
        List<Match> matches = new ArrayList<Match>();
        for (Object element : this.getElements()) {
            IFile elementFile = getFile(element);
            if (elementFile != null && elementFile.equals(file)) {
                matches.addAll(Arrays.asList(getMatches(element)));
            }
        }
        return matches.toArray(new Match[matches.size()]);
    }

    @Override
    public IFile getFile(Object element) {
        if (element instanceof IFile) {
            return (IFile) element;
        } else {
            if (element instanceof AsciidocSearchResultElement) {
                AsciidocSearchResultElement e = (AsciidocSearchResultElement) element;
                while (e != null) {
                    if (e instanceof ResourceElement) {
                        ResourceElement re = (ResourceElement) e;
                        IResource resource = re.getResource();
                        if (resource instanceof IFile) {
                            return (IFile) resource;
                        }
                        return null;
                    }
                    e = e.getParent();
                }
            }
            return null;
        }
    }

    /*
     * --- Editor matcher ----
     */
    @Override
    public boolean isShownInEditor(Match match, IEditorPart editor) {
        IEditorInput ei = editor.getEditorInput();
        if (ei instanceof IFileEditorInput) {
            IFile file = getFile(match.getElement());
            return file != null && file.equals(((IFileEditorInput) ei).getFile());
        }
        return false;
    }

    @Override
    public Match[] computeContainedMatches(AbstractTextSearchResult result, IEditorPart editor) {
        IEditorInput ei = editor.getEditorInput();
        if (ei instanceof IFileEditorInput) {
            IFileEditorInput fe = (IFileEditorInput) ei;
            return getMatchesForFile(fe.getFile());
        }
        return new Match[0];
    }

}
