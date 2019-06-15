/*
 * Copyright 2016 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.ui;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;

import de.jcup.asciidoctoreditor.AdaptedFromEGradle;
import de.jcup.asciidoctoreditor.FilterPatternMatcher;
import de.jcup.asciidoctoreditor.outline.FallbackOutlineContentProvider;
import de.jcup.asciidoctoreditor.util.EclipseUtil;

@AdaptedFromEGradle
public abstract class AbstractFilterableTreeQuickDialog<T> extends AbstractQuickDialog implements IDoubleClickListener {

	private static final boolean DO_SHOW_DIALOG = SHOW_DIALOG_MENU;
	private static final int DEFAULT_X = 600;
	private static final int DEFAULT_Y = 400;

	private Object input;

	private Object monitor = new Object();

	private Text text;
	private TreeViewer treeViewer;
	private String currentUsedFilterText;
	private ITreeContentProvider contentProvider;
	private AbstractTreeViewerFilter<T> textFilter;
	private FilterPatternMatcher<T> matcher;
	private int minWidth;
	private int minHeight;

	/**
	 * Creates a quick outline dialog containing a filterable tree
	 * 
	 * @param adaptable
	 *            adaptable which can be used by child class implementations
	 * @param parent
	 *            shell to use is null the outline will have no content! If the
	 *            gradle editor is null location setting etc. will not work.
	 * @param title
	 *            title for dialog
	 * @param minWidth
	 * @param minHeight
	 * @param infoText
	 *            additional information to show at the bottom of dialogs
	 */
	public AbstractFilterableTreeQuickDialog(IAdaptable adaptable, Shell parent, String title, int minWidth,
			int minHeight, String infoText) {
		super(parent, PopupDialog.INFOPOPUPRESIZE_SHELLSTYLE, GRAB_FOCUS, PERSIST_SIZE, PERSIST_BOUNDS, DO_SHOW_DIALOG,
				SHOW_PERSIST_ACTIONS, title, infoText);
		this.minWidth = minWidth;
		this.minHeight = minHeight;

		contentProvider = createTreeContentProvider(adaptable);

		if (contentProvider == null) {
			contentProvider = new FallbackOutlineContentProvider();
		}
	}

	protected abstract ITreeContentProvider createTreeContentProvider(IAdaptable adaptable);

	@Override
	public void doubleClick(DoubleClickEvent event) {
		ISelection selection = event.getSelection();
		openSelectionAndCloseDialog(selection);
	}

	private final void openSelectionAndCloseDialog(ISelection selection) {
		openSelection(selection);
		close();
	}

	private final void openSelection(ISelection selection) {
		String filterText = null;
		if (text != null && !text.isDisposed()) {
			filterText = text.getText();
		}
		openSelectionImpl(selection, filterText);
	}

	/**
	 * Open selection
	 * 
	 * 
	 * @param filterText
	 *            the filter as text or <code>null</code> if not filtered
	 * @param selected
	 *            selected
	 */
	protected abstract void openSelectionImpl(ISelection selection, String filterText);

	/**
	 * Set input to show
	 * 
	 * @param input
	 */
	public final void setInput(Object input) {
		this.input = input;
	}

	@Override
	protected final void beforeRunEventLoop() {
		treeViewer.setInput(input);

		text.setFocus();

		T item = getInitialSelectedItem();
		if (item == null) {
			return;
		}
		StructuredSelection startSelection = new StructuredSelection(item);
		treeViewer.setSelection(startSelection, true);
	}

	protected abstract T getInitialSelectedItem();

	@Override
	protected boolean canHandleShellCloseEvent() {
		return true;
	}

	@Override
	protected final Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		boolean isWin32 = Util.isWindows();
		GridLayoutFactory.fillDefaults().extendedMargins(isWin32 ? 0 : 3, 3, 2, 2).applyTo(composite);

		IBaseLabelProvider labelProvider = createLabelProvider();
		if (labelProvider == null) {
			labelProvider = new LabelProvider();
		}
		int style = SWT.NONE;
		Tree tree = new Tree(composite, SWT.SINGLE | (style & ~SWT.MULTI));
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = tree.getItemHeight() * 12;

		gridData.verticalAlignment = SWT.TOP;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		tree.setLayoutData(gridData);

		treeViewer = new TreeViewer(tree);
		treeViewer.setContentProvider(contentProvider);

		/* filter */
		textFilter = createFilter();
		matcher = createItemMatcher();
		textFilter.setMatcher(matcher);
		treeViewer.setFilters(textFilter);

		tree.setLayoutData(gridData);

		treeViewer.setContentProvider(contentProvider);
		treeViewer.addDoubleClickListener(this);
		treeViewer.setLabelProvider(labelProvider);

		return composite;
	}

	protected abstract FilterPatternMatcher<T> createItemMatcher();

	protected abstract IBaseLabelProvider createLabelProvider();

	protected abstract AbstractTreeViewerFilter<T> createFilter();

	@Override
	protected Control createInfoTextArea(Composite parent) {
		return super.createInfoTextArea(parent);
	}

	@Override
	protected Control createTitleControl(Composite parent) {
		text = new Text(parent, SWT.NONE);

		GridData textLayoutData = new GridData();
		textLayoutData.horizontalAlignment = GridData.FILL;
		textLayoutData.verticalAlignment = GridData.FILL;
		textLayoutData.grabExcessHorizontalSpace = true;
		textLayoutData.grabExcessVerticalSpace = false;
		textLayoutData.horizontalSpan = 2;

		text.setLayoutData(textLayoutData);

		text.addKeyListener(new FilterKeyListener());

		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(DO_SHOW_DIALOG ? 1 : 2, 1)
				.applyTo(text);

		return text;
	}

	@Override
	protected final IDialogSettings getDialogSettings() {
		AbstractUIPlugin activator = getUIPlugin();
		if (activator == null) {
			return null;
		}
		return activator.getDialogSettings();
	}

	protected abstract AbstractUIPlugin getUIPlugin();

	@Override
	protected Point getInitialLocation(Point initialSize) {
		IDialogSettings dialogSettings = getDialogSettings();
		if (dialogSettings == null) {
			/* no dialog settings available, so fall back to min settings */
			return new Point(DEFAULT_X, DEFAULT_Y);
		}
		return super.getInitialLocation(initialSize);
	}

	@Override
	protected Point getInitialSize() {
		IDialogSettings dialogSettings = getDialogSettings();
		if (dialogSettings == null) {
			/* no dialog settings available, so fall back to min settings */
			return new Point(minWidth, minHeight);
		}
		Point point = super.getInitialSize();
		if (point.x < minWidth) {
			point.x = minWidth;
		}
		if (point.y < minHeight) {
			point.y = minHeight;
		}
		return point;
	}

	@Override
	protected boolean hasInfoArea() {
		return super.hasInfoArea();
	}

	private void rebuildFilterTextPattern() {
		if (text == null) {
			return;
		}
		if (text.isDisposed()) {
			return;
		}
		String filterText = text.getText();
		if (filterText == null) {
			if (currentUsedFilterText == null) {
				/* same as before */
				return;
			}
		} else if (filterText.equals(currentUsedFilterText)) {
			/* same as before */
			return;
		}

		matcher.setFilterText(filterText);

		currentUsedFilterText = filterText;

	}

	private class FilterKeyListener extends KeyAdapter {
		private boolean dirty;

		@Override
		public void keyPressed(KeyEvent event) {
			if (event.keyCode == SWT.ARROW_DOWN) {
				Tree tree = treeViewer.getTree();
				if (tree.isDisposed()) {
					return;
				}
				if (tree.isFocusControl()) {
					return;
				}
				tree.setFocus();
				return;
			}
			if (event.character == '\r') {
				ISelection selection = treeViewer.getSelection();
				openSelectionAndCloseDialog(selection);
				return;
			}
			boolean allowedChar = false;
			allowedChar = allowedChar || event.character == '*';
			allowedChar = allowedChar || event.character == '(';
			allowedChar = allowedChar || event.character == ')';
			allowedChar = allowedChar || Character.isJavaIdentifierPart(event.character);
			allowedChar = allowedChar || Character.isWhitespace(event.character);
			if (!allowedChar) {
				event.doit = false;
				return;
			}
			if (treeViewer == null) {
				return;
			}

		}

		@Override
		public void keyReleased(KeyEvent e) {
			String filterText = text.getText();
			if (filterText != null) {
				if (filterText.equals(currentUsedFilterText)) {
					/*
					 * same text, occurs when only cursor keys used etc. avoid
					 * flickering
					 */
					return;
				}
			}
			synchronized (monitor) {
				if (dirty) {
					return;
				}
				dirty = true;
			}

			UIJob job = new UIJob("Rebuild asciidoctor editor quick outline") {

				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) {
					try {
						rebuildFilterTextPattern();
						if (treeViewer.getControl().isDisposed()) {
							return Status.CANCEL_STATUS;
						}
						treeViewer.refresh();
						if (matcher.hasFilterPattern()) {
							/*
							 * something was entered into filter - so results
							 * must be expanded:
							 */
							treeViewer.expandAll();
							selectFirstMaching();
						}
					} catch (RuntimeException e) {
						EclipseUtil.logError("quick dialog failure", e);
					}
					dirty = false;
					return Status.OK_STATUS;
				}
			};
			job.schedule(400);
		}

		protected void selectFirstMaching() {
			selectfirstMatching(getTreeContentProvider().getElements(null));
		}

		private boolean selectfirstMatching(Object[] elements) {
			if (treeViewer==null){
				return false;
			}
			if (textFilter==null){
				return false;
			}
			if (elements == null) {
				return false;
			}
			for (int i = 0; i < elements.length; i++) {
				Object element = elements[i];
				if (Boolean.TRUE.equals(textFilter.isMatchingOrNull(element))) {
					StructuredSelection selection = new StructuredSelection(element);
					treeViewer.setSelection(selection, true);
					return true;
				}
				ITreeContentProvider contentProvider = getTreeContentProvider();
				Object[] children = contentProvider.getChildren(element);
				boolean selectionDone = selectfirstMatching(children);
				if (selectionDone) {
					return true;
				}

			}
			return false;
		}

		private ITreeContentProvider getTreeContentProvider() {
			return contentProvider;
		}
	}

}
