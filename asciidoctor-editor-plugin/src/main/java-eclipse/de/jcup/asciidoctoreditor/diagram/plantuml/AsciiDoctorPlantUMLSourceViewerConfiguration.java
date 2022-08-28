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
package de.jcup.asciidoctoreditor.diagram.plantuml;

import static de.jcup.asciidoctoreditor.document.AsciiDoctorPlantUMLDocumentIdentifiers.*;
import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorPlantUMLEditorSyntaxColorPreferenceConstants.*;
import static de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import de.jcup.asciidoctoreditor.AsciiDoctorTextHover;
import de.jcup.asciidoctoreditor.codeassist.PlantumlContentAssistProcessor;
import de.jcup.asciidoctoreditor.document.AsciiDoctorDocumentIdentifier;
import de.jcup.asciidoctoreditor.document.AsciiDoctorPlantUMLDocumentIdentifiers;
import de.jcup.asciidoctoreditor.presentation.AsciiDoctorDefaultTextScanner;
import de.jcup.asciidoctoreditor.presentation.PresentationSupport;
import de.jcup.asciidoctoreditor.ui.ColorManager;

/**
 * 
 * @author Albert Tregnaghi
 *
 */
public class AsciiDoctorPlantUMLSourceViewerConfiguration extends TextSourceViewerConfiguration {

    private AsciiDoctorDefaultTextScanner scanner;
    private ColorManager colorManager;

    private TextAttribute defaultTextAttribute;
    private AsciiDoctorPlantUMLEditorAnnotationHoover annotationHoover;
    private ContentAssistant contentAssistant;
    private PlantumlContentAssistProcessor contentAssistProcessor;

    /**
     * Creates configuration by given adaptable
     * 
     * @param adaptable must provide {@link ColorManager} and {@link IFile}
     */
    public AsciiDoctorPlantUMLSourceViewerConfiguration(IAdaptable adaptable) {

        IPreferenceStore generalTextStore = EditorsUI.getPreferenceStore();
        this.fPreferenceStore = new ChainedPreferenceStore(new IPreferenceStore[] { getPreferences().getPreferenceStore(), generalTextStore });

        Assert.isNotNull(adaptable, "adaptable may not be null!");
        this.annotationHoover = new AsciiDoctorPlantUMLEditorAnnotationHoover();

        this.contentAssistant = new ContentAssistant();
        contentAssistProcessor = new PlantumlContentAssistProcessor();

        contentAssistant.enableColoredLabels(true);

        contentAssistant.setContentAssistProcessor(contentAssistProcessor, IDocument.DEFAULT_CONTENT_TYPE);
        for (AsciiDoctorDocumentIdentifier identifier : AsciiDoctorPlantUMLDocumentIdentifiers.values()) {
            contentAssistant.setContentAssistProcessor(contentAssistProcessor, identifier.getId());
        }

        contentAssistant.addCompletionListener(contentAssistProcessor.getCompletionListener());

        this.colorManager = adaptable.getAdapter(ColorManager.class);
        Assert.isNotNull(colorManager, " adaptable must support color manager");
        defaultTextAttribute = new TextAttribute(colorManager.getColor(getPreferences().getColor(COLOR_PLANTUML_NORMAL_TEXT)));

    }

    public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
        return contentAssistant;
    }

    @Override
    public IQuickAssistAssistant getQuickAssistAssistant(ISourceViewer sourceViewer) {
        /*
         * currently we avoid the default quick assistence parts (spell checking etc.)
         */
        return null;
    }

    public IReconciler getReconciler(ISourceViewer sourceViewer) {
        /*
         * currently we avoid the default reconciler mechanism parts (spell checking
         * etc.)
         */
        return null;
    }

    @Override
    public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
        return annotationHoover;
    }

    private class AsciiDoctorPlantUMLEditorAnnotationHoover extends DefaultAnnotationHover {
        @Override
        protected boolean isIncluded(Annotation annotation) {
            if (annotation instanceof MarkerAnnotation) {
                return true;
            }
            /* we do not support other annotations */
            return false;
        }
    }

    @Override
    public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
        return new AsciiDoctorTextHover();
    }

    @Override
    public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
        /* @formatter:off */
		return allIdsToStringArray( 
				IDocument.DEFAULT_CONTENT_TYPE);
		/* @formatter:on */
    }

    @Override
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        PresentationReconciler reconciler = new PresentationReconciler();

        addDefaultPresentation(reconciler);
        /* TODO Albert: think about using own color preferences here */
        addPresentation(reconciler, PLANTUML_PREPROCESSOR.getId(), getPreferences().getColor(COLOR_PLANTUML_PREPROCESSOR), SWT.NONE);
        addPresentation(reconciler, PLANTUML_DOUBLE_STRING.getId(), getPreferences().getColor(COLOR_PLANTUML_DOUBLESTRING), SWT.NONE);
        addPresentation(reconciler, PLANTUML_KEYWORD.getId(), getPreferences().getColor(COLOR_PLANTUML_KEYWORD), SWT.NONE);
        addPresentation(reconciler, PLANTUML_NOTE.getId(), getPreferences().getColor(COLOR_PLANTUML_NOTE), SWT.NONE);
        addPresentation(reconciler, PLANTUML_COMMENT.getId(), getPreferences().getColor(COLOR_PLANTUML_COMMENT), SWT.NONE);
        addPresentation(reconciler, PLANTUML_SKINPARAMETER.getId(), getPreferences().getColor(COLOR_PLANTUML_SKINPARAMETER), SWT.NONE);
        addPresentation(reconciler, PLANTUML_COLOR.getId(), getPreferences().getColor(COLOR_PLANTUML_COLOR), SWT.NONE);
        addPresentation(reconciler, PLANTUML_TYPE.getId(), getPreferences().getColor(COLOR_PLANTUML_TYPE), SWT.NONE);
        addPresentation(reconciler, PLANTUML_DIVIDER.getId(), getPreferences().getColor(COLOR_PLANTUML_DIVIDER), SWT.NONE);
        addPresentation(reconciler, PLANTUML_ARROW.getId(), getPreferences().getColor(COLOR_PLANTUML_ARROW), SWT.BOLD);
        addPresentation(reconciler, PLANTUML_LABEL.getId(), getPreferences().getColor(COLOR_PLANTUML_LABEL), SWT.NONE);
        return reconciler;
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
        if (scanner == null) {
            scanner = new AsciiDoctorDefaultTextScanner(colorManager);
            updateTextScannerDefaultColorToken();
        }
        return scanner;
    }

    public void updateTextScannerDefaultColorToken() {
        if (scanner == null) {
            return;
        }
        RGB color = getPreferences().getColor(COLOR_PLANTUML_NORMAL_TEXT);
        scanner.setDefaultReturnToken(createColorToken(color));
    }

}