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

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;

import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;

public class FindAsciidocfileReferencesQuery implements ISearchQuery{

    private AsciidocSearchResult result = new AsciidocSearchResult(this);
    private File fileToSearch;
    
    public FindAsciidocfileReferencesQuery(File fileToSearch){
        this.fileToSearch=fileToSearch;
    }
    
    @Override
    public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
        SearchOperation searchOperation = new SearchOperation(fileToSearch);
        try {
            searchOperation.execute(result, monitor);
            return Status.OK_STATUS;
        } catch (CoreException e) {
           return new Status(IStatus.ERROR,AsciiDoctorEditorActivator.PLUGIN_ID,"was not able to execute search operation",e);
        }
        
    }

    @Override
    public String getLabel() {
        return "References of '"+fileToSearch.getName()+"'";
    }

    @Override
    public boolean canRerun() {
        return false;
    }

    @Override
    public boolean canRunInBackground() {
        return true;
    }

    @Override
    public ISearchResult getSearchResult() {
        return result;
    }

}
