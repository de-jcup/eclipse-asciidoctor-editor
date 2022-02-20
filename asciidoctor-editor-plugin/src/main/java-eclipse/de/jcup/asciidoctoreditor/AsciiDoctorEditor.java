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

import static de.jcup.asciidoctoreditor.util.EclipseUtil.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Semaphore;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension2;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
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
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import de.jcup.asciidoctoreditor.asciidoc.AsciiDoctorWrapper;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDoctorWrapperRegistry;
import de.jcup.asciidoctoreditor.asciidoc.InstalledAsciidoctorException;
import de.jcup.asciidoctoreditor.asciidoc.WrapperConvertData;
import de.jcup.asciidoctoreditor.diagram.plantuml.AsciiDoctorPlantUMLSourceViewerConfiguration;
import de.jcup.asciidoctoreditor.document.AsciiDoctorFileDocumentProvider;
import de.jcup.asciidoctoreditor.document.AsciiDoctorTextFileDocumentProvider;
import de.jcup.asciidoctoreditor.hyperlink.AsciiDoctorEditorLinkSupport;
import de.jcup.asciidoctoreditor.outline.AsciiDoctorEditorTreeContentProvider;
import de.jcup.asciidoctoreditor.outline.Item;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.preview.AsciiDoctorEditorBuildSupport;
import de.jcup.asciidoctoreditor.preview.BrowserAccess;
import de.jcup.asciidoctoreditor.preview.BrowserAccess.BrowserContentInitializer;
import de.jcup.asciidoctoreditor.preview.BuildAsciiDocMode;
import de.jcup.asciidoctoreditor.preview.EnsureFileRunnable;
import de.jcup.asciidoctoreditor.preview.ScrollSynchronizer;
import de.jcup.asciidoctoreditor.preview.WaitForGeneratedFileAndShowInsideExternalPreviewPreviewRunner;
import de.jcup.asciidoctoreditor.preview.WaitForGeneratedFileAndShowInsideIternalPreviewRunner;
import de.jcup.asciidoctoreditor.provider.AsciiDoctorProviderContext;
import de.jcup.asciidoctoreditor.script.AsciiDoctorHeadline;
import de.jcup.asciidoctoreditor.script.AsciiDoctorInlineAnchor;
import de.jcup.asciidoctoreditor.script.AsciiDoctorMarker;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModel;
import de.jcup.asciidoctoreditor.script.AsciidoctorTextSelectable;
import de.jcup.asciidoctoreditor.toolbar.AddErrorDebugAction;
import de.jcup.asciidoctoreditor.toolbar.AddLineBreakAction;
import de.jcup.asciidoctoreditor.toolbar.BoldFormatAction;
import de.jcup.asciidoctoreditor.toolbar.ClearProjectCacheAsciiDocViewAction;
import de.jcup.asciidoctoreditor.toolbar.CreatePDFAction;
import de.jcup.asciidoctoreditor.toolbar.InsertAdmonitionAction;
import de.jcup.asciidoctoreditor.toolbar.InsertSectionTitleAction;
import de.jcup.asciidoctoreditor.toolbar.ItalicFormatAction;
import de.jcup.asciidoctoreditor.toolbar.JumpToTopOfAsciiDocViewAction;
import de.jcup.asciidoctoreditor.toolbar.MonospacedFormatAction;
import de.jcup.asciidoctoreditor.toolbar.NewCodeBlockInsertAction;
import de.jcup.asciidoctoreditor.toolbar.NewLinkInsertAction;
import de.jcup.asciidoctoreditor.toolbar.NewTableInsertAction;
import de.jcup.asciidoctoreditor.toolbar.RebuildAsciiDocViewAction;
import de.jcup.asciidoctoreditor.toolbar.ShowPreviewHorizontalInsideEditorAction;
import de.jcup.asciidoctoreditor.toolbar.ShowPreviewInExternalBrowserAction;
import de.jcup.asciidoctoreditor.toolbar.ShowPreviewVerticalInsideEditorAction;
import de.jcup.asciidoctoreditor.toolbar.ToggleTOCAction;
import de.jcup.asciidoctoreditor.ui.ColorManager;
import de.jcup.asciidoctoreditor.ui.StatusMessageSupport;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;
import de.jcup.eclipse.commons.ui.ColorUtil;

