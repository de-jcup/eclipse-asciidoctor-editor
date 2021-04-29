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
import de.jcup.asciidoctoreditor.asciidoc.AsciiDoctorWrapper;
import de.jcup.asciidoctoreditor.console.AsciiDoctorConsoleUtil;

public class ClearProjectCacheAsciiDocViewAction extends ToolbarAction {

    private static final ImageDescriptor IMG_CLEAR = createToolbarImageDescriptor("clear_project.png");

    public ClearProjectCacheAsciiDocViewAction(AsciiDoctorEditor editor) {
        super(editor);
        initUI();
    }

    @Override
    public void run() {
        AsciiDoctorWrapper wrapper = asciiDoctorEditor.getWrapper();
        AsciiDoctorConsoleUtil.showConsole();
        AsciiDoctorConsoleUtil.output("\nClear complete cache for project:"+asciiDoctorEditor.getProjectName());
        wrapper.deleteTempFolder();
        wrapper.resetCaches();;
    }

    private void initUI() {
        initImage();
        initText();
    }

    private void initImage() {
        setImageDescriptor(IMG_CLEAR);
    }

    private void initText() {
        StringBuilder sb = new StringBuilder();
        sb.append("Clear complete cache for project.\n\nWill destroy every temporary part created/copied for the project.\nSo next rebuild will take more time!");
        setText(sb.toString());
    }

}