/*
 * Copyright 2021 Albert Tregnaghi
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

import static de.jcup.eclipse.commons.ui.SWTFactory.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.jcup.asciidoctoreditor.CustomEntrySupport;
import de.jcup.asciidoctoreditor.KeyValue;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;
import de.jcup.eclipse.commons.ui.SWTFactory;

public abstract class AbstractAsciiDoctorEditorCustomEntriesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    protected static final String P_VARIABLE = "variable";
    protected static final String P_VALUE = "value";

    private static final Object[] NO_OBJECTS = new Object[] {};
    
    private Button checkBoxCustomEntriesEnabled;
    protected boolean customEntriesEnabled;

    protected TableViewer propertiesTable;
    protected String[] propertyTableColumnHeaders = { "Variable", "Value", };
    
    protected Button propertyAddButton;
    protected Button propertyEditButton;
    protected Button propertyRemoveButton;
    protected List<KeyValue> definitionWorkingCopy;

    public AbstractAsciiDoctorEditorCustomEntriesPreferencePage() {
        setPreferenceStore(AsciiDoctorEditorUtil.getPreferences().getPreferenceStore());
        setDescription("Setup custom environment entries.");
        setTitle("Customize");
        
        this.definitionWorkingCopy = new ArrayList<KeyValue>();
    }
    
    protected void setPropertyTableColumnHeaders(String[] propertyTableColumnHeaders) {
        this.propertyTableColumnHeaders = propertyTableColumnHeaders;
    }

    @Override
    protected void performDefaults() {
        this.definitionWorkingCopy = new ArrayList<>();
        propertiesTable.setInput(definitionWorkingCopy);

        this.customEntriesEnabled = getSupport().areCustomEntriesEnabledPerDefault();
        
        this.checkBoxCustomEntriesEnabled.setSelection(customEntriesEnabled);
        
        updateUI();
    }
    
    private void updateUI() {
        updateTable();
        updateButtons();
    }
    
    private void updateButtons() {
        propertyAddButton.setEnabled(customEntriesEnabled);
        /* the remove and edit buttons are set by the table selection listener */
    }

    protected boolean checkSameDefinitionsAsBefore() {
        /* we use a set to check for changes - list does inspect ordering too! */
        Set<KeyValue> freshCopy = getSupport().fetchConfiguredEntriesData();
        HashSet<KeyValue> copiedDefinitionsAsSet = new HashSet<>(definitionWorkingCopy);

        return copiedDefinitionsAsSet.equals(freshCopy);
    }

    protected abstract CustomEntrySupport getSupport();
    
    @Override
    protected void performApply() {
        super.performApply();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void init(IWorkbench workbench) {
       
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite mainComposite = SWTFactory.createComposite(parent, 2, 2, GridData.FILL_BOTH);

        customEntriesEnabled = getSupport().areCustomEntriesEnabled();
        definitionWorkingCopy.addAll(getSupport().fetchConfiguredEntriesData());

        createCheckBox(mainComposite);

        createTable(mainComposite);
        createTableButtons(mainComposite);
        
        updateUI();

        return mainComposite;
    }

    private void createCheckBox(Composite mainComposite) {
        checkBoxCustomEntriesEnabled = new Button(mainComposite, SWT.CHECK);
        checkBoxCustomEntriesEnabled.setText("Custom entries enabled");
        checkBoxCustomEntriesEnabled.setSelection(customEntriesEnabled);
        checkBoxCustomEntriesEnabled.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                customEntriesEnabled = checkBoxCustomEntriesEnabled.getSelection();
                
                updateUI();
            }
        });
        GridData layoutData = new GridData();
        layoutData.verticalSpan = 2;
        checkBoxCustomEntriesEnabled.setLayoutData(layoutData);

    }

    /**
     * Creates and configures the table that displayed the key/value pairs that
     * comprise the environment.
     * 
     * @param parent the composite in which the table should be created
     */
    protected void createTable(Composite parent) {
        Font font = parent.getFont();
        SWTFactory.createLabel(parent, "Definitions:", 2);

        Composite tableComposite = SWTFactory.createComposite(parent, font, 1, 1, GridData.FILL_BOTH, 0, 0);
        propertiesTable = new TableViewer(tableComposite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
        
        Table table = propertiesTable.getTable();
        table.setLayout(new GridLayout());
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        table.setFont(font);
        
        propertiesTable.setContentProvider(new CustomEntryDefinitionsContentProvider());
        propertiesTable.setLabelProvider(new CustomEntryDefinitionLabelProvider());
        propertiesTable.setColumnProperties(new String[] { P_VARIABLE, P_VALUE });
        propertiesTable.setComparator(new ViewerComparator() {
            public int compare(Viewer iviewer, Object e1, Object e2) {
                if (e1 == null) {
                    return -1;
                } else if (e2 == null) {
                    return 1;
                } else {
                    return ((KeyValue) e1).getKey().compareToIgnoreCase(((KeyValue) e2).getKey());
                }
            }
        });
        propertiesTable.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                handleTableSelectionChanged(event);
            }
        });
        propertiesTable.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                if (!propertiesTable.getSelection().isEmpty()) {
                    handlePropertiesEditButtonSelected();
                }
            }
        });
        
        createColumns(tableComposite, table);
        
        propertiesTable.setInput(definitionWorkingCopy);
        propertiesTable.refresh();
    }

    private void createColumns(Composite tableComposite, Table table) {
        final TableColumn tc1 = new TableColumn(table, SWT.NONE, 0);
        tc1.setText(propertyTableColumnHeaders[0]);
        final TableColumn tc2 = new TableColumn(table, SWT.NONE, 1);
        tc2.setText(propertyTableColumnHeaders[1]);
        final Table tref = table;
        final Composite comp = tableComposite;
        tableComposite.addControlListener(new ControlAdapter() {
            public void controlResized(ControlEvent e) {
                Rectangle area = comp.getClientArea();
                Point size = tref.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                ScrollBar vBar = tref.getVerticalBar();
                int width = area.width - tref.computeTrim(0, 0, 0, 0).width - 2;
                if (size.y > area.height + tref.getHeaderHeight()) {
                    Point vBarSize = vBar.getSize();
                    width -= vBarSize.x;
                }
                Point oldSize = tref.getSize();
                if (oldSize.x > area.width) {
                    tc1.setWidth(width / 2 - 1);
                    tc2.setWidth(width - tc1.getWidth());
                    tref.setSize(area.width, area.height);
                } else {
                    tref.setSize(area.width, area.height);
                    tc1.setWidth(width / 2 - 1);
                    tc2.setWidth(width - tc1.getWidth());
                }
            }
        });
    }

    /**
     * Responds to a selection changed event in the table
     * 
     * @param event the selection change event
     */
    protected void handleTableSelectionChanged(SelectionChangedEvent event) {
        int size = ((IStructuredSelection) event.getSelection()).size();
        
        propertyEditButton.setEnabled(size == 1);
        propertyRemoveButton.setEnabled(size > 0);
    }

    /**
     * Creates the add/edit/remove buttons for the table
     * 
     * @param parent the composite in which the buttons should be created
     */
    protected void createTableButtons(Composite parent) {
        // Create button composite
        Composite buttonComposite = SWTFactory.createComposite(parent, parent.getFont(), 1, 1, GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_END, 0, 0);

        // Create buttons
        propertyAddButton = createPushButton(buttonComposite, "N&ew...", null);
        propertyAddButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                handlePropertiesAddButtonSelected();
            }
        });
        propertyEditButton = createPushButton(buttonComposite, "E&dit...", null);
        propertyEditButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                handlePropertiesEditButtonSelected();
            }
        });
        propertyEditButton.setEnabled(false);
        propertyRemoveButton = createPushButton(buttonComposite, "Rem&ove", null);
        propertyRemoveButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                handlePropertiesRemoveButtonSelected();
            }
        });
        propertyRemoveButton.setEnabled(false);
    }

    /**
     * Adds a new variable to the table.
     */
    protected void handlePropertiesAddButtonSelected() {
        KeyValueEntryDialog dialog = new KeyValueEntryDialog(getShell(), null, getAddDialogTitle(), null);
        if (dialog.open() != Window.OK) {
            return;
        }
        KeyValue keyValue = dialog.getKeyValue();
        String name = keyValue.getKey();

        if (name != null && name.length() > 0) {
            addVariable(keyValue);
        }
    }

    protected abstract String getAddDialogTitle();

    protected abstract String getOverwriteDialogTitle();
    
    protected String getOverwriteMessage() {
        return "An entry definition named {0} already exists. Overwrite?";
    }
    
    protected abstract String getChangeEntryDialogTitle();
    
    /**
     * Attempts to add the given variable. Returns whether the variable was added or
     * not (as when the user answers not to overwrite an existing variable).
     * 
     * @param definition the variable to add
     * @return <code>true</code> when variable was added
     */
    protected boolean addVariable(KeyValue definition) {
        String key = definition.getKey();
        TableItem[] items = propertiesTable.getTable().getItems();
        for (int i = 0; i < items.length; i++) {
            KeyValue existingVariable = (KeyValue) items[i].getData();
            if (existingVariable.getKey().equals(key)) {
                boolean overWrite = MessageDialog.openQuestion(getShell(), getOverwriteDialogTitle(),
                        MessageFormat.format(getOverwriteMessage(), new Object[] { key })); //
                if (!overWrite) {
                    return false;
                }
                definitionWorkingCopy.remove(existingVariable);
                break;
            }
        }
        definitionWorkingCopy.add(definition);
        updateTable();
        return true;
    }

    private void updateTable() {
        this.propertiesTable.getContentProvider().inputChanged(propertiesTable, null, definitionWorkingCopy);

        propertiesTable.getControl().setEnabled(customEntriesEnabled);
        if (!customEntriesEnabled) {
            propertiesTable.setSelection(null);
        }

    }

    private void handlePropertiesEditButtonSelected() {
        IStructuredSelection sel = (IStructuredSelection) propertiesTable.getSelection();
        KeyValue var = (KeyValue) sel.getFirstElement();
        if (var == null) {
            return;
        }

        KeyValueEntryDialog dialog = new KeyValueEntryDialog(getShell(), var, getChangeEntryDialogTitle(), null);
        if (dialog.open() != Window.OK) {
            return;
        }
        updateTable();
    }

    private void handlePropertiesRemoveButtonSelected() {
        IStructuredSelection sel = (IStructuredSelection) propertiesTable.getSelection();
        for (@SuppressWarnings("unchecked")
        Iterator<KeyValue> i = sel.iterator(); i.hasNext();) {
            KeyValue var = i.next();
            definitionWorkingCopy.remove(var);
        }
        updateTable();
    }

    
    protected class CustomEntryDefinitionsContentProvider implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                Object[] result = ((List<?>) inputElement).toArray();
                return result;
            }
            return NO_OBJECTS;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            if (newInput == null) {
                return;
            }
            if (viewer instanceof TableViewer) {
                TableViewer tableViewer = (TableViewer) viewer;
                if (tableViewer.getTable().isDisposed()) {
                    return;
                }
                tableViewer.refresh();
            }
        }
    }

    public class CustomEntryDefinitionLabelProvider extends LabelProvider implements ITableLabelProvider {
        public String getColumnText(Object element, int columnIndex) {
            String result = null;
            if (element != null) {
                KeyValue var = (KeyValue) element;
                switch (columnIndex) {
                case 0: // variable
                    result = var.getKey();
                    break;
                case 1: // value
                    result = var.getValue();
                    break;
                }
            }
            return result;
        }

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }
    }
}