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
import static org.asciidoctor.Asciidoctor.Factory.*;

import java.util.Collection;
import java.util.HashMap;

import org.asciidoctor.Asciidoctor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension2;
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
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
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

@AdaptedFromEGradle
public class AsciiDoctorEditor extends TextEditor implements StatusMessageSupport, IResourceChangeListener {

	/** The COMMAND_ID of this editor as defined in plugin.xml */
	public static final String EDITOR_ID = "asciidoctoreditor.editors.AsciiDoctorEditor";
	/** The COMMAND_ID of the editor context menu */
	public static final String EDITOR_CONTEXT_MENU_ID = EDITOR_ID + ".context";
	/** The COMMAND_ID of the editor ruler context menu */
	public static final String EDITOR_RULER_CONTEXT_MENU_ID = EDITOR_CONTEXT_MENU_ID + ".ruler";

	private SourceViewerDecorationSupport additionalSourceViewerSupport;
	private AsciiDoctorContentOutlinePage outlinePage;
	private AsciiDoctorScriptModelBuilder modelBuilder;
	private Object monitor = new Object();
	private boolean quickOutlineOpened;
	private int lastCaretPosition;
	private AsciiDoctorOSGIWrapper asciidoctor;
	private Browser browser;
	
	private static final AsciiDoctorScriptModel FALLBACK_MODEL = new AsciiDoctorScriptModel();

	public AsciiDoctorEditor() {
		setSourceViewerConfiguration(new AsciiDoctorSourceViewerConfiguration(this));
		this.modelBuilder = new AsciiDoctorScriptModelBuilder();
		asciidoctor = new AsciiDoctorOSGIWrapper();
	}

