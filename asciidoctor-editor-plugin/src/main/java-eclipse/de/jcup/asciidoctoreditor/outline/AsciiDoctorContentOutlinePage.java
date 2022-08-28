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
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocStringUtils;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModel;
import de.jcup.asciidoctoreditor.util.EclipseUtil;

public class AsciiDoctorContentOutlinePage extends ContentOutlinePage implements IDoubleClickListener, ScriptItemContentOutlinePage {
    private static final ImageDescriptor IMG_DESC_GROUPED = createOutlineImageDescriptor("grouped.png");;
    private static final ImageDescriptor IMG_DESC_NOT_GROUPED = createOutlineImageDescriptor("not_grouped.png");;
    private static final ImageDescriptor IMG_DESC_LINKED = createOutlineImageDescriptor("synced.png");
    private static final ImageDescriptor IMG_DESC_NOT_LINKED = createOutlineImageDescriptor("sync_broken.png");
    private static final ImageDescriptor IMG_DESC_EXPAND_ALL = createOutlineImageDescriptor("expandall.png");
    private static final ImageDescriptor IMG_DESC_COLLAPSE_ALL = createOutlineImageDescriptor("collapseall.png");
    
    private static final String MENU_ID = "de.jcup.asciidoctoreditor.outline";
	private static final String CONTEXT_MENU_ID = "AsciiDoctorOutlinePageContextMenu";

    private AsciiDoctorEditorTreeContentProvider contentProvider;
    private Object input;
    private AsciiDoctorEditor editor;
    private AsciiDoctorEditorOutlineLabelProvider labelProvider;

    private boolean linkingWithEditorEnabled;
    private boolean ignoreNextSelectionEvents;
    private ToggleLinkingAction toggleLinkingAction;

    public AsciiDoctorContentOutlinePage(AsciiDoctorEditor editor) {
        this.editor = editor;
        this.contentProvider = new AsciiDoctorEditorTreeContentProvider();
        this.contentProvider.setGroupingEnabled(AsciiDoctorEditorPreferences.getInstance().isGroupingInOutlineEnabledPerDefault());
    }

    public AsciiDoctorEditorTreeContentProvider getScriptItemTreeContentProvider() {
        return contentProvider;
    }

    public void createControl(Composite parent) {
        super.createControl(parent);

        labelProvider = new AsciiDoctorEditorOutlineLabelProvider();

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
        toolBarManager.add(new ToggleSortAction());

        IMenuManager viewMenuManager = actionBars.getMenuManager();
        viewMenuManager.add(new Separator("EndFilterGroup")); //$NON-NLS-1$

        viewMenuManager.add(new Separator("treeGroup")); //$NON-NLS-1$
        viewMenuManager.add(toggleLinkingAction);
        
        configureContextMenu();
		configureDragAndDrop();
		
        /*
         * when no input is set on init state - let the editor rebuild outline (async)
         */
        if (input == null && editor != null) {
            editor.getOutlineSupport().rebuildOutlineAndValidate();
        }

    }

    private void configureContextMenu() {
		MenuManager menuManager = new MenuManager(CONTEXT_MENU_ID, CONTEXT_MENU_ID);
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		
		Menu contextMenu = menuManager.createContextMenu(getTreeViewer().getTree());
		getTreeViewer().getTree().setMenu(contextMenu);
		getSite().registerContextMenu(MENU_ID, menuManager, getTreeViewer());
	}
	
	private void configureDragAndDrop() {
		int ops = DND.DROP_COPY | DND.DROP_MOVE;
		DragSource source = new DragSource(getTreeViewer().getControl(), ops);
		source.setTransfer(LocalSelectionTransfer.getTransfer());
		source.addDragListener(new DragSourceListener() {
			@Override
			public void dragStart(DragSourceEvent event) {
				IStructuredSelection selection = (IStructuredSelection) getTreeViewer().getSelection();
				// Only start the drag if anything is actually selected in the tree.
				if (selection.isEmpty()) {
					event.doit = false;
				}
			}

			@Override
			public void dragSetData(DragSourceEvent event) {
				// Provide the data of the requested type.
				if (LocalSelectionTransfer.getTransfer().isSupportedType(event.dataType)) {
					event.data = getTreeViewer().getSelection();
					LocalSelectionTransfer.getTransfer().setSelection(getTreeViewer().getSelection());
					((DragSource) event.widget).setTransfer(LocalSelectionTransfer.getTransfer());
				}
			}

			@Override
			public void dragFinished(DragSourceEvent event) {
				// Deliberately do nothing
			}

		});
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
        if (contentProvider instanceof ScriptItemTreeContentProvider) {
            ScriptItemTreeContentProvider provider = (ScriptItemTreeContentProvider) contentProvider;
            Item item = provider.tryToFindByOffset(caretOffset);
            if (item != null) {
                StructuredSelection selection = new StructuredSelection(item);
                getTreeViewer().setSelection(selection, true);
            }
        }
        ignoreNextSelectionEvents = false;
    }

    public void rebuild(AsciiDoctorScriptModel model) {
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

    class ToggleSortAction extends Action {

        private ToggleSortAction() {
            if (editor != null) {
                linkingWithEditorEnabled = editor.getPreferences().isLinkOutlineWithEditorEnabled();
            }
            setDescription("link with editor");
            initImage();
            initText();
        }

        @Override
        public void run() {
            contentProvider.setGroupingEnabled(!contentProvider.isGroupingEnabled());

            initText();
            initImage();

            if (editor == null) {
                return;
            }
            editor.rebuildOutlineAndValidate();

        }

        private void initImage() {
            setImageDescriptor(contentProvider.isGroupingEnabled() ? getImageDescriptionForGrouped() : getImageDescriptionNotGrouped());
        }

        private void initText() {
            setText(contentProvider.isGroupingEnabled() ? "Click to disable grouping" : "Click to enable grouping");
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

    protected ImageDescriptor getImageDescriptionForGrouped() {
        return IMG_DESC_GROUPED;
    }

    protected ImageDescriptor getImageDescriptionNotGrouped() {
        return IMG_DESC_NOT_GROUPED;
    }

    private static ImageDescriptor createOutlineImageDescriptor(String name) {
        return EclipseUtil.createImageDescriptor("/icons/outline/" + name, AsciiDoctorEditorActivator.PLUGIN_ID);
    }

}
