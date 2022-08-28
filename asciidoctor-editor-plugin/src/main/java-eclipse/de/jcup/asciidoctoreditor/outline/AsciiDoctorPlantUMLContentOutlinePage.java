/*
 * Copyright 2017 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.outline;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocStringUtils;
import de.jcup.asciidoctoreditor.diagram.plantuml.AsciiDoctorPlantUMLEditor;
import de.jcup.asciidoctoreditor.diagram.plantuml.PlantUMLScriptModel;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModel;
import de.jcup.asciidoctoreditor.util.EclipseUtil;

public class AsciiDoctorPlantUMLContentOutlinePage extends ContentOutlinePage implements IDoubleClickListener, ScriptItemContentOutlinePage {
    private static final ImageDescriptor IMG_DESC_LINKED = createOutlineImageDescriptor("synced.png");
    private static final ImageDescriptor IMG_DESC_NOT_LINKED = createOutlineImageDescriptor("sync_broken.png");
    private static final ImageDescriptor IMG_DESC_EXPAND_ALL = createOutlineImageDescriptor("expandall.png");
    private static final ImageDescriptor IMG_DESC_COLLAPSE_ALL = createOutlineImageDescriptor("collapseall.png");

    private AsciiDoctorPlantUMLEditorTreeContentProvider contentProvider;
    private Object input;
    private AsciiDoctorPlantUMLEditor editor;
    private AsciiDoctorPlantUMLEditorOutlineLabelProvider labelProvider;

    private boolean linkingWithEditorEnabled;
    private boolean ignoreNextSelectionEvents;
    private ToggleLinkingAction toggleLinkingAction;

    public AsciiDoctorPlantUMLContentOutlinePage(AsciiDoctorPlantUMLEditor editor) {
        this.editor = editor;
        this.contentProvider = new AsciiDoctorPlantUMLEditorTreeContentProvider();
    }

    public AsciiDoctorPlantUMLEditorTreeContentProvider getScriptItemTreeContentProvider() {
        return contentProvider;
    }

    public void createControl(Composite parent) {
        super.createControl(parent);

        labelProvider = new AsciiDoctorPlantUMLEditorOutlineLabelProvider();

        TreeViewer viewer = getTreeViewer();
        viewer.setContentProvider(contentProvider);
        viewer.addDoubleClickListener(this);
        viewer.setLabelProvider(new DelegatingStyledCellLabelProvider(labelProvider));
        viewer.addSelectionChangedListener(this);

        /* it can happen that input is already updated before control created */
        if (input != null) {
            viewer.setInput(input);
        }
        toggleLinkingAction = new ToggleLinkingAction();
        toggleLinkingAction.setActionDefinitionId(IWorkbenchCommandConstants.NAVIGATE_TOGGLE_LINK_WITH_EDITOR);

        IActionBars actionBars = getSite().getActionBars();

        IToolBarManager toolBarManager = actionBars.getToolBarManager();
        toolBarManager.add(new ExpandAllAction());
        toolBarManager.add(new CollapseAllAction());
        toolBarManager.add(toggleLinkingAction);

        IMenuManager viewMenuManager = actionBars.getMenuManager();
        viewMenuManager.add(new Separator("EndFilterGroup")); //$NON-NLS-1$

        viewMenuManager.add(new Separator("treeGroup")); //$NON-NLS-1$
        viewMenuManager.add(toggleLinkingAction);

        /*
         * when no input is set on init state - let the editor rebuild outline (async)
         */
        if (input == null && editor != null) {
            editor.getOutlineSupport().rebuildOutlineAndValidate();
        }

    }

    @Override
    public void doubleClick(DoubleClickEvent event) {
        if (editor == null) {
            return;
        }
        ISelection selection = event.getSelection();

        if (isAnIncludedAndHandled(selection)) {
            return;
        }
        if (linkingWithEditorEnabled) {
            editor.setFocus();
            // selection itself is already handled by single click
            return;
        }
        editor.getOutlineSupport().openSelectedTreeItemInEditor(selection, true);
    }

    private boolean isAnIncludedAndHandled(ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection ss = (IStructuredSelection) selection;
            Object firstElement = ss.getFirstElement();
            if (firstElement instanceof Item) {
                Item item = (Item) firstElement;
                ItemType type = item.getItemType();
                if (type != ItemType.INCLUDE) {
                    return false;
                }
                String fullString = item.getFullString();
                String fileName = AsciiDocStringUtils.resolveFilenameOfIncludeOrNull(fullString);
                if (fileName != null) {
                    editor.openInclude(fileName);
                    return true;
                }
            }

        }
        return false;
    }

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        super.selectionChanged(event);
        if (!linkingWithEditorEnabled) {
            return;
        }
        if (ignoreNextSelectionEvents) {
            return;
        }
        ISelection selection = event.getSelection();
        editor.getOutlineSupport().openSelectedTreeItemInEditor(selection, false);
    }

    /**
     * This method is called when editor cursor is moved. When linking between
     * editor and outline is disabled a guard close is made.<br>
     * <br>
     * When an item in tree is found at caret offset the tree item is selected
     * 
     * @param caretOffset
     */
    public void onEditorCaretMoved(int caretOffset) {
        if (!linkingWithEditorEnabled) {
            return;
        }
        ignoreNextSelectionEvents = true;
        if (contentProvider instanceof AsciiDoctorPlantUMLEditorTreeContentProvider) {
            AsciiDoctorPlantUMLEditorTreeContentProvider provider = (AsciiDoctorPlantUMLEditorTreeContentProvider) contentProvider;
            Item item = provider.tryToFindByOffset(caretOffset);
            if (item != null) {
                StructuredSelection selection = new StructuredSelection(item);
                getTreeViewer().setSelection(selection, true);
            }
        }
        ignoreNextSelectionEvents = false;
    }

    public void rebuild(PlantUMLScriptModel model) {
        if (model == null) {
            return;
        }
        contentProvider.rebuildTree(model);

        TreeViewer treeViewer = getTreeViewer();
        if (treeViewer != null) {
            Control control = treeViewer.getControl();
            if (control == null || control.isDisposed()) {
                return;
            }
            treeViewer.setInput(model);
            treeViewer.expandAll(); // we always expand
        }
    }

    class ToggleLinkingAction extends Action {

        private ToggleLinkingAction() {
            if (editor != null) {
                linkingWithEditorEnabled = editor.getPreferences().isLinkOutlineWithEditorEnabled();
            }
            setDescription("link with editor");
            initImage();
            initText();
        }

        @Override
        public void run() {
            linkingWithEditorEnabled = !linkingWithEditorEnabled;

            initText();
            initImage();
        }

        private void initImage() {
            setImageDescriptor(linkingWithEditorEnabled ? getImageDescriptionForLinked() : getImageDescriptionNotLinked());
        }

        private void initText() {
            setText(linkingWithEditorEnabled ? "Click to unlink from editor" : "Click to link with editor");
        }

    }

    class CollapseAllAction extends Action {

        private CollapseAllAction() {
            setImageDescriptor(IMG_DESC_COLLAPSE_ALL);
            setText("Collapse all");
        }

        @Override
        public void run() {
            getTreeViewer().collapseAll();
        }
    }

    class ExpandAllAction extends Action {

        private ExpandAllAction() {
            setImageDescriptor(IMG_DESC_EXPAND_ALL);
            setText("Expand all");
        }

        @Override
        public void run() {
            getTreeViewer().expandAll();
        }
    }

    protected ImageDescriptor getImageDescriptionForLinked() {
        return IMG_DESC_LINKED;
    }

    protected ImageDescriptor getImageDescriptionNotLinked() {
        return IMG_DESC_NOT_LINKED;
    }

    private static ImageDescriptor createOutlineImageDescriptor(String name) {
        return EclipseUtil.createImageDescriptor("/icons/outline/" + name, AsciiDoctorEditorActivator.PLUGIN_ID);
    }

    @Override
    public void rebuild(AsciiDoctorScriptModel model) {
        if (!(model instanceof PlantUMLScriptModel)) {
            return;
        }
        rebuild((PlantUMLScriptModel) model);
    }

}
