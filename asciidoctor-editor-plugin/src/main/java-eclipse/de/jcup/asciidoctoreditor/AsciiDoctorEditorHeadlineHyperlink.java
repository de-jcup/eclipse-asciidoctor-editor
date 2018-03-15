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
package de.jcup.asciidoctoreditor;

import static org.eclipse.core.runtime.Assert.*;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import de.jcup.asciidoctoreditor.script.AsciiDoctorHeadline;

public class AsciiDoctorEditorHeadlineHyperlink implements IHyperlink {

	private IRegion region;
	private AsciiDoctorHeadline function;
	private AsciiDoctorEditor editor;
	

	public AsciiDoctorEditorHeadlineHyperlink(IRegion region, AsciiDoctorHeadline function, AsciiDoctorEditor editor) {
		isNotNull(region, "Gradle hyperlink region may not be null!");
		isNotNull(function, "function may not be null!");
		isNotNull(editor, "editor may not be null!");
		this.region = region;
		this.function = function;
		this.editor=editor;
	}

	@Override
	public IRegion getHyperlinkRegion() {
		return region;
	}

	@Override
	public String getTypeLabel() {
		return "Open function";
	}

	@Override
	public String getHyperlinkText() {
		return "Opens declaration of "+function.getName();
	}


	@Override
	public void open() {
		editor.selectAndReveal(function.getPosition(), function.getLengthToNameEnd());
	}

}
