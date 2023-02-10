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