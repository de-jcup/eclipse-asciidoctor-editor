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
package de.jcup.asciidoctoreditor.console;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.IHyperlink;

import de.jcup.asciidoctoreditor.AsciiDoctorEclipseLogAdapter;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class AsciiDoctorConsoleFileHyperlink implements IHyperlink {
    File file;

    public AsciiDoctorConsoleFileHyperlink(File file) {
        this.file = file;
    }

    @Override
    public void linkActivated() {
        if (file == null) {
            return;
        }
        if (!file.exists()) {
            MessageDialog.openWarning(EclipseUtil.getActiveWorkbenchShell(), "Link problem", "The file " + file.getAbsolutePath() + " does not exist!");
            return;
        }
        try {
            EclipseUtil.openInEditor(file);
        } catch (PartInitException e) {
            AsciiDoctorEclipseLogAdapter.INSTANCE.logError("Cannot open " + file.getAbsolutePath(), e);
        }
    }

    @Override
    public void linkEntered() {
    }

    @Override
    public void linkExited() {
    }
}
