/*
 * Copyright 2016 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.hyperlink;

import static org.eclipse.core.runtime.Assert.*;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;

public class AsciiDoctorEditorOpenImageHyperlink implements IHyperlink {

	private IRegion region;
	private AsciiDoctorEditor editor;
	private String fileName;
	

	public AsciiDoctorEditorOpenImageHyperlink(IRegion region, String fileName, AsciiDoctorEditor editor) {
		isNotNull(region, "Hyperlink region may not be null!");
		isNotNull(fileName, "fileName may not be null!");
		isNotNull(editor, "editor may not be null!");
		this.region = region;
		this.fileName = fileName;
		this.editor=editor;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return region;
	}

	@Override
	public String getTypeLabel() {
		return "Open image";
	}

	@Override
	public String getHyperlinkText() {
		return "Opens image:"+fileName;
	}


	@Override
	public void open() {
		editor.openImage(fileName);
	}

}