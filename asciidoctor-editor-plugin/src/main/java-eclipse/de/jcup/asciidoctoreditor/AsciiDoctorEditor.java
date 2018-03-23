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
package de.jcup.asciidoctoreditor;

import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorValidationPreferenceConstants.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension2;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import de.jcup.asciidoctoreditor.document.AsciiDoctorFileDocumentProvider;
import de.jcup.asciidoctoreditor.document.AsciiDoctorTextFileDocumentProvider;
import de.jcup.asciidoctoreditor.outline.AsciiDoctorContentOutlinePage;
import de.jcup.asciidoctoreditor.outline.AsciiDoctorEditorTreeContentProvider;
import de.jcup.asciidoctoreditor.outline.AsciiDoctorQuickOutlineDialog;
import de.jcup.asciidoctoreditor.outline.Item;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.script.AsciiDoctorError;
import de.jcup.asciidoctoreditor.script.AsciiDoctorHeadline;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModel;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModelBuilder;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModelException;
import de.jcup.asciidoctoreditor.script.parser.validator.AsciiDoctorEditorValidationErrorLevel;
import de.jcup.asciidoctoreditor.toolbar.NewTableInsertAction;
import de.jcup.asciidoctoreditor.toolbar.RebuildAsciiDocViewAction;
import de.jcup.asciidoctoreditor.toolbar.ToggleLayoutAction;
import de.jcup.asciidoctoreditor.toolbar.ToggleTOCAction;

@AdaptedFromEGradle
public class AsciiDoctorEditor extends TextEditor implements StatusMessageSupport, IResourceChangeListener {

	private static final int INITIAL_LAYOUT_ORIENTATION = SWT.HORIZONTAL;

	/** The EDITOR_ID of this editor as defined in plugin.xml */
	public static final String EDITOR_ID = "asciidoctoreditor.editors.AsciiDoctorEditor";

	/** The COMMAND_ID of the editor context menu */
	public static final String EDITOR_CONTEXT_MENU_ID = EDITOR_ID + ".context";

	/** The COMMAND_ID of the editor ruler context menu */
	public static final String EDITOR_RULER_CONTEXT_MENU_ID = EDITOR_CONTEXT_MENU_ID + ".ruler";

	private static final AsciiDoctorScriptModel FALLBACK_MODEL = new AsciiDoctorScriptModel();
	private AsciiDoctorWrapper asciidoctorWrapper;
	private String bgColor;
	private Browser browser;
	private String fgColor;
	private boolean ignoreNextCaretMove;
	private int lastCaretPosition;
	private AsciiDoctorScriptModelBuilder modelBuilder;

	private Object monitor = new Object();

	private AsciiDoctorContentOutlinePage outlinePage;

	private boolean quickOutlineOpened;

	private File tempADFile;

	private SashForm sashForm;

	private Composite topComposite;

	private CoolBarManager coolBarManager;

