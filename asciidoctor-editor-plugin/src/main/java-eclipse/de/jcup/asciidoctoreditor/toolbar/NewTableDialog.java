package de.jcup.asciidoctoreditor.toolbar;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

public class NewTableDialog extends TitleAreaDialog {

	private Spinner spinnerColumns;

	private int rows;
	private int columns;

	private Spinner spinnerRows;

	public NewTableDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Insert a new table into document");
		setMessage("Select your wanted table data and press OK", IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		createSpinnerColumns(container);
		createSpinnerRows(container);

		return area;
	}

	private void createSpinnerColumns(Composite container) {
		Label lblSpinnerCols = new Label(container, SWT.NONE);
		lblSpinnerCols.setText("Columns");

		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;

		spinnerColumns = new Spinner(container, SWT.BORDER);
		spinnerColumns.setValues(3, 1, 30, 0, 1, 10);
		spinnerColumns.setLayoutData(data);
	}

	private void createSpinnerRows(Composite container) {
		Label lblSpinnerRows = new Label(container, SWT.NONE);
		lblSpinnerRows.setText("Rows");

		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;

		spinnerRows = new Spinner(container, SWT.BORDER);
		spinnerRows.setValues(3, 1, 100, 0, 1, 10);
		spinnerRows.setLayoutData(data);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	// save content of the Text fields because they get disposed
	// as soon as the Dialog closes
	private void saveInput() {
		rows = spinnerRows.getSelection();
		columns = spinnerColumns.getSelection();

	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public int getRows() {
		return rows;
	}
	public int getColumns() {
		return columns;
	}
}