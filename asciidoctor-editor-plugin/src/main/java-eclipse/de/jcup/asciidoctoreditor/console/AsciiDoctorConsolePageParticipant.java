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
package de.jcup.asciidoctoreditor.console;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;

import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;

public class AsciiDoctorConsolePageParticipant  implements IConsolePageParticipant {
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	@Override
	public void activated() {
	}

	@Override
	public void deactivated() {
	}

	@Override
	public void dispose() {
		AsciiDoctorEditorActivator.getDefault().removeViewerWithPageParticipant(this);
	}

	@Override
	public void init(IPageBookViewPage page, IConsole console) {
		boolean needsStyleing = console instanceof AsciiDoctorConsole;
		if (!needsStyleing) {
			return;
		}
		Control control = page.getControl();
		if (control instanceof StyledText) {

			/* Add process style listener to viewer */
			StyledText viewer = (StyledText) control;
			AsciiDoctorConsoleStyleListener myListener = new AsciiDoctorConsoleStyleListener();
			viewer.addLineStyleListener(myListener);

			AsciiDoctorEditorActivator.getDefault().addViewer(viewer, this);
		}
	}
}
