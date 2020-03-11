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
package de.jcup.asciidoctoreditor.presentation;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class AccessibleFileFieldEditor extends FileFieldEditor {

	public AccessibleFileFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
	}

	@Override
	public Button getChangeControl(Composite parent) {
		return super.getChangeControl(parent);
	}

	
	@Override
	protected void createControl(Composite parent) {
		super.createControl(parent);
	}

	@Override
	public boolean checkState() {
		return super.checkState();
	}
	
	
}
