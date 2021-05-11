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

import static de.jcup.asciidoctoreditor.document.AsciiDoctorDocumentIdentifiers.*;
import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorSyntaxColorPreferenceConstants.*;
import static de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.ColorDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.spelling.SpellingAnnotation;

import de.jcup.asciidoctoreditor.codeassist.AsciidocContentAssistProcessor;
import de.jcup.asciidoctoreditor.document.AsciiDoctorDocumentIdentifier;
import de.jcup.asciidoctoreditor.document.AsciiDoctorDocumentIdentifiers;
import de.jcup.asciidoctoreditor.hyperlink.AsciiDoctorEditorLinkTextHyperlinkDetector;
import de.jcup.asciidoctoreditor.hyperlink.AsciiDoctorURLHyperlinkDetector;
import de.jcup.asciidoctoreditor.presentation.AsciiDoctorDefaultTextScanner;
import de.jcup.asciidoctoreditor.presentation.PresentationSupport;
import de.jcup.asciidoctoreditor.ui.ColorManager;
import de.jcup.eclipse.commons.codeassist.MultipleContentAssistProcessor;
import de.jcup.eclipse.commons.templates.TemplateSupport;
import de.jcup.eclipse.commons.ui.CSSProvider;
import de.jcup.eclipse.commons.ui.ColorUtil;
import de.jcup.eclipse.commons.ui.EclipseUtil;
import de.jcup.eclipse.commons.ui.PlainTextToHTMLProvider;
import de.jcup.eclipse.commons.ui.ReducedBrowserInformationControlCreator;

/**
 * 
 * @author Albert Tregnaghi
 *
 */
public class AsciiDoctorSourceViewerConfiguration extends TextSourceViewerConfiguration {

    private AsciiDoctorDefaultTextScanner textScanner;
    private ColorManager colorManager;