@AdaptedFromEGradle
public class AsciiDoctorEditor extends TextEditor implements StatusMessageSupport, IResourceChangeListener {

    /** The EDITOR_ID of this editor as defined in plugin.xml */
    public static final String EDITOR_ID = "asciidoctoreditor.editors.AsciiDoctorEditor";

    /** The COMMAND_ID of the editor context menu */
    public static final String EDITOR_CONTEXT_MENU_ID = EDITOR_ID + ".context";

    /** The COMMAND_ID of the editor ruler context menu */
    public static final String EDITOR_RULER_CONTEXT_MENU_ID = EDITOR_CONTEXT_MENU_ID + ".ruler";

    private static final int INITIAL_LAYOUT_ORIENTATION = SWT.HORIZONTAL;

    protected CoolBarManager coolBarManager;

    private BrowserAccess browserAccess;
    private ContentTransformer contentTransformer;
    private Semaphore outputBuildSemaphore = new Semaphore(1);
    ScrollSynchronizer synchronizer;
    File temporaryExternalPreviewFile;
    private File temporaryInternalPreviewFile;

    private long editorId;
    private long fallBackEditorId;
    private String bgColor;
    private BoldFormatAction boldFormatAction;
    private AddLineBreakAction addLineBreakAction;
    private AsciiDoctorEditorBuildSupport buildSupport;
    private AsciiDoctorEditorCommentSupport commentSupport;
    private File editorFile;
    private String fgColor;
    private boolean internalPreview;
    private ItalicFormatAction italicFormatAction;
    private int lastCaretPosition;
    private AsciiDoctorEditorLinkSupport linkSupport;
    private MonospacedFormatAction monoSpacedFormatAction;
    private AsciidoctorEditorOutlineSupport outlineSupport;
    private IProject project;
    private SashForm sashForm;
    private Composite topComposite;
    protected RebuildAsciiDocViewAction rebuildAction;

    private ClearProjectCacheAsciiDocViewAction clearProjectAction;

    private static final AsciiDoctorTextFileDocumentProvider ASCIIDOC_SHARED_TEXTFILE_DOCUMENT_PROVIDER = new AsciiDoctorTextFileDocumentProvider();
    private static final AsciiDoctorFileDocumentProvider ASCIIDOC__SHARED_FILE_DOCUMENT_PROVIDER = new AsciiDoctorFileDocumentProvider();

    public long getEditorId() {
        return editorId;
    }

    public ContentTransformer getContentTransformer() {
        return contentTransformer;
    }

    public Semaphore getOutputBuildSemaphore() {
        return outputBuildSemaphore;
    }

    public File getTemporaryInternalPreviewFile() {
        return temporaryInternalPreviewFile;
    }

    public EditorType getType() {
        return EditorType.ASCIIDOC;
    }

    public AsciiDoctorEditor() {
        outlineSupport = new AsciidoctorEditorOutlineSupport(this);
        buildSupport = new AsciiDoctorEditorBuildSupport(this);
        linkSupport = new AsciiDoctorEditorLinkSupport(this);
        commentSupport = new AsciiDoctorEditorCommentSupport(this);

        fallBackEditorId = System.nanoTime(); // nano time just as fallback - we use hashcode of path normally
        editorId = fallBackEditorId;

        setSourceViewerConfiguration(createSourceViewerConfig());

        contentTransformer = createCustomContentTransformer();
        if (contentTransformer == null) {
            contentTransformer = NotChangingContentTransformer.INSTANCE;
        }
        this.synchronizer = new ScrollSynchronizer(this);
    }

