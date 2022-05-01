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

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;

public class RebuildAsciiDocViewAction extends ToolbarAction {

    private static ImageDescriptor IMG_REFRESH = createToolbarImageDescriptor("refresh.png");

    public RebuildAsciiDocViewAction(AsciiDoctorEditor editor) {
        super(editor);
        initUI();
    }

    @Override
    public void run() {
        asciiDoctorEditor.resetCache();
        asciiDoctorEditor.refreshAsciiDocView();
        initUI();

    }

    private void initUI() {
        initImage();
        initText();
    }

    private void initImage() {
        setImageDescriptor(IMG_REFRESH);
    }

    private void initText() {
        StringBuilder sb = new StringBuilder();
        sb.append("Rebuild Asciidoc preview content (F5)\n\n");
        sb.append("When to use? \n");
        sb.append(" 1. When included files or resources have been changed outside the editor\n");
        sb.append(" 2. You are using external browser instead of of internal view and you \n");
        sb.append("    want to update the external view in browser - but be aware: you still have to\n");
        sb.append("    refresh your external browser view too");
        setText(sb.toString());
    }

}