    private TextAttribute defaultTextAttribute;
    private AsciiDoctorEditorAnnotationHoover annotationHoover;
    private IAdaptable adaptable;
    private ContentAssistant contentAssistant;
    private AsciidocContentAssistProcessor contentAssistProcessor;
    private ReducedBrowserInformationControlCreator creator;
    private String bgColor;
    private String fgColor;
    /**
     * Creates configuration by given adaptable
     * 
     * @param adaptable must provide {@link ColorManager} and {@link IFile}
     */
    public AsciiDoctorSourceViewerConfiguration(IAdaptable adaptable) {

        IPreferenceStore generalTextStore = EditorsUI.getPreferenceStore();
        this.fPreferenceStore = new ChainedPreferenceStore(new IPreferenceStore[] { getPreferences().getPreferenceStore(), generalTextStore });

        Assert.isNotNull(adaptable, "adaptable may not be null!");
        this.annotationHoover = new AsciiDoctorEditorAnnotationHoover();

        this.contentAssistant = new ContentAssistant();
        contentAssistProcessor = new AsciidocContentAssistProcessor();
        contentAssistant.enableColoredLabels(true);

        TemplateSupport support = AsciiDoctorEditorActivator.getDefault().getTemplateSupportProvider().getSupport();
        IContentAssistProcessor templateProcessor = support.getProcessor();

        /* first templates, then words etc. */
        MultipleContentAssistProcessor multiProcessor = new MultipleContentAssistProcessor(templateProcessor, contentAssistProcessor);

        contentAssistant.setContentAssistProcessor(multiProcessor, IDocument.DEFAULT_CONTENT_TYPE);
        for (AsciiDoctorDocumentIdentifier identifier : AsciiDoctorDocumentIdentifiers.values()) {
            contentAssistant.setContentAssistProcessor(contentAssistProcessor, identifier.getId());
        }

        contentAssistant.addCompletionListener(contentAssistProcessor.getCompletionListener());

        this.colorManager = adaptable.getAdapter(ColorManager.class);
        Assert.isNotNull(colorManager, " adaptable must support color manager");
        defaultTextAttribute = new TextAttribute(colorManager.getColor(getPreferences().getColor(COLOR_NORMAL_TEXT)));

        this.adaptable = adaptable;

    }

    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
        contentAssistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
        contentAssistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
        return contentAssistant;
    }

    @Override
    public IQuickAssistAssistant getQuickAssistAssistant(ISourceViewer sourceViewer) {
        return super.getQuickAssistAssistant(sourceViewer);
    }
    
    public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
        if (creator==null) {
            creator = new ReducedBrowserInformationControlCreator();
        }
        if (bgColor == null || fgColor == null) {

            StyledText textWidget = sourceViewer.getTextWidget();
            if (textWidget != null) {

                EclipseUtil.getSafeDisplay().syncExec(new Runnable() {

                    @Override
                    public void run() {
                        bgColor = ColorUtil.convertToHexColor(textWidget.getBackground());
                        fgColor = ColorUtil.convertToHexColor(textWidget.getForeground());
                    }
                });
            }

        }
        PlainTextToHTMLProvider htmlProvider = creator.getFallbackHtmlProvider();
        if (htmlProvider==null) {
            return creator;
        }
        CSSProvider cssProvider = htmlProvider.getCSSProvider();
        if (cssProvider!=null) {
            cssProvider.setForegroundColor(fgColor);
            cssProvider.setBackgroundColor(bgColor);
        }
        return creator;
    }

    public IReconciler getReconciler(ISourceViewer sourceViewer) {
        return super.getReconciler(sourceViewer);
    }

    @Override
    public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
        return annotationHoover;
    }

    private class AsciiDoctorEditorAnnotationHoover extends DefaultAnnotationHover {
        @Override
        protected boolean isIncluded(Annotation annotation) {
            if (annotation instanceof MarkerAnnotation) {
                return true;
            }
            if (annotation instanceof SpellingAnnotation) {
                return true;
            }
            /* we do not support other annotations */
            return false;
        }
    }

    @Override
    public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
        if (AsciiDoctorDocumentIdentifiers.isContaining(contentType)) {
            return new AsciiDoctorTextHover();
        } else {
            return super.getTextHover(sourceViewer, contentType);
        }
    }

    @Override
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        /* @formatter:off */
		return allIdsToStringArray( 
				IDocument.DEFAULT_CONTENT_TYPE);
		/* @formatter:on */
    }

    @Override
    public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
        if (sourceViewer == null) {
            return null;
        }
        return new IHyperlinkDetector[] { new AsciiDoctorURLHyperlinkDetector(), new AsciiDoctorEditorLinkTextHyperlinkDetector(adaptable) };
    }

    @Override
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        PresentationReconciler reconciler = new PresentationReconciler();
        RGB jfaceHyperlinkColor = fetchLinkColor();

        addDefaultPresentation(reconciler);
        addPresentation(reconciler, TEXT_BLOCK.getId(), getPreferences().getColor(COLOR_TEXT_BLOCKS), SWT.BOLD);
        addPresentation(reconciler, TEXT_MONOSPACED.getId(), getPreferences().getColor(COLOR_TEXT_BLOCKS), SWT.BOLD);
        if (jfaceHyperlinkColor != null) {
            addPresentation(reconciler, HYPERLINK.getId(), jfaceHyperlinkColor, SWT.NONE);
        }
        addPresentation(reconciler, TEXT_BOLD.getId(), getPreferences().getColor(COLOR_TEXT_BOLD), SWT.BOLD);
        addPresentation(reconciler, TEXT_ITALIC.getId(), getPreferences().getColor(COLOR_TEXT_ITALIC), SWT.ITALIC);
        addPresentation(reconciler, COMMENT.getId(), getPreferences().getColor(COLOR_COMMENT), SWT.NONE);
        addPresentation(reconciler, ASCIIDOCTOR_COMMAND.getId(), getPreferences().getColor(COLOR_ASCIIDOCTOR_COMMAND), SWT.NONE);
        addPresentation(reconciler, HEADLINE.getId(), getPreferences().getColor(COLOR_ASCIIDOCTOR_HEADLINES), SWT.BOLD);
        addPresentation(reconciler, DELIMITERS.getId(), getPreferences().getColor(COLOR_DELIMITERS), SWT.ITALIC);
        if (jfaceHyperlinkColor != null) {
            addPresentation(reconciler, INCLUDE_KEYWORD.getId(), jfaceHyperlinkColor, SWT.BOLD);
        }
        addPresentation(reconciler, KNOWN_VARIABLES.getId(), getPreferences().getColor(COLOR_KNOWN_VARIABLES), SWT.NONE);

        return reconciler;
    }

    protected RGB fetchLinkColor() {
        RGB managedLinkColor = null;
        ColorDescriptor linkColorDescriptor = JFaceResources.getColorRegistry().getColorDescriptor(JFacePreferences.HYPERLINK_COLOR);
        if (linkColorDescriptor != null) {
            Color color = linkColorDescriptor.createColor(EclipseUtil.getSafeDisplay());
            managedLinkColor = color.getRGB();
            linkColorDescriptor.destroyColor(color);
        }
        return managedLinkColor;
    }

    private void addDefaultPresentation(PresentationReconciler reconciler) {
        DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getDefaultTextScanner());
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
    }

    private IToken createColorToken(RGB rgb) {
        Token token = new Token(new TextAttribute(colorManager.getColor(rgb)));
        return token;
    }

    private void addPresentation(PresentationReconciler reconciler, String id, RGB rgb, int style) {
        addPresentation(reconciler, id, rgb, style, null, null);
    }

    private void addPresentation(PresentationReconciler reconciler, String id, RGB foreGround, int style, Font font, RGB backGround) {
        Color backGroundColor = (backGround == null ? defaultTextAttribute.getBackground() : colorManager.getColor(backGround));
        Color foreGroundColor = (foreGround == null ? defaultTextAttribute.getForeground() : colorManager.getColor(foreGround));

        TextAttribute textAttribute = new TextAttribute(foreGroundColor, backGroundColor, style, font);

        PresentationSupport presentation = new PresentationSupport(textAttribute);
        reconciler.setDamager(presentation, id);
        reconciler.setRepairer(presentation, id);
    }

    private AsciiDoctorDefaultTextScanner getDefaultTextScanner() {
        if (textScanner == null) {
            textScanner = new AsciiDoctorDefaultTextScanner(colorManager);
            updateTextScannerDefaultColorToken();
        }
        return textScanner;
    }

    public void updateTextScannerDefaultColorToken() {
        if (textScanner == null) {
            return;
        }
        RGB color = getPreferences().getColor(COLOR_NORMAL_TEXT);
        textScanner.setDefaultReturnToken(createColorToken(color));
    }

}