	public void resourceChanged(IResourceChangeEvent event) {
		if (isMarkerChangeForThisEditor(event)) {
			int severity = getSeverity();

			setTitleImageDependingOnSeverity(severity);
		}
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

	private AsciiDoctorScriptModel buildModelWithoutValidation() {
		String text = getDocumentText();

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

	void setTitleImageDependingOnSeverity(int severity) {
		setTitleImage(
				EclipseUtil.getImage("icons/" + getTitleImageName(severity), AsciiDoctorEditorActivator.PLUGIN_ID));
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

	public void setErrorMessage(String message) {
		super.setStatusLineErrorMessage(message);
	}

	@Override
	public void createPartControl(Composite c) {
		
		RGB green = new RGB(0, 255, 0);
		RGB red = new RGB(255, 0, 0);
		RGB blue = new RGB(0, 0, 255);
		ColorManager colorManager = AsciiDoctorEditorActivator.getDefault().getColorManager();

		SashForm sashForm = new SashForm(c, SWT.HORIZONTAL);
		c.setLayout(new FillLayout());
		
		Composite parentTextEditor = new Composite(sashForm, SWT.CENTER|SWT.SCROLL_PAGE);
		parentTextEditor.setLayout(new FillLayout());
		parentTextEditor.setBackground(colorManager.getColor(blue));
		
		try {
			browser = new Browser(sashForm, SWT.CENTER);
		} catch (SWTError e) {
			MessageBox messageBox = new MessageBox(EclipseUtil.getActiveWorkbenchShell(), SWT.ICON_ERROR | SWT.OK);
			messageBox.setMessage("Browser cannot be initialized.");
			messageBox.setText("Exit");
			messageBox.open();
			return;
		}
		browser.setText("<html><h1>Hello world</h1></html>");
		
		
//		Composite asciiDoctorOutputView = new Composite(sashForm, SWT.CENTER);
//		asciiDoctorOutputView.setBackground(colorManager.getColor(red));
//		Button button = new Button(asciiDoctorOutputView, SWT.PUSH);
//		asciiDoctorOutputView.setBackground(colorManager.getColor(green));
//		// register listener for the selection event
//		button.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				System.out.println("Called!");
//			}
//		});

		super.createPartControl(parentTextEditor);

		Control adapter = getAdapter(Control.class);
		if (adapter instanceof StyledText) {
			StyledText text = (StyledText) adapter;
			text.addCaretListener(new AsciiDoctorEditorCaretListener());
		}

		activateAsciiDoctorEditorContext();

		installAdditionalSourceViewerSupport();

		/*
		 * register as resource change listener to provide marker change
		 * listening
		 */
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);

		setTitleImageInitial();
		updateAsciiDocView();
	}

	public AsciiDoctorContentOutlinePage getOutlinePage() {
		if (outlinePage == null) {
			outlinePage = new AsciiDoctorContentOutlinePage(this);
		}
		return outlinePage;
	}

	/**
	 * Installs an additional source viewer support which uses editor
	 * preferences instead of standard text preferences. If standard source
	 * viewer support would be set with editor preferences all standard
	 * preferences would be lost or had to be reimplmented. To avoid this
	 * another source viewer support is installed...
	 */
	private void installAdditionalSourceViewerSupport() {

		// additionalSourceViewerSupport = new
		// SourceViewerDecorationSupport(getSourceViewer(), getOverviewRuler(),
		// getAnnotationAccess(), getSharedColors());
		// additionalSourceViewerSupport.setMatchingCharacterPainterPreferenceKeys(
		// P_EDITOR_MATCHING_BRACKETS_ENABLED.getId(),
		// P_EDITOR_MATCHING_BRACKETS_COLOR.getId(),
		// P_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION.getId(),
		// P_EDITOR_ENCLOSING_BRACKETS.getId());
		//
		// IPreferenceStore preferenceStoreForDecorationSupport =
		// AsciiDoctorEditorUtil.getPreferences().getPreferenceStore();
		// additionalSourceViewerSupport.install(preferenceStoreForDecorationSupport);
	}

	@Override
	public void dispose() {
		super.dispose();

		if (additionalSourceViewerSupport != null) {
			additionalSourceViewerSupport.dispose();
		}

		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	public String getBackGroundColorAsWeb() {
		ensureColorsFetched();
		return bgColor;
	}

	public String getForeGroundColorAsWeb() {
		ensureColorsFetched();
		return fgColor;
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

	private String bgColor;
	private String fgColor;
	private boolean ignoreNextCaretMove;

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

	private IDocumentProvider createDocumentProvider(IEditorInput input) {
		if (input instanceof FileStoreEditorInput) {
			return new AsciiDoctorTextFileDocumentProvider();
		} else {
			return new AsciiDoctorFileDocumentProvider();
		}
	}

	public IDocument getDocument() {
		return getDocumentProvider().getDocument(getEditorInput());
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

	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		setEditorContextMenuId(EDITOR_CONTEXT_MENU_ID);
		setRulerContextMenuId(EDITOR_RULER_CONTEXT_MENU_ID);
	}

	private void activateAsciiDoctorEditorContext() {
		IContextService contextService = getSite().getService(IContextService.class);
		if (contextService != null) {
			contextService.activateContext(EDITOR_CONTEXT_MENU_ID);
		}
	}

	private ColorManager getColorManager() {
		return AsciiDoctorEditorActivator.getDefault().getColorManager();
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
				if ("#".equals(foundCode.toString())) {
					/* comment before */
					doc.replace(offset + whitespaceOffsetAdd, 1, "");
				} else {
					/* not commented */
					doc.replace(offset, 0, "#");
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

	public Item getItemAtCarretPosition() {
		return getItemAt(lastCaretPosition);
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

	public void selectFunction(String text) {
		System.out.println("should select functin:" + text);

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

	public AsciiDoctorEditorPreferences getPreferences() {
		return AsciiDoctorEditorPreferences.getInstance();
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

	public void validate() {
		rebuildOutline();
//		http://www.baeldung.com/asciidoctor
//		http://www.baeldung.com/asciidoctor-book
//		https://github.com/asciidoctor/asciidoctorj#converting-documents
		
	}

	public void updateAsciiDocView() {
		if (browser==null){
			return;
		}
		if (browser.isDisposed()){
			return;
		}
		String html = asciidoctor.convertToHTML(getDocumentText());
		browser.setText(html);
	}
}
