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

public class NewCrossReferenceMarkerInsertAction extends InsertTextAction {

    private static ImageDescriptor IMG_NEW_ANCHOR = createToolbarImageDescriptor("inline_anchor.gif");

    public NewCrossReferenceMarkerInsertAction(AsciiDoctorEditor editor) {
        super(editor, "Insert a reference anchor", IMG_NEW_ANCHOR);
    }

    private class LinkData {
        String target;
    }

    @Override
    protected String getInsertText(InsertTextContext context) {
        LinkData data = (LinkData) context.data;

        StringBuilder sb = new StringBuilder();
        sb.append("[[");
        sb.append(data.target);
        sb.append("]]");
        context.nextOffset = context.selectedOffset + sb.length();

        return sb.toString();
    }

    @Override
    protected void beforeInsert(InsertTextContext context) {
        NewCrossReferenceMarkerDialog dialog = new NewCrossReferenceMarkerDialog(EclipseUtil.getActiveWorkbenchShell());
        int result = dialog.open();
        if (result == Window.CANCEL) {
            context.canceled = true;
            return;
        }
        LinkData data = new LinkData();
        data.target = dialog.getTarget();

        context.data = data;
    }

}
