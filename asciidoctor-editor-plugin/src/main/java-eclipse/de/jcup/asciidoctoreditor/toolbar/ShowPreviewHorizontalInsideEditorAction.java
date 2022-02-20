/*
 * Copyright 2019 Albert Tregnaghi
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

public class ShowPreviewHorizontalInsideEditorAction extends ToolbarAction {
    private static final String LAYOUT_TOOLTIP= "Horizontal layout";
	private static final ImageDescriptor IMG_LAYOUT = createToolbarImageDescriptor("layout_horizontal.png");
	
	public ShowPreviewHorizontalInsideEditorAction(AsciiDoctorEditor editor) {
		super(editor);
		initUI();
	}
	
	private void initUI() {
		setImageDescriptor(IMG_LAYOUT);
		setToolTipText(LAYOUT_TOOLTIP);
		
	}

	@Override
	public void run() {
	    asciiDoctorEditor.setVerticalSplit(false);
        asciiDoctorEditor.setInternalPreview(true);
	}
}