    public BrowserAccess getBrowserAccess() {
        return browserAccess;
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
         * register as resource change listener to provide marker change listening
         */
        ResourcesPlugin.getWorkspace().addResourceChangeListener(this);

        setTitleImageInitial();

    }

    @Override
    public void dispose() {
        super.dispose();
        if (browserAccess != null) {
            browserAccess.dispose();
        }

        getWrapper().dispose();

        ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    }

    public AsciiDoctorHeadline findAsciiDoctorHeadlineByName(String headlineName) {
        if (headlineName == null) {
            return null;
        }
        AsciiDoctorScriptModel model = outlineSupport.buildModelWithoutValidation();
        Collection<AsciiDoctorHeadline> headlines = model.getHeadlines();
        for (AsciiDoctorHeadline headline : headlines) {
            if (headlineName.equals(headline.getName())) {
                return headline;
            }
        }
        return null;
    }

    public AsciidoctorTextSelectable findAsciiDoctorPositionByElementId(String elementId) {
        if (elementId == null) {
            return null;
        }
        AsciiDoctorScriptModel model = outlineSupport.buildModelWithoutValidation();
        Collection<AsciiDoctorHeadline> headlines = model.getHeadlines();
        for (AsciiDoctorHeadline headline : headlines) {
            if (elementId.equals(headline.getCalculatedId())) {
                return headline;
            }
        }
        Collection<AsciiDoctorInlineAnchor> anchors = model.getInlineAnchors();
        for (AsciiDoctorInlineAnchor anchor : anchors) {
            if (elementId.equals(anchor.getId())) {
                return anchor;
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
            return (T) getOutlineSupport().getOutlinePage();
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
            return (T) outlineSupport.getOutlinePage().getContentProvider();
        }
        return super.getAdapter(adapter);
    }

    public String getBackGroundColorAsWeb() {
        ensureColorsFetched();
        return bgColor;
    }

    public AsciiDoctorEditorCommentSupport getCommentSupport() {
        return commentSupport;
    }

    public IDocument getDocument() {
        IDocumentProvider documentProvider = getDocumentProvider();
        if (documentProvider == null) {
            return null;
        }
        return documentProvider.getDocument(getEditorInput());
    }

    public String getForeGroundColorAsWeb() {
        ensureColorsFetched();
        return fgColor;
    }

    public Item getItemAt(int offset) {
        AsciiDoctorEditorTreeContentProvider contentProvider = getOutlineSupport().getOutlinePage().getContentProvider();
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

    public AsciiDoctorEditorLinkSupport getLinkSupport() {
        return linkSupport;
    }

    public AsciidoctorEditorOutlineSupport getOutlineSupport() {
        return outlineSupport;
    }

    public AsciiDoctorEditorPreferences getPreferences() {
        return AsciiDoctorEditorPreferences.getInstance();
    }

    /**
     * 
     * @return file or <code>null</code>
     */
    public File getTemporaryExternalPreviewFile() {
        return temporaryExternalPreviewFile;
    }

    public AsciiDoctorWrapper getWrapper() {
        return AsciiDoctorWrapperRegistry.INSTANCE.getWrapper(getProject());
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

    public boolean isInternalPreview() {
        return internalPreview;
    }

    public boolean isTOCShown() {
        return getWrapper().isTocVisible();
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

    public void makeSelectedTextBold() {
        boldFormatAction.run();
    }

    public void addLineBreak() {
        addLineBreakAction.run();
    }

    public void makeSelectedTextItalic() {
        italicFormatAction.run();
    }

    public void makeSelectedTextMonoSpaced() {
        monoSpacedFormatAction.run();
    }

    public void navgigateToTopOfView() {
        browserAccess.navgigateToTopOfView();
    }

    public void openDiagram(String fileName) {
        if (fileName == null) {
            return;
        }
        File diagramRootDirectory = getWrapper().getContext().getDiagramProvider().getDiagramRootDirectory();
        if (diagramRootDirectory == null) {
            return;
        }
        if (!diagramRootDirectory.exists()) {
            return;
        }
        File file = new File(diagramRootDirectory, fileName);
        openFileWithEclipseDefault(file);
    }

    /**
     * @return diagram path as string, or <code>null</code>
     */
    public String getDiagramPathOrNull() {
        AsciiDoctorProviderContext context = getWrapper().getContext();
        File editorFile = getEditorFileOrNull();
        if (editorFile == null) {
            return null;
        }
        context.setAsciidocFile(editorFile);
        File rootDir = context.getDiagramProvider().getDiagramRootDirectory();
        if (rootDir == null) {
            return null;
        }
        return rootDir.getAbsolutePath();
    }

    /**
     * @return images path as string, or <code>null</code>
     */
    public String getImagesPathOrNull() {
        AsciiDoctorProviderContext context = getWrapper().getContext();
        File editorFile = getEditorFileOrNull();
        if (editorFile == null) {
            return null;
        }
        context.setAsciidocFile(editorFile);
        return context.getImageProvider().getCachedSourceImagesPath();
    }

    public void openImage(String fileName) {
        if (fileName == null) {
            return;
        }
        String imagespath = getImagesPathOrNull();
        File file = new File(imagespath, fileName);
        openFileWithEclipseDefault(file);
    }

    public void openInclude(String fileName) {

        File editorFileOrNull = getEditorFileOrNull();
        if (editorFileOrNull == null) {
            MessageDialog.openWarning(getActiveWorkbenchShell(), "Not able to resolve editor file", "Not able to resolve editor file, so Cannot open " + fileName);
            return;
        }
        File file = new File(editorFileOrNull.getParentFile(), fileName);
        openFileWithEclipseDefault(file);

    }

    public void openInExternalBrowser() {
        /*
         * remove old existing file to show browser only when ready... otherwise old
         * files are shown while not rendered complete!
         */
        if (temporaryExternalPreviewFile != null && temporaryExternalPreviewFile.exists()) {
            temporaryExternalPreviewFile.delete();
        }
        /* always build now again the file */
        buildSupport.build(BuildAsciiDocMode.ALWAYS, false);

        startEnsureFileThread(temporaryExternalPreviewFile, new WaitForGeneratedFileAndShowInsideExternalPreviewPreviewRunner(this, null));
    }

    public void createAndShowPDF() {
        AsciiDoctorEditorPDFLauncher.INSTANCE.createAndShowPDF(this);
    }

    public void rebuild() {
        this.rebuildAction.run();
    }

    public void refreshAsciiDocView() {
        buildSupport.build(BuildAsciiDocMode.ALWAYS, internalPreview);
    }

    public void resetCache() {
        getWrapper().resetCaches();
    }

    public void resourceChanged(IResourceChangeEvent event) {
        if (isMarkerChangeForThisEditor(event)) {
            int severity = getSeverity();

            setTitleImageDependingOnSeverity(severity);
        }
    }

    public void setErrorMessage(String message) {
        super.setStatusLineErrorMessage(message);
    }

    public void setInternalPreview(boolean internalPreview) {
        boolean wasExternalBefore = !this.internalPreview && internalPreview;
        this.internalPreview = internalPreview;
        browserAccess.setEnabled(internalPreview);
        sashForm.layout(); // after this the browser will be hidden/shown ...
                           // otherwise we got an empty space appearing
        if (wasExternalBefore) {
            refreshAsciiDocView();
        } else {
            ensureInternalBrowserShowsURL(null);
        }
    }

    public void setTOCShown(boolean shown) {
        if (shown == getWrapper().isTocVisible()) {
            return;
        }
        getWrapper().setTocVisible(shown);
        /*
         * TOC building does always lead to a long time running task, at least inside
         * preview - so we show the initializing info with progressbar
         */
        buildSupport.build(BuildAsciiDocMode.NOT_WHEN_EXTERNAL_PREVIEW_DISABLED, internalPreview);
    }

    public void setVerticalSplit(boolean verticalSplit) {
        /*
         * don't be confused: SWT.HOROIZONTAL will setup editor on top and view on
         * bottom - so its vertical...
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

    /**
     * Cleans validation errors and does rebuild outline and validate again
     */
    public void validate() {
        removeValidationErrors();
        rebuildOutlineAndValidate();
    }

    protected ContentTransformer createCustomContentTransformer() {
        return null;
    }

    protected IDocumentProvider resolveDocumentProvider(IEditorInput input) {
        if (input instanceof FileStoreEditorInput) {
            return ASCIIDOC_SHARED_TEXTFILE_DOCUMENT_PROVIDER;
        } else {
            return ASCIIDOC__SHARED_FILE_DOCUMENT_PROVIDER;
        }
    }

    @Override
    protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
        return super.createSourceViewer(parent, ruler, styles);
    }

    protected SourceViewerConfiguration createSourceViewerConfig() {
        return new AsciiDoctorSourceViewerConfiguration(this);
    }

    protected void createToolbar() {
        coolBarManager = new CoolBarManager(SWT.FLAT | SWT.HORIZONTAL);
        CoolBar coolbar = coolBarManager.createControl(topComposite);
        GridData toolbarGD = new GridData(GridData.FILL_HORIZONTAL);

        coolbar.setLayoutData(toolbarGD);
    }

    @Override
    protected void doSetInput(IEditorInput input) throws CoreException {
        setDocumentProvider(resolveDocumentProvider(input));
        super.doSetInput(input);
        IFile file = resolveFileOrNull();
        if (file == null) {
            editorId = fallBackEditorId;
        } else {
            editorId = file.getFullPath().toFile().hashCode();
        }
        File configRoot = null;
        try {
            IProject project = getProject();
            if (project != null) {
                IPath projectLocation = project.getLocation();
                configRoot = EclipseResourceHelper.DEFAULT.toFile(projectLocation);
            }
        } catch (Exception e) {
            AsciiDoctorEclipseLogAdapter.INSTANCE.logError("Was not able to determine config root, fallback to base dir", e);
        }
        getWrapper().getContext().setConfigRoot(configRoot);

    }

    @Override
    protected void editorSaved() {
        super.editorSaved();

        buildSupport.build(BuildAsciiDocMode.NOT_WHEN_EXTERNAL_PREVIEW_DISABLED, internalPreview);
    }

    public void removeValidationErrors() {
        AsciiDoctorEditorUtil.removeScriptErrors(this);

    }

    public void rebuildOutlineAndValidate() {
        outlineSupport.rebuildOutlineAndValidate();
    }

    public void ensureInternalBrowserShowsURL(IProgressMonitor monitor) {
        if (!isInternalPreview()) {
            return;
        }
        if (isCanceled(monitor)) {
            return;
        }
        startEnsureFileThread(temporaryInternalPreviewFile, new WaitForGeneratedFileAndShowInsideIternalPreviewRunner(this, monitor));
    }

    public String fetchAsciidoctorErrorMessage(Throwable e) {
        if (e == null) {
            return null;
        }
        if (e instanceof InstalledAsciidoctorException) {
            return e.getMessage();
        }
        return e.getClass().getSimpleName() + ": " + SimpleExceptionUtils.getRootMessage(e);
    }

    public File getEditorFileOrNull() {
        /* !editorFileExists == true can happen when we got a rename of the file */
        if (editorFile == null || !editorFile.exists()) {
            editorFile = resolveEditorFileOrNull();
        }
        return editorFile;
    }

    protected PreviewLayout getInitialLayoutMode() {
        return getPreferences().getInitialLayoutModeForNewEditors();
    }

    protected IProject getProject() {
        if (project != null) {
            return project;
        }
        IFile f = resolveFileOrNull();
        if (f != null) {
            project = f.getProject();
        }
        return project;
    }

    public String getProjectName() {
        IProject p = getProject();
        if (p == null) {
            return null;
        }
        return p.getName();
    }

    private IFile resolveFileOrNull() {
        IEditorInput input = getEditorInput();

        IPath location = null;

        IFile iFile = null;
        if (input instanceof FileEditorInput) {
            /* standard opening with eclipse IDE inside */
            FileEditorInput finput = (FileEditorInput) input;
            iFile = finput.getFile();
        } else if (input instanceof FileStoreEditorInput) {
            /*
             * command line : eclipse xyz.adoc does use file store editor input ....
             */
            FileStoreEditorInput fsInput = (FileStoreEditorInput) input;
            iFile = fsInput.getAdapter(IFile.class);
        }
        if (iFile == null) {
            return null;
        }
        location = iFile.getFullPath();
        if (location == null) {
            return null;
        }
        IWorkspaceRoot root = getWorkspace().getRoot();
        IFile f = root.getFile(location);
        return f;
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

    protected String getToggleCommentCodePart() {
        return "//";
    }

    @Override
    protected void initializeEditor() {
        super.initializeEditor();
        setEditorContextMenuId(EDITOR_CONTEXT_MENU_ID);
        setRulerContextMenuId(EDITOR_RULER_CONTEXT_MENU_ID);
    }

    protected void initPreview(SashForm sashForm) {
        File editorFileOrNull = getEditorFileOrNull();
        if (editorFileOrNull == null) {
            setErrorMessage("Asciidoctor Editor: preview not available because no editor file found");
            return;
        }
        AsciiDoctorWrapper wrapper = getWrapper();
        temporaryInternalPreviewFile = wrapper.getTempFileFor(editorFileOrNull, editorId, TemporaryFileType.INTERNAL_PREVIEW);
        temporaryExternalPreviewFile = wrapper.getTempFileFor(editorFileOrNull, editorId, TemporaryFileType.EXTERNAL_PREVIEW);

        browserAccess.ensureBrowser(new BrowserContentInitializer() {

            @Override
            public void initialize(Browser browser) {
                UIJob job = new UIJob("Initialize Browser") {

                    @Override
                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        AsciiDoctorEditorBuildSupport.showInitializingInfo(AsciiDoctorEditor.this);
                        return Status.OK_STATUS;
                    }
                };
                job.schedule();
            }
        });
        PreviewLayout initialLayout = getInitialLayoutMode();
        boolean internal = !initialLayout.isExternal();

        synchronizer.installInBrowser();

        setInternalPreview(internal);
        if (internal) {
            setVerticalSplit(initialLayout.isVertical());
        }
    }

    protected void initToolbar() {
        rebuildAction = new RebuildAsciiDocViewAction(this);
        clearProjectAction = new ClearProjectCacheAsciiDocViewAction(this);

        italicFormatAction = new ItalicFormatAction(this);
        boldFormatAction = new BoldFormatAction(this);
        monoSpacedFormatAction = new MonospacedFormatAction(this);

        addLineBreakAction = new AddLineBreakAction(this);

        IToolBarManager stylingToolBarManager = new ToolBarManager(coolBarManager.getStyle());
        stylingToolBarManager.add(new InsertSectionTitleAction(this));

        stylingToolBarManager.add(italicFormatAction);
        stylingToolBarManager.add(boldFormatAction);
        stylingToolBarManager.add(monoSpacedFormatAction);
        stylingToolBarManager.add(new InsertAdmonitionAction(this));

        IToolBarManager insertToolberManager = new ToolBarManager(coolBarManager.getStyle());

        insertToolberManager.add(new NewLinkInsertAction(this));
        insertToolberManager.add(new NewTableInsertAction(this));
        insertToolberManager.add(new NewCodeBlockInsertAction(this));
        insertToolberManager.add(addLineBreakAction);

        IToolBarManager viewToolBarManager = new ToolBarManager(coolBarManager.getStyle());
        viewToolBarManager.add(new ToggleTOCAction(this));
        viewToolBarManager.add(new JumpToTopOfAsciiDocViewAction(this));

        IToolBarManager previewToolBarManager = new ToolBarManager(coolBarManager.getStyle());
        previewToolBarManager.add(new ShowPreviewVerticalInsideEditorAction(this));
        previewToolBarManager.add(new ShowPreviewHorizontalInsideEditorAction(this));
        previewToolBarManager.add(new ShowPreviewInExternalBrowserAction(this));

        IToolBarManager buildToolBarManager = new ToolBarManager(coolBarManager.getStyle());
        buildToolBarManager.add(rebuildAction);
        buildToolBarManager.add(clearProjectAction);

        IToolBarManager outputToolBarManager = new ToolBarManager(coolBarManager.getStyle());
        outputToolBarManager.add(new CreatePDFAction(this));

        // Add to the cool bar manager
        coolBarManager.add(new ToolBarContributionItem(previewToolBarManager, "asciiDocEditor.toolbar.preview"));
        coolBarManager.add(new ToolBarContributionItem(stylingToolBarManager, "asciiDocEditor.toolbar.style"));
        coolBarManager.add(new ToolBarContributionItem(insertToolberManager, "asciiDocEditor.toolbar.insert"));
        coolBarManager.add(new ToolBarContributionItem(viewToolBarManager, "asciiDocEditor.toolbar.view"));
        coolBarManager.add(new ToolBarContributionItem(outputToolBarManager, "asciiDocEditor.toolbar.output"));
        coolBarManager.add(new ToolBarContributionItem(buildToolBarManager, "asciiDocEditor.toolbar.build"));

        if (EclipseDevelopmentSettings.DEBUG_TOOLBAR_ENABLED) {
            IToolBarManager debugToolBar = new ToolBarManager(coolBarManager.getStyle());
            debugToolBar.add(new AddErrorDebugAction(this));
            coolBarManager.add(new ToolBarContributionItem(debugToolBar, "asciiDocEditor.toolbar.debug"));
        }

        coolBarManager.update(true);

    }

    public boolean isAsciiDoctorError(Throwable e) {
        if (e == null) {
            return false;
        }
        boolean error = e instanceof InstalledAsciidoctorException || e.getClass().getName().startsWith("org.asciidoctor");

        return error;
    }

    protected boolean isNoPreviewFileGenerated() {
        return temporaryInternalPreviewFile == null || !temporaryInternalPreviewFile.exists();
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
             * after creating the file, refresh project to show the newly created file in
             * the current workspace
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
             * command line : eclipse xyz.adoc does use file store editor input ....
             */
            FileStoreEditorInput fsInput = (FileStoreEditorInput) input;
            editorFile = fsInput.getAdapter(File.class);
        }
        return editorFile;
    }

    protected void startEnsureFileThread(File file, EnsureFileRunnable runnable) {
        Thread t = new Thread(runnable);
        String name = "";
        if (file != null) {
            name = file.getName();
        } else {
            name = "undefined";
        }
        t.setName("asciidoctor-editor-ensure:" + name);
        t.start();
    }

    void addErrorMarkers(AsciiDoctorScriptModel model, int severity) {
        if (model == null) {
            return;
        }
        IDocument document = getDocument();
        if (document == null) {
            return;
        }
        Collection<AsciiDoctorMarker> errors = model.getErrors();
        for (AsciiDoctorMarker error : errors) {
            int startPos = error.getStart();
            int line;
            try {
                line = document.getLineOfOffset(startPos);
            } catch (BadLocationException e) {
                logError("Cannot get line offset for " + startPos, e);
                line = 0;
            }
            AsciiDoctorEditorUtil.addAsciiDoctorMarker(this, line, error, severity);
        }

    }

    public ISourceViewer getAsciiDoctorSourceViewer() {
        return super.getSourceViewer();
    }

    public SourceViewerConfiguration getAsciiDoctorSourceViewerConfiguration() {
        return getSourceViewerConfiguration();
    }

    /**
     * Get document text - safe way.
     * 
     * @return string, never <code>null</code>
     */
    public String getDocumentText() {
        IDocument doc = getDocument();
        if (doc == null) {
            return "";
        }
        return doc.get();
    }

    public boolean isCanceled(IProgressMonitor monitor) {
        if (monitor == null) {
            return false; // no chance to cancel...
        }
        return monitor.isCanceled();
    }

    public boolean isNotCanceled(IProgressMonitor monitor) {
        return !isCanceled(monitor);
    }

    public void refocus() {
        if (!isInternalPreview()) {
            /* problem exists only at internal preview */
            return;
        }
        ISourceViewer sourceviewer = getSourceViewer();
        if (sourceviewer == null) {
            return;
        }
        StyledText textWidget = sourceviewer.getTextWidget();
        if (textWidget == null || textWidget.isDisposed()) {
            return;
        }
        if (!textWidget.isFocusControl()) {
            if (EclipseDevelopmentSettings.DEBUG_LOGGING_ENABLED) {
                AsciiDoctorEclipseLogAdapter.INSTANCE.logInfo("not focus controlled! try to get it");
            }
            if (!textWidget.setFocus()) {
                AsciiDoctorEclipseLogAdapter.INSTANCE.logInfo("cannot get focus?!?!?");
            }
        }
    }

    /* if necessary do some preparations before calling asciidoctor... */
    public void beforeAsciidocConvert(WrapperConvertData data) {
        /* per default nothing */
    }

    void setTitleImageDependingOnSeverity(int severity) {
        safeAsyncExec(() -> setTitleImage(getImage("icons/" + getTitleImageName(severity), AsciiDoctorEditorActivator.PLUGIN_ID)));
    }

    private void activateAsciiDoctorEditorContext() {
        IContextService contextService = getSite().getService(IContextService.class);
        if (contextService != null) {
            contextService.activateContext(EDITOR_CONTEXT_MENU_ID);
        }
    }

    /**
     * Create a missing file
     * 
     * @param file
     * @return <code>false</code> when file not accessible after this method call.
     *         <code>true</code> when file is no longer missing after calling this
     *         method. If the file is created meanwhile the method will return
     *         <code>true</code> also...
     */
    private boolean createMissingFile(File file) {
        try {
            if (file.exists()) {
                return true;
            }
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                if (!parentFile.mkdirs()) {
                    throw new IOException("Unable to create parent folder:" + parentFile.getAbsolutePath());
                }
            }
            if (file.createNewFile()) {
                return true;
            }
            MessageDialog.openInformation(getActiveWorkbenchShell(), "File already exists", "The file already exists");
            return true;
        } catch (IOException e) {
            AsciiDoctorEditorUtil.logError("There was an Error while creating the file", e);

            String message = String.format("An Error occured while creating the file %s", file.getAbsolutePath());
            ErrorDialog.openError(getActiveWorkbenchShell(), "Unable to create file", null, new Status(Status.ERROR, AsciiDoctorEditorActivator.PLUGIN_ID, message, e));
        }
        return false;
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

    private boolean requestCreateOfMissingFile(File file) {
        String message = String.format("Cannot open\n%s\nbecause it does not exist!\n\nWould you like to create the file?", file.getAbsolutePath());
        boolean userWantsToCreateFile = MessageDialog.openQuestion(getActiveWorkbenchShell(), "Not able to load", message);

        if (!userWantsToCreateFile) {
            return false;
        }
        return createMissingFile(file);
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
     * Set initial title image dependent on current marker severity. This will mark
     * error icon on startup time which is not handled by resource change handling,
     * because having no change...
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
            handleCaretMoves(event);
        }

        protected void handleCaretMoves(CaretEvent event) {
            lastCaretPosition = event.caretOffset;
            if (outlineSupport.ignoreNextCaretMove) {
                outlineSupport.ignoreNextCaretMove = false;
                return;
            }
            synchronizer.onEditorCaretMoved(event.caretOffset);

            getOutlineSupport().getOutlinePage().onEditorCaretMoved(event.caretOffset);
        }

    }

}
