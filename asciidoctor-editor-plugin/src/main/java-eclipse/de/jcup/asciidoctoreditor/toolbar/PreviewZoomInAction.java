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

public class PreviewZoomInAction extends ToolbarAction {
	
	private static ImageDescriptor IMG_CREATE_PDF = createToolbarImageDescriptor("create_pdf.png");
	
	public PreviewZoomInAction(AsciiDoctorEditor editor) {
		super(editor);
		initUI();
	}
	
	private void initUI() {
		setImageDescriptor(IMG_CREATE_PDF);
		setToolTipText("Zoom in");
		
	}

	@Override
	public void run() {
		asciiDoctorEditor.zoomIn(10);
		
	}
}