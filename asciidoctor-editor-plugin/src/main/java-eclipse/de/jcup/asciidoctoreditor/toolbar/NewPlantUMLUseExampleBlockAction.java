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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.util.EclipseUtil;

public class NewPlantUMLUseExampleBlockAction extends ToolbarAction {
    private static ImageDescriptor IMG_EXAMPLE_BLOCK = createToolbarImageDescriptor("plantuml-examples.png");

    public NewPlantUMLUseExampleBlockAction(AsciiDoctorEditor editor) {
        super(editor);
        setText("Use a PlantUML example");
        setImageDescriptor(IMG_EXAMPLE_BLOCK);
    }

    @Override
    public void run() {
        Shell shell = EclipseUtil.getActiveWorkbenchShell();
        IStructuredContentProvider contentProvider = new ArrayContentProvider();
        ILabelProvider provider = new LabelProvider();

        ListDialog dlg = new ListDialog(shell);
        dlg.setContentProvider(contentProvider);
        dlg.setLabelProvider(provider);
        dlg.setInitialSelections(new Object[] { PlantUMLExampleDiagram.CLASS }); 
        dlg.setInput(PlantUMLExampleDiagram.values());
        dlg.setTitle("Replace your editor content with example:");

        int result = dlg.open();
        if (result == Window.CANCEL || dlg.getResult() == null || dlg.getResult().length == 0) {
            return;
        }
        String text = getTextToInject(dlg);
        this.asciiDoctorEditor.getDocument().set(text);
        this.asciiDoctorEditor.doSave(new NullProgressMonitor());

    }

    private String getTextToInject(ListDialog dlg) {
        Object data = dlg.getResult()[0];
        
        if (data instanceof PlantUMLExampleDiagram) {
            PlantUMLExampleDiagram diagram = (PlantUMLExampleDiagram) data;
            String example = ExampleTextSupport.getExampleText(diagram.getPath());
            return example;
        }else {
            return "";
        }
    }

}
