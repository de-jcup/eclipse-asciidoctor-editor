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

import static de.jcup.asciidoctoreditor.EclipseUtil.*;
import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorValidationPreferenceConstants.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.ErrorDialog;
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

import de.jcup.asciidoctoreditor.BrowserAccess.BrowserContentInitializer;
import de.jcup.asciidoctoreditor.document.AsciiDoctorFileDocumentProvider;
import de.jcup.asciidoctoreditor.document.AsciiDoctorTextFileDocumentProvider;
import de.jcup.asciidoctoreditor.outline.AsciiDoctorContentOutlinePage;
import de.jcup.asciidoctoreditor.outline.AsciiDoctorEditorTreeContentProvider;
import de.jcup.asciidoctoreditor.outline.AsciiDoctorQuickOutlineDialog;
import de.jcup.asciidoctoreditor.outline.Item;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferenceConstants;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.script.AsciiDoctorError;
import de.jcup.asciidoctoreditor.script.AsciiDoctorErrorBuilder;
import de.jcup.asciidoctoreditor.script.AsciiDoctorHeadline;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModel;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModelBuilder;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModelException;
import de.jcup.asciidoctoreditor.script.parser.validator.AsciiDoctorEditorValidationErrorLevel;
import de.jcup.asciidoctoreditor.toolbar.AddErrorDebugAction;
import de.jcup.asciidoctoreditor.toolbar.BoldFormatAction;
import de.jcup.asciidoctoreditor.toolbar.ChangeLayoutAction;
import de.jcup.asciidoctoreditor.toolbar.InsertAdmonitionAction;
import de.jcup.asciidoctoreditor.toolbar.InsertSectionTitleAction;
import de.jcup.asciidoctoreditor.toolbar.ItalicFormatAction;
import de.jcup.asciidoctoreditor.toolbar.JumpToTopOfAsciiDocViewAction;
import de.jcup.asciidoctoreditor.toolbar.MonospacedFormatAction;
import de.jcup.asciidoctoreditor.toolbar.NewCodeBlockInsertAction;
import de.jcup.asciidoctoreditor.toolbar.NewLinkInsertAction;
import de.jcup.asciidoctoreditor.toolbar.NewTableInsertAction;
import de.jcup.asciidoctoreditor.toolbar.OpenInExternalBrowserAction;
import de.jcup.asciidoctoreditor.toolbar.RebuildAsciiDocViewAction;
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

	private String fgColor;
	private boolean ignoreNextCaretMove;
	private int lastCaretPosition;
	private AsciiDoctorScriptModelBuilder modelBuilder;

	private Object monitor = new Object();

	private AsciiDoctorContentOutlinePage outlinePage;

	private boolean quickOutlineOpened;

	private File temporaryInternalPreviewFile;

	private BrowserAccess browserAccess;

	private SashForm sashForm;

	private Composite topComposite;

	protected CoolBarManager coolBarManager;

	private boolean previewVisible;

	private Semaphore outputBuildSemaphore = new Semaphore(1);

	private File temporaryExternalPreviewFile;

	private ContentTransformer contentTransformer;

	private long editorTempIdentifier;

	private File editorFile;

	public AsciiDoctorEditor() {
		this.editorTempIdentifier = System.nanoTime();
		setSourceViewerConfiguration(createSourceViewerConfig());
		this.modelBuilder = new AsciiDoctorScriptModelBuilder();
		asciidoctorWrapper = new AsciiDoctorWrapper(editorTempIdentifier, AsciiDoctorEclipseLogAdapter.INSTANCE);

		contentTransformer = createCustomContentTransformer();
		if (contentTransformer == null) {
			contentTransformer = NotChangingContentTransformer.INSTANCE;
		}
	}

	protected SourceViewerConfiguration createSourceViewerConfig() {
		return new AsciiDoctorSourceViewerConfiguration(this);
	}

	protected ContentTransformer createCustomContentTransformer() {
		return null;
	}

	/**
	 * 
	 * @return file or <code>null</code>
	 */
	public File getTemporaryExternalPreviewFile() {
		return temporaryExternalPreviewFile;
	}

	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		return super.createSourceViewer(parent, ruler, styles);
	}

	@Override
	public void createPartControl(Composite parent) {

		GridLayout topGridLayout = new GridLayout();
		topGridLayout.numColumns = 1;
		topGridLayout.marginWidth = 0;
		topGridLayout.marginHeight = 0;
		topGridLayout.horizontalSpacing = 0;
		topGridLayout.verticalSpacing = 0;

		/* new composite - takes full place */
		topComposite = new Composite(parent, SWT.NONE);
		topComposite.setLayout(topGridLayout);

		createToolbar();

		GridData sashGD = new GridData(GridData.FILL_BOTH);
		sashForm = new SashForm(topComposite, INITIAL_LAYOUT_ORIENTATION);
		sashForm.setLayoutData(sashGD);
		sashForm.setSashWidth(5);

		super.createPartControl(sashForm);

		browserAccess = new BrowserAccess(sashForm);
		initPreview(sashForm);
		initToolbar(); // init after browser creation so we toolbar icons are
						// set depending on browser visible or not...

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
	}

	protected void createToolbar() {
		coolBarManager = new CoolBarManager(SWT.FLAT | SWT.HORIZONTAL);
		CoolBar coolbar = coolBarManager.createControl(topComposite);
		GridData toolbarGD = new GridData(GridData.FILL_HORIZONTAL);

		coolbar.setLayoutData(toolbarGD);
	}

	protected void initToolbar() {

		IToolBarManager asciiDocToolBarManager = new ToolBarManager(coolBarManager.getStyle());
		asciiDocToolBarManager.add(new InsertSectionTitleAction(this));

		asciiDocToolBarManager.add(new ItalicFormatAction(this));
		asciiDocToolBarManager.add(new BoldFormatAction(this));
		asciiDocToolBarManager.add(new MonospacedFormatAction(this));

		asciiDocToolBarManager.add(new NewTableInsertAction(this));
		asciiDocToolBarManager.add(new NewLinkInsertAction(this));
		asciiDocToolBarManager.add(new InsertAdmonitionAction(this));
		asciiDocToolBarManager.add(new NewCodeBlockInsertAction(this));

		IToolBarManager viewToolBarManager = new ToolBarManager(coolBarManager.getStyle());
		viewToolBarManager.add(new ChangeLayoutAction(this));
		viewToolBarManager.add(new RebuildAsciiDocViewAction(this));
		viewToolBarManager.add(new ToggleTOCAction(this));
		viewToolBarManager.add(new Separator("simple"));
		viewToolBarManager.add(new JumpToTopOfAsciiDocViewAction(this));

		IToolBarManager otherToolBarManager = new ToolBarManager(coolBarManager.getStyle());
		otherToolBarManager.add(new OpenInExternalBrowserAction(this));

		// Add to the cool bar manager
		coolBarManager.add(new ToolBarContributionItem(asciiDocToolBarManager, "asciiDocEditor.toolbar.asciiDoc"));
		coolBarManager.add(new ToolBarContributionItem(viewToolBarManager, "asciiDocEditor.toolbar.view"));
		coolBarManager.add(new ToolBarContributionItem(otherToolBarManager, "asciiDocEditor.toolbar.other"));

		if (EclipseDevelopmentSettings.DEBUG_TOOLBAR_ENABLED) {
			IToolBarManager debugToolBar = new ToolBarManager(coolBarManager.getStyle());
			debugToolBar.add(new AddErrorDebugAction(this));
			coolBarManager.add(new ToolBarContributionItem(debugToolBar, "asciiDocEditor.toolbar.debug"));
		}

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
		browserAccess.dispose();

		asciidoctorWrapper.dispose();
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	public AsciiDoctorHeadline findAsciiDoctorHeadline(String functionName) {
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

	public int getLastCaretPosition() {
		return lastCaretPosition;
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
			} else if (configuration instanceof AsciiDoctorPlantUMLSourceViewerConfiguration) {
				AsciiDoctorPlantUMLSourceViewerConfiguration gconf = (AsciiDoctorPlantUMLSourceViewerConfiguration) configuration;
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

		boolean validateGraphviz = store.getBoolean(VALIDATE_GRAPHVIZ.getId());
		String errorLevelId = store.getString(VALIDATE_ERROR_LEVEL.getId());
		AsciiDoctorEditorValidationErrorLevel errorLevel = AsciiDoctorEditorValidationErrorLevel.fromId(errorLevelId);

		if (validateGraphviz) {
			modelBuilder.setGraphVizCheckSupport(CheckGraphviz.INSTANCE);
		} else {
			modelBuilder.setGraphVizCheckSupport(null);
		}
		safeAsyncExec(() -> {
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
					if (foundCode.length() > 1) {
						break;
					}
				}
				int whitespaceOffsetAdd = whitespaces.length();
				if ("//".equals(foundCode.toString())) {
					/* comment before */
					doc.replace(offset + whitespaceOffsetAdd, 2, "");
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

	public void refreshAsciiDocView() {
		showRebuildingInPreviewAndTriggerFullHTMLRebuildAsJob();
	}

	public void validate() {
		rebuildOutline();
	}

	private void fullBuildTemporaryHTMLFilesAndShowAfter(IProgressMonitor monitor) {
		String htmlInternalPreview = null;
		String htmlExternalBrowser = null;
		if (isCanceled(monitor)) {
			return;
		}
		try {
			safeAsyncExec(() -> AsciiDoctorEditorUtil.removeScriptErrors(AsciiDoctorEditor.this));

			File editorFileOrNull = getEditorFileOrNull();

			String content = null;
			File fileToConvertIntoHTML = null;
			if (editorFileOrNull == null) {
				String asciiDoc = getDocumentText();
				fileToConvertIntoHTML = resolveFileToConvertToHTMLAndConvertBeforeWhenNecessary(asciiDoc);
			} else {
				fileToConvertIntoHTML = resolveFileToConvertToHTMLAndConvertBeforeWhenNecessary(editorFileOrNull);
			}
			if (fileToConvertIntoHTML == null) {
				return;
			}
			if (isCanceled(monitor)) {
				return;
			}

			/* content exists as simple file */
			asciidoctorWrapper.convertToHTML(fileToConvertIntoHTML);
			if (isCanceled(monitor)) {
				return;
			}
			content = readFileCreatedByAsciiDoctor(fileToConvertIntoHTML);
			int refreshAutomaticallyInSeconds = AsciiDoctorEditorPreferences.getInstance()
					.getAutoRefreshInSecondsForExternalBrowser();
			htmlInternalPreview = asciidoctorWrapper.buildHTMLWithCSS(content, 0);
			htmlExternalBrowser = asciidoctorWrapper.buildHTMLWithCSS(content, refreshAutomaticallyInSeconds);
			if (isCanceled(monitor)) {
				return;
			}
			try {
				AsciiDocStringUtils.writeTextToUTF8File(htmlInternalPreview, temporaryInternalPreviewFile);
				AsciiDocStringUtils.writeTextToUTF8File(htmlExternalBrowser, temporaryExternalPreviewFile);

			} catch (IOException e1) {
				AsciiDoctorEditorUtil.logError("Was not able to save temporary files for preview!", e1);
			}

		} catch (Throwable e) {
			/*
			 * Normally I would do a catch(Exception e), but we must use
			 * catch(Throwable t) here. Reason (at least eclipse neon) we got
			 * full eclipse editor tab freeze problem when a jruby class not
			 * found error occurs!
			 */
			/*
			 * This means the ASCIIDOCTOR wrapper was not able to convert - so
			 * we have to clean the former ouptput and show up a marker for
			 * complete file
			 */
			StringBuilder htmlSb = new StringBuilder();
			htmlSb.append("<h4");
			if (e.getClass().getName().startsWith("org.asciidoctor")) {
				htmlSb.append("AsciiDoctor error");
			} else {
				htmlSb.append("Unknown error");
			}
			htmlSb.append("</h4");

			safeAsyncExec(() -> {

				String errorMessage = e.getClass().getSimpleName() + ": " + SimpleExceptionUtils.getRootMessage(e);

				AsciiDoctorErrorBuilder builder = new AsciiDoctorErrorBuilder();
				AsciiDoctorError error = builder.build(errorMessage);
				browserAccess.safeBrowserSetText(htmlSb.toString());
				AsciiDoctorEditorUtil.addScriptError(AsciiDoctorEditor.this, -1, error, IMarker.SEVERITY_ERROR);
				AsciiDoctorEditorUtil.logError("AsciiDoctor error occured:" + e.getMessage(), e);
			});

		}

	}

	protected File resolveFileToConvertToHTMLAndConvertBeforeWhenNecessary(String asciiDoc) throws IOException {
		File fileToConvertIntoHTML;
		String text;
		if (contentTransformer.isTransforming(asciiDoc)) {
			text = contentTransformer.transform(asciiDoc);
		} else {
			text = asciiDoc;
		}
		fileToConvertIntoHTML = resolveFileToConvertToHTML("no_origin_file_defined", text);
		return fileToConvertIntoHTML;
	}

	protected File resolveFileToConvertToHTMLAndConvertBeforeWhenNecessary(File editorFile) throws IOException {
		if (editorFile == null || !editorFile.exists()) {
			return null;
		}

		String originText = AsciiDocStringUtils.readUTF8FileToString(editorFile);
		if (originText == null) {
			return null;
		}
		if (!contentTransformer.isTransforming(originText)) {
			return editorFile;
		}
		return resolveFileToConvertToHTML(editorFile.getName(), originText);
	}

	public File resolveFileToConvertToHTML(String filename, String text) throws IOException {
		File newTempFile = AsciiDocFileUtils.createTempFileForConvertedContent(editorTempIdentifier, filename);

		String transformed = contentTransformer.transform(text);
		try {
			return AsciiDocStringUtils.writeTextToUTF8File(transformed, newTempFile);
		} catch (IOException e) {
			logError("Was not able to write transformed file:" + filename, e);
			return null;
		}
	}

	protected String readFileCreatedByAsciiDoctor(File fileToConvertIntoHTML) {
		File generatedFile = asciidoctorWrapper.getTempFileFor(fileToConvertIntoHTML, TemporaryFileType.ORIGIN);
		try {
			return AsciiDocStringUtils.readUTF8FileToString(generatedFile);
		} catch (IOException e) {
			AsciiDoctorEditorUtil.logError("Was not able to build new full html variant", e);
			return "";
		}
	}

	@Override
	protected void doSetInput(IEditorInput input) throws CoreException {
		setDocumentProvider(createDocumentProvider(input));
		super.doSetInput(input);

		rebuildOutline();
		if (browserAccess == null) {
			/*
			 * happens when eclipse is starting editors opened before are
			 * initialized. The createPartControl is not already called
			 */
			return;
		}
		showRebuildingInPreviewAndTriggerFullHTMLRebuildAsJob();
	}

	@Override
	protected void editorSaved() {
		super.editorSaved();

		rebuildOutline();

		showRebuildingInPreviewAndTriggerFullHTMLRebuildAsJob();
	}

	protected File getEditorFileOrNull() {
		if (editorFile == null) {
			editorFile = resolveEditorFileOrNull();
		}
		return editorFile;
	}

	protected File resolveEditorFileOrNull() {
		IEditorInput input = getEditorInput();
		File editorFile = null;

		if (input instanceof FileEditorInput) {
			/* standard opening with eclipse IDE inside */
			FileEditorInput finput = (FileEditorInput) input;
			IFile iFile = finput.getFile();
			try {
				editorFile = EclipseResourceHelper.DEFAULT.toFile(iFile);
			} catch (CoreException e) {
				AsciiDoctorEditorUtil.logError("Was not able to fetch file of current editor", e);
			}
		} else if (input instanceof FileStoreEditorInput) {
			/*
			 * command line : eclipse xyz.adoc does use file store editor input
			 * ....
			 */
			FileStoreEditorInput fsInput = (FileStoreEditorInput) input;
			editorFile = fsInput.getAdapter(File.class);
		}
		return editorFile;
	}

	protected PreviewLayout getLayoutMode() {

		String layoutMode = AsciiDoctorEditorPreferences.getInstance()
				.getStringPreference(AsciiDoctorEditorPreferenceConstants.P_EDITOR_NEWEDITOR_PREVIEW_LAYOUT);
		PreviewLayout layout = PreviewLayout.fromId(layoutMode);
		if (layout == null) {
			layout = PreviewLayout.VERTICAL;
		}
		return layout;
	}

	protected void showRebuildingInPreviewAndTriggerFullHTMLRebuildAsJob() {
		showRebuildingInPreviewAndTriggerFullHTMLRebuildAsJob(false);
	}

	protected void showRebuildingInPreviewAndTriggerFullHTMLRebuildAsJob(boolean forceInitialize) {

		if (!forceInitialize && outputBuildSemaphore.availablePermits() == 0) {
			/* already rebuilding -so ignore */
			return;
		}
		boolean initializing = forceInitialize || temporaryInternalPreviewFile == null
				|| !temporaryExternalPreviewFile.exists();

		try {
			outputBuildSemaphore.acquire();
			if (initializing) {
				File previewInitializingFile = new File(AsciiDoctorOSGIWrapper.INSTANCE.getAddonsFolder(),
						"html/initialize/preview_initializing.html");
				boolean previewInitializingFileFound = false;
				try {
					if (previewInitializingFile.exists()) {
						previewInitializingFileFound = true;
					}
					String previewFileURL = previewInitializingFile.toURI().toURL().toExternalForm();
					browserAccess.setUrl(previewFileURL);
				} catch (MalformedURLException e) {
					logError("Preview initializer html file not valid url", e);
				}
				if (!previewInitializingFileFound) {
					browserAccess.safeBrowserSetText("<html><body><h3>Initializing document</h3></body></html>");
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return;
		}
		String jobInfo = null;
		if (initializing) {
			jobInfo = "Asciidoctor editor preview initializing ";
		} else {
			jobInfo = "Asciidoctor editor full rebuild";
		}
		Job job = Job.create(jobInfo, new ICoreRunnable() {

			@Override
			public void run(IProgressMonitor monitor) throws CoreException {
				try {
					monitor.beginTask("Building document " + getSafeFileName(), IProgressMonitor.UNKNOWN);

					fullBuildTemporaryHTMLFilesAndShowAfter(monitor);

					ensureInternalBrowserShowsURL(monitor);

					monitor.done();

				} finally {
					outputBuildSemaphore.release();
				}
			}

			protected String getSafeFileName() {
				if (temporaryInternalPreviewFile == null) {
					return "<unknown>";
				}
				return temporaryInternalPreviewFile.getName();
			}
		});
		job.schedule();
	}

	protected void initPreview(SashForm sashForm) {
		File editorFileOrNull = getEditorFileOrNull();
		if (editorFileOrNull == null) {
			setErrorMessage("Asciidoctor Editor: preview not available because no editor file found");
			return;
		}
		temporaryInternalPreviewFile = asciidoctorWrapper.getTempFileFor(editorFileOrNull,
				TemporaryFileType.INTERNAL_PREVIEW);
		temporaryExternalPreviewFile = asciidoctorWrapper.getTempFileFor(editorFileOrNull,
				TemporaryFileType.EXTERNAL_PREVIEW);

		browserAccess.ensureBrowser(new BrowserContentInitializer() {

			@Override
			public void initialize(Browser browser) {

			}
		});

		showRebuildingInPreviewAndTriggerFullHTMLRebuildAsJob();

		PreviewLayout layout = getLayoutMode();
		if (layout.isExternal()) {
			setPreviewVisible(false);
		} else {
			setPreviewVisible(true);
			setVerticalSplit(layout.isVertical());
		}
	}

	protected boolean isNoPreviewFileGenerated() {
		return temporaryInternalPreviewFile == null || !temporaryInternalPreviewFile.exists();
	}

	protected void ensureInternalBrowserShowsURL(IProgressMonitor monitor) {
		if (!isPreviewVisible()) {
			return;
		}
		if (isCanceled(monitor)) {
			return;
		}
		Thread t = new Thread(new WaitForGeneratedFileAndShowInsideIternalPreviewRunner(monitor));
		String name = "";
		if (temporaryExternalPreviewFile != null) {
			name = temporaryInternalPreviewFile.getName();
		} else {
			name = "undefined";
		}
		t.setName("asciidoctor-editor-ensure:" + name);
		t.start();
	}

	private boolean isNotCanceled(IProgressMonitor monitor) {
		return !isCanceled(monitor);
	}

	private boolean isCanceled(IProgressMonitor monitor) {
		if (monitor == null) {
			return false; // no chance to cancel...
		}
		return monitor.isCanceled();
	}

	private class WaitForGeneratedFileAndShowInsideIternalPreviewRunner implements Runnable {

		private IProgressMonitor monitor;

		WaitForGeneratedFileAndShowInsideIternalPreviewRunner(IProgressMonitor monitor) {
			this.monitor = monitor;
		}

		@Override
		public void run() {
			long start = System.currentTimeMillis();
			boolean aquired = false;
			try {
				while (isNotCanceled(monitor)
						&& (temporaryInternalPreviewFile == null || !temporaryInternalPreviewFile.exists())) {
					if (System.currentTimeMillis() - start > 20000) {
						// after 20 seconds there seems to be no chance to get
						// the generated preview file back
						browserAccess.safeBrowserSetText(
								"<html><body><h3>Preview file generation timed out, so preview not available.</h3></body></html>");
						return;
					}
					Thread.sleep(300);
				}
				aquired = outputBuildSemaphore.tryAcquire(5, TimeUnit.SECONDS);

				safeAsyncExec(() -> {

					try {
						URL url = temporaryInternalPreviewFile.toURI().toURL();
						String foundURL = browserAccess.getUrl();
						try {
							URL formerURL = new URL(browserAccess.getUrl());
							foundURL = formerURL.toExternalForm();
						} catch (MalformedURLException e) {
							/* ignore - about pages etc. */
						}
						String externalForm = url.toExternalForm();
						if (!externalForm.equals(foundURL)) {
							browserAccess.setUrl(externalForm);
						} else {
							browserAccess.refresh();
						}

					} catch (MalformedURLException e) {
						AsciiDoctorEditorUtil.logError("Was not able to use malformed URL", e);
						browserAccess.safeBrowserSetText("<html><body><h3>URL malformed</h3></body></html>");
					}
				});

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} finally {
				if (aquired == true) {
					outputBuildSemaphore.release();
				}
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
		safeAsyncExec(() -> setTitleImage(
				getImage("icons/" + getTitleImageName(severity), AsciiDoctorEditorActivator.PLUGIN_ID)));
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
				logError("Cannot get line offset for " + startPos, e);
				line = 0;
			}
			AsciiDoctorEditorUtil.addScriptError(this, line, error, severity);
		}

	}

	private AsciiDoctorScriptModel buildModelWithoutValidation() {
		String text = getDocumentText();

		AsciiDoctorScriptModel model;
		try {
			model = modelBuilder.build(text);
		} catch (AsciiDoctorScriptModelException e) {
			AsciiDoctorEditorUtil.logError("Was not able to build script model", e);
			model = FALLBACK_MODEL;
		}
		return model;
	}

	protected IDocumentProvider createDocumentProvider(IEditorInput input) {
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

			safeSyncExec(() -> {
				bgColor = ColorUtil.convertToHexColor(textWidget.getBackground());
				fgColor = ColorUtil.convertToHexColor(textWidget.getForeground());
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

	protected String getTitleImageName(int severity) {
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

		File editorFileOrNull = getEditorFileOrNull();
		if (editorFileOrNull == null) {
			MessageDialog.openWarning(getActiveWorkbenchShell(), "Not able to resolve editor file",
					"Not able to resolve editor file, so Cannot open " + fileName);
			return;
		}
		File file = new File(editorFileOrNull.getParentFile(), fileName);
		openFileWithEclipseDefault(file);

	}

	protected void openFileWithEclipseDefault(File file) {
		IWorkbenchPage activePage = getActivePage();
		if (!file.exists()) {
			boolean fileCreated = requestCreateOfMissingFile(file);
			if (!fileCreated) {
				/* file creation not possible or not wanted */
				return;
			}
		}
		try {
			IFile iFile = EclipseResourceHelper.DEFAULT.toIFile(file);
			/*
			 * after creating the file, refresh project to show the newly
			 * created file in the current workspace
			 */
			iFile.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			IDE.openEditor(activePage, iFile, true);
			return;
		} catch (PartInitException e) {
			AsciiDoctorEditorUtil.logError("Not able to open include", e);
		} catch (CoreException e) {
			AsciiDoctorEditorUtil.logError("CoreException", e);
		}
	}

	private boolean requestCreateOfMissingFile(File file) {
		String message = String.format("Cannot open\n%s\nbecause it does not exist!\n\nWould you like to create the file?", file.getAbsolutePath());
		boolean userWantsToCreateFile = MessageDialog.openQuestion(getActiveWorkbenchShell(), "Not able to load",
				message);

		if (!userWantsToCreateFile) {
			return false;
		}
		return createMissingFile(file);
	}

	/**
	 * Create a missing file
	 * @param file
	 * @return <code>false</code> when file not accessible after this method call. <code>true</code> when file is no longer missing after calling this method. If the file is created meanwhile the method will return <code>true</code> also... 
	 */
	private boolean createMissingFile(File file) {
		try {
			if (file.exists()){
				return true;
			}
			File parentFile = file.getParentFile();
			if (! parentFile.exists()){
				if (!parentFile.mkdirs()){
					throw new IOException("Unable to create parent folder:"+parentFile.getAbsolutePath());
				}
			}
			if (file.createNewFile()){
				return true;
			}				
			MessageDialog.openInformation(getActiveWorkbenchShell(), "File already exists", "The file already exists");
			return true;
		} catch (IOException e) {
			AsciiDoctorEditorUtil.logError("There was an Error while creating the file", e);

			String message = String.format("An Error occured while creating the file %s", file.getAbsolutePath());
			ErrorDialog.openError(getActiveWorkbenchShell(), "Unable to create file", null,
					new Status(Status.ERROR, AsciiDoctorEditorActivator.PLUGIN_ID, message, e));
		}
		return false;
	}

	public void resetCache() {
		asciidoctorWrapper.resetCaches();
	}

	public void setTOCShown(boolean shown) {
		if (shown == asciidoctorWrapper.isTocVisible()) {
			return;
		}
		asciidoctorWrapper.setTocVisible(shown);
		/*
		 * TOC building does always lead to a long time running task, at least
		 * inside preview - so we show the initializing info with progressbar
		 */
		showRebuildingInPreviewAndTriggerFullHTMLRebuildAsJob(true);
	}

	public boolean isTOCShown() {
		return asciidoctorWrapper.isTocVisible();
	}

	public void setPreviewVisible(boolean visible) {
		this.previewVisible = visible;
		browserAccess.setEnabled(previewVisible);
		sashForm.layout(); // after this the browser will be hidden/shown ...
							// otherwise we got an empty space appearing
		ensureInternalBrowserShowsURL(null);
	}

	public boolean isPreviewVisible() {
		return previewVisible;
	}

	public void navgigateToTopOfView() {
		browserAccess.navgigateToTopOfView();
	}

	public void openImage(String fileName) {
		if (fileName == null) {
			return;
		}
		String imagespath = asciidoctorWrapper.getContext().getImageProvider().getCachedSourceImagesPath();
		File file = new File(imagespath, fileName);
		openFileWithEclipseDefault(file);
	}

	public void openDiagram(String fileName) {
		if (fileName == null) {
			return;
		}
		File diagramRootDirectory = asciidoctorWrapper.getContext().getDiagramProvider().getDiagramRootDirectory();
		if (diagramRootDirectory == null) {
			return;
		}
		if (!diagramRootDirectory.exists()) {
			return;
		}
		File file = new File(diagramRootDirectory, fileName);
		openFileWithEclipseDefault(file);
	}

}
