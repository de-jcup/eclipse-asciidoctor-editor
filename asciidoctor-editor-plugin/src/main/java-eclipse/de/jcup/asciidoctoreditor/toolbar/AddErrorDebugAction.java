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

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.script.AsciiDoctorMarker;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;

public class AddErrorDebugAction extends ToolbarAction implements DebugAction {

    public AddErrorDebugAction(AsciiDoctorEditor editor) {
        super(editor);
        initUI();
    }

    @Override
    public void run() {
        AsciiDoctorMarker error = new AsciiDoctorMarker(-1, -1, "the message at " + System.currentTimeMillis() + " millis");
        AsciiDoctorEditorUtil.addAsciiDoctorMarker(asciiDoctorEditor, -1, error, IMarker.SEVERITY_ERROR);
    }

    private void initUI() {
        initImage();
        initText();
    }

    private void initImage() {
        ImageDescriptor sharedImage = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_ERROR_TSK);
        setImageDescriptor(sharedImage);
    }

    private void initText() {
        setText("Add an error");
    }

}