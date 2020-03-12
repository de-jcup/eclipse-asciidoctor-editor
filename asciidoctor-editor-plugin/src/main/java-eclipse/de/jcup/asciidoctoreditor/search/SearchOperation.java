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
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.search.ui.text.Match;

import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;
import de.jcup.asciidoctoreditor.search.AsciidocSearchResultModel.ResourceElement;
import de.jcup.asciidoctoreditor.search.AsciidocSearchResultModel.ResourceElement.ResourceLineElement;
import de.jcup.asciidoctoreditor.search.AsciidocSearchResultModel.ResourceElement.ResourceLineElement.ResourceLineContentElement;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;
import de.jcup.eclipse.commons.EclipseResourceHelper;

public class SearchOperation /*extends WorkspaceModifyOperation */implements IResourceProxyVisitor {
    private File fileBeingReferenced;
    private IProgressMonitor monitor;
    private AsciidocSearchResult result;

    public SearchOperation(File file) {
        this.fileBeingReferenced=file;
    }

    public void execute(AsciidocSearchResult result, IProgressMonitor monitor) throws CoreException {
        this.monitor=monitor;
        this.result=result;
        ResourcesPlugin.getWorkspace().getRoot().accept(this, IResource.DEPTH_INFINITE);
    }

    protected void handleFile(IFile file) throws CoreException {
        if (file==null) {
            return;
        }
        if (!AsciiDoctorEditorUtil.isAsciidocFileExtension(file.getFileExtension())) {
            return;
        }
        
        /* ---------------- Filter build parts ---------------- */
        String p = file.getProjectRelativePath().toString();
        /* TODO de-jcup, 2020-03-11: make next filtering of build/bin configurable and not hard coded...*/
        if (p.startsWith("bin/")) { // eclipse itself
            return;
        }
        if (p.startsWith("build/")) { // gradle
            return;
        }
        if (p.startsWith("target/")) { // maven
            return;
        }
        /* ---------------- EOF: Filter build parts ---------------- */
        
        File inspectFile = EclipseResourceHelper.DEFAULT.toFile(file);
        if (inspectFile==null) {
            return;
        }
        AsciidocSearchResultModel model = result.getModel();
        ResourceElement resourceElement = null;
        try {
            if (isCanceled()) {
                return;
            }
            List<String> lines = FileUtils.readLines(inspectFile,"UTF-8");
            int offset=0;
            int lineNr=0;
            for (String line : lines) {
                if (isCanceled()) {
                    return;
                }
                lineNr++;
                ResourceLineElement lineElement=null;
                int index =-1;
                int pos =0;
                while ( true) {
                    if (isCanceled()) {
                        return;
                    }
                    index = line.indexOf(fileBeingReferenced.getName(),pos);
                    if (index==-1) {
                        break;
                    }
                    if (resourceElement==null) {
                        resourceElement = model.addResourceElement(file);
                    }
                    if (lineElement==null) {
                        lineElement = resourceElement.createNewLine(lineNr);
                    }
                    ResourceLineContentElement content = lineElement.addContent(line, offset);
                    Match match = new Match(content, offset+index, fileBeingReferenced.getName().length());
                    result.addMatch(match);
                    pos=index+1;
                };
                offset+=line.length()+1;
            }
            
        } catch (IOException e) {
            throw new CoreException(new Status(IStatus.ERROR, AsciiDoctorEditorActivator.PLUGIN_ID, "Was not able to readlines in file:"+file, e));
        }
    }

    public boolean visit(IResourceProxy proxy) throws CoreException {
        if (proxy.getType() == IResource.FILE) {
            IFile file = (IFile) proxy.requestResource();
            handleFile(file);
        }
        return true;
    }
    public boolean isCanceled() {
        return monitor!=null && monitor.isCanceled();
    }
}