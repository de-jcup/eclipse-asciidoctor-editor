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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE.SharedImages;

import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocFileFilter;
import de.jcup.asciidoctoreditor.search.AsciidocSearchResultModel.ProjectElement;
import de.jcup.asciidoctoreditor.search.AsciidocSearchResultModel.ResourceElement;
import de.jcup.asciidoctoreditor.search.AsciidocSearchResultModel.ResourceElement.ResourceLineElement;
import de.jcup.asciidoctoreditor.search.AsciidocSearchResultModel.ResourceElement.ResourceLineElement.ResourceLineContentElement;
import de.jcup.asciidoctoreditor.ui.AsciidoctorIconConstants;
import de.jcup.asciidoctoreditor.util.EclipseUtil;

public class AsciidocSearchResultLabelProvider extends BaseLabelProvider implements IStyledLabelProvider, IColorProvider, ITableLabelProvider, ILabelProvider {

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
            styled.append(re.getResource().getName());
        } else if (element instanceof ResourceLineElement) {
            ResourceLineElement rle = (ResourceLineElement) element;
            styled.append("Line:" + rle.getLineNumber());
        } else if (element instanceof ResourceLineContentElement) {
            ResourceLineContentElement rle = (ResourceLineContentElement) element;
            styled.append(rle.getText());
        } else {
            styled.append("->" + element);
        }
        return styled;
    }

    @Override
    public String getText(Object element) {
        StringBuilder styled = new StringBuilder();
        if (element instanceof ProjectElement) {
            ProjectElement pe = (ProjectElement) element;
            return pe.getProjectName();
        }

        if (element instanceof ResourceElement) {
            ResourceElement re = (ResourceElement) element;
            IResource r = re.getResource();
            if (r == null) {
                return null;
            }

            styled.append(r.getProjectRelativePath());
        } else if (element instanceof ResourceLineElement) {
            ResourceLineElement rle = (ResourceLineElement) element;
            styled.append("Line:" + rle.getLineNumber());
        } else if (element instanceof ResourceLineContentElement) {
            ResourceLineContentElement rle = (ResourceLineContentElement) element;
            styled.append(rle.getText());
        } else {
            styled.append("->" + element);
        }
        return styled.toString();
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof ProjectElement) {
            return PlatformUI.getWorkbench().getSharedImages().getImage(SharedImages.IMG_OBJ_PROJECT);
        }
        if (element instanceof ResourceElement) {
            ResourceElement re = (ResourceElement) element;
            IResource resource = re.getResource();
            if (resource == null) {
                return null;
            }
            if (AsciiDocFileFilter.hasValidFileEnding(resource.getName())) {
                return EclipseUtil.getImage(AsciidoctorIconConstants.PATH_ICON_ASCIIDOCTOR_EDITOR, AsciiDoctorEditorActivator.PLUGIN_ID);
            }
            return null;
        }
        if (element instanceof ResourceLineContentElement) {
            ResourceLineContentElement rlce = (ResourceLineContentElement) element;
            String text = rlce.getText();
            if (text == null) {
                return null;
            }
            if (text.startsWith("include:")) {
                return EclipseUtil.getImage(AsciidoctorIconConstants.PATH_OUTLINE_ICON_INCLUDE, AsciiDoctorEditorActivator.PLUGIN_ID);
            }
            return null;
        }
        return null;
    }

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return getImage(element);
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        if (element instanceof ResourceLineContentElement) {
            ResourceLineContentElement rlce = (ResourceLineContentElement) element;
            ResourceLineElement le = rlce.getParent();
            if (le==null) {
                return null;
            }
            ResourceElement re = le.getParent();
            if (re==null) {
                return null;
            }
            IResource resource = re.getResource();
            if (resource==null) {
                return null;
            }
            IProject project = resource.getProject();
            String projectName = null;
            if (project==null) {
                projectName="(?)";
            }else {
                projectName=project.getName();
            }
            StringBuilder sb = new StringBuilder();
            /* @formatter:off*/
            sb. append(projectName).
                append("  :  ").
                append(resource.getProjectRelativePath()).
                append(" - Line: ").
                append(le.getLineNumber()).
                append(" : ").
                append(rlce.getText());
            return sb.toString();
            /* @formatter:on*/
            
            
        }
        return getText(element);
    }

}
