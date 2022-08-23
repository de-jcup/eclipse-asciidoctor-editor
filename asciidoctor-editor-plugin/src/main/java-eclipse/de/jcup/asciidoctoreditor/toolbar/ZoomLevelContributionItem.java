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
        
        combo.setToolTipText("Set the zoom level for the PlantUML diagram preview.");
        String[] items = { "25 %", "50 %", "100 %", "150 %", "200 %", "300 %", "400 %" };
        combo.setItems(items);
        combo.setText("100 %");
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
        String[] splitted = text.split("%");
        if (splitted.length < 1) {
            return;
        }
        String valueOnly = splitted[0].trim();
        Integer value = Integer.parseInt(valueOnly);

        double percentage = ((double) value) / 100;

        editor.updateScaleFactor(percentage);
    }

    @Override
    protected int computeWidth(Control control) {
        return control.computeSize(90, 12, true).x;
    }
    
    public void updateZoomLevel(double percentage) {
        combo.setText( (int)(percentage*100)+" %");
    }

}
