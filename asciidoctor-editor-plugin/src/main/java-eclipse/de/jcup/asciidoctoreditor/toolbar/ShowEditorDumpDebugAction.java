/*
 * Copyright 2018 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this path except in compliance with the License.
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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class ShowEditorDumpDebugAction extends ToolbarAction implements DebugAction {

    public ShowEditorDumpDebugAction(AsciiDoctorEditor editor) {
        super(editor);
        initUI();
    }

    @Override
    public void run() {
        String dumpText = this.asciiDoctorEditor.createDump();
        
        StringSelection stringSelection = new StringSelection(dumpText);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        
        MessageDialog.openInformation(EclipseUtil.getActiveWorkbenchShell(), "Editor dump", "Dump was copied to clipboard");
    }

    private void initUI() {
        initImage();
        initText();
    }

    private void initImage() {
        ImageDescriptor sharedImage = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_DEF_VIEW);
        setImageDescriptor(sharedImage);
    }

    private void initText() {
        setText("Create editor dump");
    }

}