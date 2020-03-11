/*
 * Copyright 2017 Albert Tregnaghi
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
 package de.jcup.asciidoctoreditor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;

public abstract class AbstractAsciiDoctorEditorHandler extends AbstractHandler {

	public AbstractAsciiDoctorEditorHandler() {
		super();
	}

	/**
	 * Execute something by using gradle editor instance
	 * @param asciidoctorEditor - never <code>null</code>
	 */
	protected abstract void executeOnAsciiDoctorEditor(AsciiDoctorEditor asciidoctorEditor);

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		AsciiDoctorEditor editor = AsciiDoctorEditorUtil.findActiveAsciidoctorEditorOrNull();
		if (editor==null) {
		    return null;
		}
		executeOnAsciiDoctorEditor(editor);
		return null;
	}

    

}