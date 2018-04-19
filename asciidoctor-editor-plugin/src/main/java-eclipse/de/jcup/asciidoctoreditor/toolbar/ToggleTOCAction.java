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

public class ToggleTOCAction extends ToolbarAction {

	private static ImageDescriptor IMG_TOC_SHOW = createToolbarImageDescriptor("toc_show.png");
	private static ImageDescriptor IMG_TOC_HIDE = createToolbarImageDescriptor("toc_hide.png");

	public ToggleTOCAction(AsciiDoctorEditor asciiDoctorEditor) {
		super(asciiDoctorEditor);
		initUI();
	}

	@Override
	public void run() {
		asciiDoctorEditor.setTOCShown(!asciiDoctorEditor.isTOCShown());
		initUI();
	}

	private void initUI() {
		initImage();
		initText();
	}

	private void initImage() {
		setImageDescriptor(this.asciiDoctorEditor.isTOCShown() ? IMG_TOC_SHOW : IMG_TOC_HIDE);
	}

	private void initText() {
		setText(this.asciiDoctorEditor.isTOCShown() ? "TOC is shown" : "TOC is hidden");
	}

}