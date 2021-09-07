package de.jcup.asciidoctoreditor.preferences;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

class KeyValueEntryDialog extends TitleAreaDialog {

    private Text txtKey;
    private Text txtValue;
    private String title;
    private String message;
    private KeyValue keyValue;

    public KeyValueEntryDialog(Shell parentShell, KeyValue keyValue, String title, String message) {
        super(parentShell);
        this.title = title;
        this.message = message;
        if (keyValue == null) {
            keyValue = new KeyValue("","");
        }
        this.keyValue = keyValue;
    }

    @Override
    public void create() {
        super.create();
        setTitle(title);
        if (message != null) {
            setMessage(message, IMessageProvider.INFORMATION);
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        createKeyTextfields(container);
        createValueTextfields(container);

        return area;
    }

    private void createKeyTextfields(Composite container) {
        Label lblIdentifier = new Label(container, SWT.NONE);
        lblIdentifier.setText("Key");

        GridData data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;

        txtKey = new Text(container, SWT.BORDER);
        txtKey.setLayoutData(data);
        txtKey.setText(keyValue.getKey());
    }

    private void createValueTextfields(Composite container) {
        Label lblIdentifier = new Label(container, SWT.NONE);
        lblIdentifier.setText("Identifier");

        GridData data = new GridData();
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;

        txtValue = new Text(container, SWT.BORDER);
        txtValue.setLayoutData(data);
        txtValue.setText(keyValue.getValue());
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    private void saveInput() {
        keyValue.setKey(txtKey.getText());
        keyValue.setValue(txtValue.getText());
    }

    @Override
    protected void okPressed() {
        saveInput();
        super.okPressed();
    }

    public KeyValue getKeyValue() {
        return keyValue;
    }
}
