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

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NewLinkDialog extends TitleAreaDialog {

	private Text txtLinkText;

	private Text txtTarget;

	private String target;
	private String linkText;

	private Button btnLinkExternal;

	private Button btnLinkInternalCrossReference;

	private LinkType linkType;

	public NewLinkDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Insert a new link into document");
		setMessage("Select your wanted link data and press OK", IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		createTypeSelection(container);
		createLabelTextfields(container);
		createTargetTextFields(container);

		return area;
	}

	private void createTypeSelection(Composite container) {
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan=2;
		
		Group linkTypeGroup = new Group(container, SWT.NONE);
		linkTypeGroup.setText("Link blockType:");
		linkTypeGroup.setLayout(new RowLayout(SWT.VERTICAL));
		linkTypeGroup.setLayoutData(data);
		
		btnLinkExternal = new Button(linkTypeGroup, SWT.RADIO);
		btnLinkExternal.setText("External");
		btnLinkExternal.setSelection(false);

		btnLinkInternalCrossReference = new Button(linkTypeGroup, SWT.RADIO);
		btnLinkInternalCrossReference.setText("Internal cross reference");
		btnLinkInternalCrossReference.setToolTipText("Internal cross reference, enter 'abc-xyz' when you want to refer '[[abc-xyz]]' inside your document)");
		
		btnLinkInternalCrossReference.setSelection(true);
	}

	private void createLabelTextfields(Composite container) {
		Label lblLinkTextLabel = new Label(container, SWT.NONE);
		lblLinkTextLabel.setText("Link text");

		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;

		txtLinkText = new Text(container, SWT.BORDER);
		txtLinkText.setLayoutData(data);
	}

	private void createTargetTextFields(Composite container) {
		Label lblTarget = new Label(container, SWT.NONE);
		lblTarget.setText("Link target");
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;

		txtTarget = new Text(container, SWT.BORDER);
		txtTarget.setLayoutData(data);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	// save content of the Text fields because they get disposed
	// as soon as the Dialog closes
	private void saveInput() {
		target = txtTarget.getText();
		linkText = txtLinkText.getText();
		if (btnLinkExternal.getSelection()){
			linkType = LinkType.EXTERNAL;
		}else if (btnLinkInternalCrossReference.getSelection()){
			linkType = LinkType.INTERNAL_CROSS_REFERENCE;
		}
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getTarget() {
		return target;
	}

	public String getLinkText() {
		return linkText;
	}
	public LinkType getLinkType() {
		return linkType;
	}
}