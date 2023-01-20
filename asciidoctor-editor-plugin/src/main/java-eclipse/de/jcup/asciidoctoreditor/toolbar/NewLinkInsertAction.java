/*
 * Copyright 2018 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.toolbar;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.util.EclipseUtil;
import de.jcup.eclipse.commons.SimpleStringUtils;

public class NewLinkInsertAction extends InsertTextAction {

    private static ImageDescriptor IMG_NEW_LINK = createToolbarImageDescriptor("link.png");

    public NewLinkInsertAction(AsciiDoctorEditor editor) {
        super(editor, "Insert a link", IMG_NEW_LINK);
    }

    private class LinkData {
        String linkText;
        String target;
        LinkType linkType;
    }

    @Override
    protected String getInsertText(InsertTextContext context) {
        LinkData data = (LinkData) context.data;

        StringBuilder sb = new StringBuilder();
        switch (data.linkType) {
        case EXTERNAL:
            sb.append(data.target);
            if (!SimpleStringUtils.isEmpty(data.linkText)) {
                sb.append("[");
                sb.append(data.linkText);
                sb.append("]");
            }
            break;
        case INTERNAL_CROSS_REFERENCE:
            sb.append("<<");
            sb.append(data.target);
            if (!SimpleStringUtils.isEmpty(data.linkText)) {
                sb.append(',');
                sb.append(data.linkText);
            }
            sb.append(">>");
            break;
        default:
            sb.append("Unsupported blockType:" + data.linkType);
        }
        sb.append(" ");
        context.nextOffset = context.selectedOffset + sb.length();

        return sb.toString();
    }

    @Override
    protected void beforeInsert(InsertTextContext context) {
        String initialLinText = context.selectedText;
        
        NewLinkDialog dialog = new NewLinkDialog(EclipseUtil.getActiveWorkbenchShell(), initialLinText);
        int result = dialog.open();
        if (result == Window.CANCEL) {
            context.canceled = true;
            return;
        }
        LinkData data = new LinkData();
        data.linkText = dialog.getLinkText();
        data.target = dialog.getTarget();
        data.linkType = dialog.getLinkType();

        context.data = data;
    }

}
