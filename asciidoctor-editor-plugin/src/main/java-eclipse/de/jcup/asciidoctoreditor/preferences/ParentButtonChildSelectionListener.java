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
package de.jcup.asciidoctoreditor.preferences;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;

class ParentButtonChildSelectionListener implements SelectionListener {
    private Button master;
    private Control slave;
    private boolean negative;

    public ParentButtonChildSelectionListener(Button master, Control slave, boolean negative) {
        this.master = master;
        this.slave = slave;
        this.negative = negative;
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {

    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        updateChildComponent();
    }

    void updateChildComponent() {
        boolean state = master.getSelection();
        if (negative) {
            slave.setEnabled(!state);
        } else {
            slave.setEnabled(state);
        }
    }

}