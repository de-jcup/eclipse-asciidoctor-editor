/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this editorFile except in compliance with the License.
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
package de.jcup.asciidoctoreditor.hyperlink;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocStringUtils;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocStringUtils.LinkTextData;
import de.jcup.asciidoctoreditor.script.AsciiDoctorHeadline;

public class AsciiDoctorEditorLinkTextHyperlinkDetector extends AbstractHyperlinkDetector {

    private IAdaptable adaptable;

    public AsciiDoctorEditorLinkTextHyperlinkDetector(IAdaptable editor) {
        this.adaptable = editor;
    }

    @Override
    public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
        if (adaptable == null) {
            return null;
        }
        AsciiDoctorEditor editor = adaptable.getAdapter(AsciiDoctorEditor.class);
        if (editor == null) {
            return null;
        }
        return resolveHyperlinks(textViewer, region, editor);
    }

    public IHyperlink[] resolveHyperlinks(ITextViewer textViewer, IRegion region, AsciiDoctorEditor editor) {
        LinkDetectorContext context = createContext(textViewer, region, editor);

        context.append(resolveLinkToInclude(context));
        context.append(resolveLinkToImage(context));
        context.append(resolveLinkToHeadline(context));
        context.append(resolveLinkToDiagram(context));

        context.append(resolveLinkToShortCrossReference(context));

        return context.getHyperLinksArrayOrNull();

    }

    private LinkDetectorContext createContext(ITextViewer textViewer, IRegion region, AsciiDoctorEditor editor) {
        IDocument document = textViewer.getDocument();
        int offset = region.getOffset();

        IRegion lineInfo;
        String line;
        try {
            lineInfo = document.getLineInformationOfOffset(offset);
            line = document.get(lineInfo.getOffset(), lineInfo.getLength());
        } catch (BadLocationException ex) {
            return null;
        }

        int offsetInLine = offset - lineInfo.getOffset();

        LinkDetectorContext context = new LinkDetectorContext(editor, line, offset, offsetInLine);
        return context;
    }

    private class LinkDetectorContext {
        private List<IHyperlink> hyperlinks = new ArrayList<>();
        private LinkTextData whiteSpaceBorderedlinkTextData;
        private LinkTextData comparisionSignsBorderedLinkTextData;
        private AsciiDoctorEditor editor;

        public LinkDetectorContext(AsciiDoctorEditor editor, String line, int offset, int offsetInLine) {
            this.editor = editor;
            this.whiteSpaceBorderedlinkTextData = AsciiDocStringUtils.resolveTextFromStartToBracketsEnd(line, offset, offsetInLine);
            this.comparisionSignsBorderedLinkTextData = AsciiDocStringUtils.resolveComparisionSignsBorderedAreaFromStartToBracketsEnd(line, offset, offsetInLine);
        }

        public IHyperlink[] getHyperLinksArrayOrNull() {
            if (hyperlinks.isEmpty()) {
                return null;
            }
            return hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
        }

        private void append(IHyperlink... hyperlinkArray) {
            if (hyperlinkArray == null) {
                return;
            }
            for (IHyperlink hyperlink : hyperlinkArray) {
                if (hyperlink == null) {
                    continue;
                }
                hyperlinks.add(hyperlink);
            }

        }
        
        public String getCrossRefShortCutlinkTextDataOrNull() {
            return comparisionSignsBorderedLinkTextData.text;
        }

        public String getWhiteSpaceBorderedLinkTextOrNull() {
            return whiteSpaceBorderedlinkTextData.text;
        }

        public boolean hasHyperlinks() {
            return ! hyperlinks.isEmpty();
        }

    }

    private Region createTargetRegionForWhitespaceBordered(LinkDetectorContext context) {
        LinkTextData textData = context.whiteSpaceBorderedlinkTextData;
        Region targetRegion = new Region(textData.offsetLeft, textData.text.length());
        return targetRegion;
    }
    private Region createTargetRegionForComparisionSignsBordered(LinkDetectorContext context) {
        LinkTextData textData = context.comparisionSignsBorderedLinkTextData;
        Region targetRegion = new Region(textData.offsetLeft, textData.text.length());
        return targetRegion;
    }

    protected IHyperlink[] resolveLinkToImage(LinkDetectorContext context) {
        if (context.hasHyperlinks()) {
            return null;
        }
        String imageName = AsciiDocStringUtils.resolveFilenameOfImageOrNull(context.getWhiteSpaceBorderedLinkTextOrNull());
        if (imageName != null) {
            Region targetRegion = createTargetRegionForWhitespaceBordered(context);
            return new IHyperlink[] { new AsciiDoctorEditorOpenImageHyperlink(targetRegion, imageName, context.editor) };

        }
        return null;
    }

    protected IHyperlink[] resolveLinkToHeadline(LinkDetectorContext context) {
        if (context.hasHyperlinks()) {
            return null;
        }
        String foundText = context.getWhiteSpaceBorderedLinkTextOrNull();
        AsciiDoctorHeadline headline = context.editor.findAsciiDoctorHeadlineByName(foundText);
        if (headline != null) {
            Region targetRegion = createTargetRegionForWhitespaceBordered(context);
            return new IHyperlink[] { new AsciiDoctorEditorHeadlineHyperlink(targetRegion, headline, context.editor) };
        }
        return null;
    }

    protected IHyperlink[] resolveLinkToInclude(LinkDetectorContext context) {
        if (context.hasHyperlinks()) {
            return null;
        }
        String foundText = context.getWhiteSpaceBorderedLinkTextOrNull();
        String includeFileName = AsciiDocStringUtils.resolveFilenameOfIncludeOrNull(foundText);
        if (includeFileName != null) {
            Region targetRegion = createTargetRegionForWhitespaceBordered(context);
            return new IHyperlink[] { new AsciiDoctorEditorOpenIncludeHyperlink(targetRegion, includeFileName, context.editor) };
        }

        return null;
    }

    protected IHyperlink[] resolveLinkToShortCrossReference(LinkDetectorContext context) {
        if (context.hasHyperlinks()) {
            return null;
        }
        String foundText = context.getCrossRefShortCutlinkTextDataOrNull();
        String crossReferenceId = AsciiDocStringUtils.resolveCrossReferenceIdOrNull(foundText);
        if (crossReferenceId != null) {
            Region targetRegion = createTargetRegionForComparisionSignsBordered(context);
            return new IHyperlink[] { new AsciiDoctorEditorOpenCrossReferenceHyperlink(targetRegion, crossReferenceId, context.editor) };
        }

        return null;
    }

    protected IHyperlink[] resolveLinkToDiagram(LinkDetectorContext context) {
        if (context.hasHyperlinks()) {
            return null;
        }

        String foundText = context.getWhiteSpaceBorderedLinkTextOrNull();
        String diagramFileName = AsciiDocStringUtils.resolveFilenameOfDiagramMacroOrNull(foundText);
        if (diagramFileName != null) {
            Region targetRegion = createTargetRegionForWhitespaceBordered(context);
            return new IHyperlink[] { new AsciiDoctorEditorOpenDiagramHyperlink(targetRegion, diagramFileName, context.editor) };
        }

        return null;
    }

}