	public AsciiDoctorEditor() {
		setSourceViewerConfiguration(new AsciiDoctorSourceViewerConfiguration(this));
		this.modelBuilder = new AsciiDoctorScriptModelBuilder();
		asciidoctorWrapper = new AsciiDoctorWrapper();
	}

	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		return super.createSourceViewer(parent, ruler, styles);
	}

	@Override
	public void createPartControl(Composite parent) {

		GridLayout topGridLayout = new GridLayout();
		topGridLayout.numColumns = 1;
		topGridLayout.marginWidth=0;
		topGridLayout.marginHeight=0;
		topGridLayout.horizontalSpacing=0;
		topGridLayout.verticalSpacing=0;

		/* new composite - takes full place */
		topComposite = new Composite(parent, SWT.NONE);
		topComposite.setLayout(topGridLayout);

		createToolbar();

		GridData sashGD = new GridData(GridData.FILL_BOTH);
		sashForm = new SashForm(topComposite, INITIAL_LAYOUT_ORIENTATION);
		sashForm.setLayoutData(sashGD);
		sashForm.setSashWidth(5);

		super.createPartControl(sashForm);

		initBrowser(sashForm);

		Control adapter = getAdapter(Control.class);
		if (adapter instanceof StyledText) {
			StyledText text = (StyledText) adapter;
			text.addCaretListener(new AsciiDoctorEditorCaretListener());
		}

		activateAsciiDoctorEditorContext();

		/*
		 * register as resource change listener to provide marker change
		 * listening
		 */
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);

		setTitleImageInitial();
		updateAsciiDocView();
	}

	protected void createToolbar() {
		coolBarManager = new CoolBarManager(SWT.FLAT|SWT.HORIZONTAL);
		CoolBar coolbar = coolBarManager.createControl(topComposite);
		GridData toolbarGD = new GridData(GridData.FILL_HORIZONTAL);

		coolbar.setLayoutData(toolbarGD);

		IToolBarManager asciiDocActionToolBar = new ToolBarManager(coolBarManager.getStyle());
		asciiDocActionToolBar.add(new NewTableInsertAction(this));

		IToolBarManager uiActionToolBar = new ToolBarManager(coolBarManager.getStyle());
		uiActionToolBar.add(new RebuildAsciiDocViewAction(this));
		uiActionToolBar.add(new ToggleLayoutAction(this));
		uiActionToolBar.add(new ToggleTOCAction(this));

		// Add to the cool bar manager
		coolBarManager.add(new ToolBarContributionItem(asciiDocActionToolBar, "asciiDocEditor.toolbar.asciiDoc"));
		coolBarManager.add(new ToolBarContributionItem(uiActionToolBar, "asciiDocEditor.toolbar.ui"));
		coolBarManager.update(true);

	}

	public void setVerticalSplit(boolean verticalSplit) {
		/*
		 * don't be confused: SWT.HOROIZONTAL will setup editor on top and view
		 * on bottom - so its vertical...
		 */
		int wanted;
		if (verticalSplit) {
			wanted = SWT.HORIZONTAL;
		} else {
			wanted = SWT.VERTICAL;
		}
		int current = sashForm.getOrientation();
		if (current == wanted) {
			return;
		}
		sashForm.setOrientation(wanted);
	}

	public boolean isVerticalSplit() {
		int orientation;
		if (sashForm == null) {
			orientation = INITIAL_LAYOUT_ORIENTATION;
		} else {
			orientation = sashForm.getOrientation();
		}
		return orientation == SWT.HORIZONTAL;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (browser != null) {
			if (!browser.isDisposed()) {
				browser.dispose();
			}
		}
		asciidoctorWrapper.dispose();
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	public AsciiDoctorHeadline findAsciiDoctorFunction(String functionName) {
		if (functionName == null) {
			return null;
		}
		AsciiDoctorScriptModel model = buildModelWithoutValidation();
		Collection<AsciiDoctorHeadline> functions = model.getHeadlines();
		for (AsciiDoctorHeadline function : functions) {
			if (functionName.equals(function.getName())) {
				return function;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (AsciiDoctorEditor.class.equals(adapter)) {
			return (T) this;
		}
		if (IContentOutlinePage.class.equals(adapter)) {
			return (T) getOutlinePage();
		}
		if (ColorManager.class.equals(adapter)) {
			return (T) getColorManager();
		}
		if (IFile.class.equals(adapter)) {
			IEditorInput input = getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFileEditorInput feditorInput = (IFileEditorInput) input;
				return (T) feditorInput.getFile();
			}
			return null;
		}
		if (ISourceViewer.class.equals(adapter)) {
			return (T) getSourceViewer();
		}
		if (StatusMessageSupport.class.equals(adapter)) {
			return (T) this;
		}
		if (ITreeContentProvider.class.equals(adapter) || AsciiDoctorEditorTreeContentProvider.class.equals(adapter)) {
			if (outlinePage == null) {
				return null;
			}
			return (T) outlinePage.getContentProvider();
		}
		return super.getAdapter(adapter);
	}

	public String getBackGroundColorAsWeb() {
		ensureColorsFetched();
		return bgColor;
	}

	public IDocument getDocument() {
		return getDocumentProvider().getDocument(getEditorInput());
	}

	public String getForeGroundColorAsWeb() {
		ensureColorsFetched();
		return fgColor;
	}

	public Item getItemAt(int offset) {
		if (outlinePage == null) {
			return null;
		}
		AsciiDoctorEditorTreeContentProvider contentProvider = outlinePage.getContentProvider();
		if (contentProvider == null) {
			return null;
		}
		Item item = contentProvider.tryToFindByOffset(offset);
		return item;
	}

	public Item getItemAtCarretPosition() {
		return getItemAt(lastCaretPosition);
	}

	public AsciiDoctorContentOutlinePage getOutlinePage() {
		if (outlinePage == null) {
			outlinePage = new AsciiDoctorContentOutlinePage(this);
		}
		return outlinePage;
	}

	public AsciiDoctorEditorPreferences getPreferences() {
		return AsciiDoctorEditorPreferences.getInstance();
	}

	public void handleColorSettingsChanged() {
		// done like in TextEditor for spelling
		ISourceViewer viewer = getSourceViewer();
		SourceViewerConfiguration configuration = getSourceViewerConfiguration();
		if (viewer instanceof ISourceViewerExtension2) {
			ISourceViewerExtension2 viewerExtension2 = (ISourceViewerExtension2) viewer;
			viewerExtension2.unconfigure();
			if (configuration instanceof AsciiDoctorSourceViewerConfiguration) {
				AsciiDoctorSourceViewerConfiguration gconf = (AsciiDoctorSourceViewerConfiguration) configuration;
				gconf.updateTextScannerDefaultColorToken();
			}
			viewer.configure(configuration);
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		if (site == null) {
			return;
		}
		IWorkbenchPage page = site.getPage();
		if (page == null) {
			return;
		}

		// workaround to show action set for block mode etc.
		// https://www.eclipse.org/forums/index.php/t/366630/
		page.showActionSet("org.eclipse.ui.edit.text.actionSet.presentation");

	}

	/**
	 * Opens quick outline
	 */
	public void openQuickOutline() {
		synchronized (monitor) {
			if (quickOutlineOpened) {
				/*
				 * already opened - this is in future the anker point for
				 * ctrl+o+o...
				 */
				return;
			}
			quickOutlineOpened = true;
		}
		Shell shell = getEditorSite().getShell();
		AsciiDoctorScriptModel model = buildModelWithoutValidation();
		AsciiDoctorQuickOutlineDialog dialog = new AsciiDoctorQuickOutlineDialog(this, shell, "Quick outline");
		dialog.setInput(model);

		dialog.open();
		synchronized (monitor) {
			quickOutlineOpened = false;
		}
	}

	public void openSelectedTreeItemInEditor(ISelection selection, boolean grabFocus) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object firstElement = ss.getFirstElement();
			if (firstElement instanceof Item) {
				Item item = (Item) firstElement;
				int offset = item.getOffset();
				int length = item.getLength();
				if (length == 0) {
					/* fall back */
					length = 1;
				}
				ignoreNextCaretMove = true;
				selectAndReveal(offset, length);
				if (grabFocus) {
					setFocus();
				}
			}
		}
	}

	/**
	 * Does rebuild the outline - this is done asynchronous
	 */
	public void rebuildOutline() {

		String text = getDocumentText();

		IPreferenceStore store = AsciiDoctorEditorUtil.getPreferences().getPreferenceStore();

		boolean validateBlocks = store.getBoolean(VALIDATE_BLOCK_STATEMENTS.getId());
		boolean validateDo = store.getBoolean(VALIDATE_DO_STATEMENTS.getId());
		boolean validateIf = store.getBoolean(VALIDATE_IF_STATEMENTS.getId());
		boolean validateFunctions = store.getBoolean(VALIDATE_FUNCTION_STATEMENTS.getId());
		String errorLevelId = store.getString(VALIDATE_ERROR_LEVEL.getId());
		AsciiDoctorEditorValidationErrorLevel errorLevel = AsciiDoctorEditorValidationErrorLevel.fromId(errorLevelId);

		boolean debugMode = Boolean.parseBoolean(System.getProperty("asciidoctoreditor.debug.enabled"));

		modelBuilder.setIgnoreBlockValidation(!validateBlocks);
		modelBuilder.setIgnoreDoValidation(!validateDo);
		modelBuilder.setIgnoreIfValidation(!validateIf);
		modelBuilder.setIgnoreFunctionValidation(!validateFunctions);

		modelBuilder.setDebug(debugMode);

		EclipseUtil.safeAsyncExec(new Runnable() {

			@Override
			public void run() {
				AsciiDoctorEditorUtil.removeScriptErrors(AsciiDoctorEditor.this);

				AsciiDoctorScriptModel model;
				try {
					model = modelBuilder.build(text);
				} catch (AsciiDoctorScriptModelException e) {
					AsciiDoctorEditorUtil.logError("Was not able to build validation model", e);
					model = FALLBACK_MODEL;
				}

				getOutlinePage().rebuild(model);

				if (model.hasErrors()) {
					int severity;
					if (AsciiDoctorEditorValidationErrorLevel.INFO.equals(errorLevel)) {
						severity = IMarker.SEVERITY_INFO;
					} else if (AsciiDoctorEditorValidationErrorLevel.WARNING.equals(errorLevel)) {
						severity = IMarker.SEVERITY_WARNING;
					} else {
						severity = IMarker.SEVERITY_ERROR;
					}
					addErrorMarkers(model, severity);
				}
			}
		});
	}

	public void resourceChanged(IResourceChangeEvent event) {
		if (isMarkerChangeForThisEditor(event)) {
			int severity = getSeverity();

			setTitleImageDependingOnSeverity(severity);
		}
	}

	public void selectFunction(String text) {
		System.out.println("should select functin:" + text);

	}

	public void setErrorMessage(String message) {
		super.setStatusLineErrorMessage(message);
	}

	/**
	 * Toggles comment of current selected lines
	 */
	public void toggleComment() {
		ISelection selection = getSelectionProvider().getSelection();
		if (!(selection instanceof TextSelection)) {
			return;
		}
		IDocumentProvider dp = getDocumentProvider();
		IDocument doc = dp.getDocument(getEditorInput());
		TextSelection ts = (TextSelection) selection;
		int startLine = ts.getStartLine();
		int endLine = ts.getEndLine();

		/* do comment /uncomment */
		for (int i = startLine; i <= endLine; i++) {
			IRegion info;
			try {
				info = doc.getLineInformation(i);
				int offset = info.getOffset();
				String line = doc.get(info.getOffset(), info.getLength());
				StringBuilder foundCode = new StringBuilder();
				StringBuilder whitespaces = new StringBuilder();
				for (int j = 0; j < line.length(); j++) {
					char ch = line.charAt(j);
					if (Character.isWhitespace(ch)) {
						if (foundCode.length() == 0) {
							whitespaces.append(ch);
						}
					} else {
						foundCode.append(ch);
					}
					if (foundCode.length() > 0) {
						break;
					}
				}
				int whitespaceOffsetAdd = whitespaces.length();
				if ("//".equals(foundCode.toString())) {
					/* comment before */
					doc.replace(offset + whitespaceOffsetAdd, 1, "");
				} else {
					/* not commented */
					doc.replace(offset, 0, "//");
				}

			} catch (BadLocationException e) {
				/* ignore and continue */
				continue;
			}

		}
		/* reselect */
		int selectionStartOffset;
		try {
			selectionStartOffset = doc.getLineOffset(startLine);
			int endlineOffset = doc.getLineOffset(endLine);
			int endlineLength = doc.getLineLength(endLine);
			int endlineLastPartOffset = endlineOffset + endlineLength;
			int length = endlineLastPartOffset - selectionStartOffset;

			ISelection newSelection = new TextSelection(selectionStartOffset, length);
			getSelectionProvider().setSelection(newSelection);
		} catch (BadLocationException e) {
			/* ignore */
		}
	}

	public void updateAsciiDocView() {
		if (browser == null) {
			return;
		}
		if (browser.isDisposed()) {
			return;
		}
		if (tempADFile == null) {
			return;
		}
		buildTemporaryHTMLFile();
		browser.refresh();

	}

	public void validate() {
		rebuildOutline();
	}

	protected void buildTemporaryHTMLFile() {
		String content = null;
		File editorFile = getEditorFile();
		try {
			if (editorFile != null) {
				boolean handledError = false;
				try {
					asciidoctorWrapper.convertToHTML(editorFile);

					File generatedFile = asciidoctorWrapper.getTempFileFor(getEditorFile(), false);
					try (BufferedReader br = new BufferedReader(new FileReader(generatedFile))) {
						String line = null;
						StringBuilder htmlSB = new StringBuilder();
						while ((line = br.readLine()) != null) {
							htmlSB.append(line);
							htmlSB.append("\n");
						}
						content = htmlSB.toString();
					} catch (IOException e) {
						AsciiDoctorEditorUtil.logError("Was not able to build new full", e);
					}

				} catch (RuntimeException e) {
					AsciiDoctorEditorUtil.logWarning("Failed to convert - use fallback to simple text. EditorFile was:"
							+ editorFile + ". Origin message was:" + e.getMessage());
					handledError = true;
				}
				if (!handledError && (content == null || "null".equals(content))) {
					AsciiDoctorEditorUtil.logWarning(
							"File conversion failed. Use fallback to simple text. EditorFile was:" + editorFile);
					content = null;
				}
			}
			if (content == null) {
				content = asciidoctorWrapper.convertToHTML(getDocumentText());
			}
			String html = asciidoctorWrapper.buildHTMLWithCSS(content);
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempADFile))) {
				bw.write(html);
				bw.close();

			} catch (IOException e1) {
				AsciiDoctorEditorUtil.logError("Was not able to setup", e1);
			}

		} catch (RuntimeException e) {
			AsciiDoctorEditorUtil.logError("Was not able to setup, editorFile was:" + editorFile, e);
		}
	}

	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		setDocumentProvider(createDocumentProvider(input));
		super.doSetInput(input);

		rebuildOutline();
		updateAsciiDocView();
	}

	@Override
	protected void editorSaved() {
		super.editorSaved();
		rebuildOutline();
		updateAsciiDocView();
	}

	protected File getEditorFile() {
		IEditorInput input = getEditorInput();
		return getEditorFile(input);
	}

	protected File getEditorFile(IEditorInput input) {
		File editorFile = null;
		if (input instanceof FileEditorInput) {
			FileEditorInput finput = (FileEditorInput) input;
			IFile iFile = finput.getFile();
			try {
				editorFile = EclipseResourceHelper.DEFAULT.toFile(iFile);
			} catch (CoreException e) {
				AsciiDoctorEditorUtil.logError("Was not able to fetch file of current editor", e);
			}
		}
		return editorFile;
	}

	protected void initBrowser(SashForm sashForm) {
		tempADFile = asciidoctorWrapper.getTempFileFor(getEditorFile(), true);
		try {
			browser = new Browser(sashForm, SWT.CENTER);
			if (tempADFile == null || !tempADFile.exists()) {
				/*
				 * can happen when eclipse restarts and editor opens - new temp
				 * folder shows to non existing file...
				 */
				buildTemporaryHTMLFile();
			}
			if (tempADFile == null || !tempADFile.exists()) {
				/* it was not possible to recreate the temp ad file */
				browser.setText("<html><h1>No temporary file available! Cannot render data</h1></html>");
			} else {
				EclipseUtil.safeAsyncExec(new Runnable() {

					@Override
					public void run() {
						try {
							browser.setUrl(tempADFile.toURI().toURL().toString());

						} catch (MalformedURLException e) {
							/* FIXME ATR, 15.03.2018: better error handling */
							e.printStackTrace();
							browser.setText("URL problems:" + e.getMessage());
						}
					}
				});
			}

		} catch (SWTError e) {
			MessageBox messageBox = new MessageBox(EclipseUtil.getActiveWorkbenchShell(), SWT.ICON_ERROR | SWT.OK);
			messageBox.setMessage("Browser cannot be initialized.");
			messageBox.setText("Exit");
			messageBox.open();
			return;
		}
		if (tempADFile == null) {
			if (browser != null) {
				browser.setText("<html><h1>No temporary file available! Cannot render data</h1></html>");
			}
		}
	}

	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		setEditorContextMenuId(EDITOR_CONTEXT_MENU_ID);
		setRulerContextMenuId(EDITOR_RULER_CONTEXT_MENU_ID);
	}

	/**
	 * Get document text - safe way.
	 * 
	 * @return string, never <code>null</code>
	 */
	String getDocumentText() {
		IDocument doc = getDocument();
		if (doc == null) {
			return "";
		}
		return doc.get();
	}

	void setTitleImageDependingOnSeverity(int severity) {
		setTitleImage(
				EclipseUtil.getImage("icons/" + getTitleImageName(severity), AsciiDoctorEditorActivator.PLUGIN_ID));
	}

	private void activateAsciiDoctorEditorContext() {
		IContextService contextService = getSite().getService(IContextService.class);
		if (contextService != null) {
			contextService.activateContext(EDITOR_CONTEXT_MENU_ID);
		}
	}

	private void addErrorMarkers(AsciiDoctorScriptModel model, int severity) {
		if (model == null) {
			return;
		}
		IDocument document = getDocument();
		if (document == null) {
			return;
		}
		Collection<AsciiDoctorError> errors = model.getErrors();
		for (AsciiDoctorError error : errors) {
			int startPos = error.getStart();
			int line;
			try {
				line = document.getLineOfOffset(startPos);
			} catch (BadLocationException e) {
				EclipseUtil.logError("Cannot get line offset for " + startPos, e);
				line = 0;
			}
			AsciiDoctorEditorUtil.addScriptError(this, line, error, severity);
		}

	}

	private AsciiDoctorScriptModel buildModelWithoutValidation() {
		String text = getDocumentText();
		/*
		 * FIXME ATR, 15.03.2018: clean up this ignore stuff... is this still
		 * useful, will we have a validation for asciidoc files?
		 */
		/* for quick outline create own model and ignore any validations */
		modelBuilder.setIgnoreBlockValidation(true);
		modelBuilder.setIgnoreDoValidation(true);
		modelBuilder.setIgnoreIfValidation(true);
		modelBuilder.setIgnoreFunctionValidation(true);

		AsciiDoctorScriptModel model;
		try {
			model = modelBuilder.build(text);
		} catch (AsciiDoctorScriptModelException e) {
			AsciiDoctorEditorUtil.logError("Was not able to build script model", e);
			model = FALLBACK_MODEL;
		}
		return model;
	}

	private IDocumentProvider createDocumentProvider(IEditorInput input) {
		if (input instanceof FileStoreEditorInput) {
			return new AsciiDoctorTextFileDocumentProvider();
		} else {
			return new AsciiDoctorFileDocumentProvider();
		}
	}

	private void ensureColorsFetched() {
		if (bgColor == null || fgColor == null) {

			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer == null) {
				return;
			}
			StyledText textWidget = sourceViewer.getTextWidget();
			if (textWidget == null) {
				return;
			}

			/*
			 * TODO ATR, 03.02.2017: there should be an easier approach to get
			 * editors back and foreground, without syncexec
			 */
			EclipseUtil.getSafeDisplay().syncExec(new Runnable() {

				@Override
				public void run() {
					bgColor = ColorUtil.convertToHexColor(textWidget.getBackground());
					fgColor = ColorUtil.convertToHexColor(textWidget.getForeground());
				}
			});
		}

	}

	private ColorManager getColorManager() {
		return AsciiDoctorEditorActivator.getDefault().getColorManager();
	}

	private int getSeverity() {
		IEditorInput editorInput = getEditorInput();
		if (editorInput == null) {
			return IMarker.SEVERITY_INFO;
		}
		try {
			final IResource resource = ResourceUtil.getResource(editorInput);
			if (resource == null) {
				return IMarker.SEVERITY_INFO;
			}
			int severity = resource.findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			return severity;
		} catch (CoreException e) {
			// Might be a project that is not open
		}
		return IMarker.SEVERITY_INFO;
	}

	private String getTitleImageName(int severity) {
		switch (severity) {
		case IMarker.SEVERITY_ERROR:
			return "asciidoctor-editor-with-error.png";
		case IMarker.SEVERITY_WARNING:
			return "asciidoctor-editor-with-warning.png";
		case IMarker.SEVERITY_INFO:
			return "asciidoctor-editor-with-info.png";
		default:
			return "asciidoctor-editor.png";
		}
	}

	private boolean isMarkerChangeForThisEditor(IResourceChangeEvent event) {
		IResource resource = ResourceUtil.getResource(getEditorInput());
		if (resource == null) {
			return false;
		}
		IPath path = resource.getFullPath();
		if (path == null) {
			return false;
		}
		IResourceDelta eventDelta = event.getDelta();
		if (eventDelta == null) {
			return false;
		}
		IResourceDelta delta = eventDelta.findMember(path);
		if (delta == null) {
			return false;
		}
		boolean isMarkerChangeForThisResource = (delta.getFlags() & IResourceDelta.MARKERS) != 0;
		return isMarkerChangeForThisResource;
	}

	/**
	 * Resolves resource from current editor input.
	 * 
	 * @return file resource or <code>null</code>
	 */
	private IResource resolveResource() {
		IEditorInput input = getEditorInput();
		if (!(input instanceof IFileEditorInput)) {
			return null;
		}
		return ((IFileEditorInput) input).getFile();
	}

	/**
	 * Set initial title image dependent on current marker severity. This will
	 * mark error icon on startup time which is not handled by resource change
	 * handling, because having no change...
	 */
	private void setTitleImageInitial() {
		IResource resource = resolveResource();
		if (resource != null) {
			try {
				int maxSeverity = resource.findMaxProblemSeverity(null, true, IResource.DEPTH_INFINITE);
				setTitleImageDependingOnSeverity(maxSeverity);
			} catch (CoreException e) {
				/* ignore */
			}
		}
	}

	private class AsciiDoctorEditorCaretListener implements CaretListener {

		@Override
		public void caretMoved(CaretEvent event) {
			if (event == null) {
				return;
			}
			lastCaretPosition = event.caretOffset;
			if (ignoreNextCaretMove) {
				ignoreNextCaretMove = false;
				return;
			}
			if (outlinePage == null) {
				return;
			}
			outlinePage.onEditorCaretMoved(event.caretOffset);
		}

	}

	public void openInclude(String fileName) {
		IWorkbenchPage activePage = EclipseUtil.getActivePage();
		File file = new File(getEditorFile().getParentFile(), fileName);
		if (!file.exists()) {
			MessageDialog.openWarning(EclipseUtil.getActiveWorkbenchShell(), "Not able to load",
					"Cannot open " + fileName);
			return;
		}
		try {
			IDE.openEditor(activePage, file.toURI(), AsciiDoctorEditor.EDITOR_ID, true);
			return;
		} catch (PartInitException e) {
			AsciiDoctorEditorUtil.logError("Not able to open include", e);
		}

	}

	public void resetCache() {
		asciidoctorWrapper.resetCaches();
	}

	public void setTOCShown(boolean shown) {
		asciidoctorWrapper.setTocVisible(shown);
		updateAsciiDocView();
	}

	public boolean isTOCShown() {
		return asciidoctorWrapper.isTocVisible();
	}
}
