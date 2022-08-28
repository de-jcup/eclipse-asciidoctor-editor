/*
 * Copyright 2022 Albert Tregnaghi
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

import static org.eclipse.swt.events.SelectionListener.*;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;

public class ZoomLevelContributionItem extends ControlContribution {

    private AsciiDoctorEditor editor;

    public ZoomLevelContributionItem(AsciiDoctorEditor editor) {
        super("asciidoctoreditor.zoomlevel.contributionitem");
        this.editor = editor;
    }

    private Combo combo;

    @Override
    protected Control createControl(Composite parent) {
        combo = new Combo(parent, SWT.DROP_DOWN);
        
        combo.setToolTipText("Set the zoom level for the PlantUML diagram preview.\nYou can also press 'CTRL' and use the mouse wheel inside the internal preview.");
        String[] items = ZoomLevel.DEFAULT_TEXT_ENTRIES;
        combo.setItems(items);
        combo.setText(AsciiDoctorEditorPreferences.getInstance().getPlantUMLDefaultZoomLevelAsText());
        GridData gridData = GridDataFactory.defaultsFor(combo).grab(false, false).create();
        gridData.heightHint=12;
        combo.setLayoutData(gridData);
        combo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.character == SWT.CR) {
                    setZoomLevel(combo.getText(), true);
                }
            }
        });
        combo.addSelectionListener(widgetSelectedAdapter(e -> {
            int index = combo.getSelectionIndex();
            if (index != -1) {
                setZoomLevel(combo.getItem(index), false);
            }
        }));
        return combo;
    }

    protected void setZoomLevel(String text, boolean enteredByUser) {
        Double level = ZoomLevel.calculatePercentagefromString(text);
        if (level == null) {
            return;
        }
        editor.updateScaleFactor(level);
    }

    @Override
    protected int computeWidth(Control control) {
        return control.computeSize(90, 12, true).x;
    }
    
    public void updateZoomLevel(double percentage) {
        combo.setText( (int)(percentage*100)+" %");
    }

}
