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
package de.jcup.asciidoctoreditor;

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

import de.jcup.asciidoctoreditor.AsciiDocStringUtils.LinkTextData;
import de.jcup.asciidoctoreditor.script.AsciiDoctorHeadline;

public class AsciiDoctorEditorLinkTextHyperlinkDetector extends AbstractHyperlinkDetector {

	private IAdaptable adaptable;

	AsciiDoctorEditorLinkTextHyperlinkDetector(IAdaptable editor) {
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

		LinkTextData linkTextData = AsciiDocStringUtils.resolveTextFromStartToBracketsEnd(line, offset, offsetInLine);

		List<IHyperlink> hyperlinks = new ArrayList<>();
		append(hyperlinks, resolveLinkToInclude(linkTextData, editor));
		append(hyperlinks, resolveLinkToImage(linkTextData, editor));
		append(hyperlinks, resolveLinkToHeadline(linkTextData, editor));
		append(hyperlinks, resolveLinkToDiagram(linkTextData, editor));

		if (hyperlinks.isEmpty()) {
			return null;
		}
		return hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
	}

	private Region createTargetRegion(LinkTextData linkTextData) {
		Region targetRegion = new Region(linkTextData.offsetLeft, linkTextData.text.length());
		return targetRegion;
	}

	private void append(List<IHyperlink> hyperlinks, IHyperlink[] hyperlinkArray) {
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

	private IHyperlink[] resolveLinkToImage(LinkTextData linkTextData, AsciiDoctorEditor editor) {

		String imageName = AsciiDocStringUtils.resolveFilenameOfImageOrNull(linkTextData.text);
		if (imageName != null) {
			Region targetRegion = createTargetRegion(linkTextData);
			return new IHyperlink[] { new AsciiDoctorEditorOpenImageHyperlink(targetRegion, imageName, editor) };

		}
		return null;
	}

	private IHyperlink[] resolveLinkToHeadline(LinkTextData linkTextData, AsciiDoctorEditor editor) {
		String foundText = linkTextData.text;
		AsciiDoctorHeadline headline = editor.findAsciiDoctorHeadline(foundText);
		if (headline != null) {
			Region targetRegion = createTargetRegion(linkTextData);
			return new IHyperlink[] { new AsciiDoctorEditorHeadlineHyperlink(targetRegion, headline, editor) };
		}
		return null;
	}

	protected IHyperlink[] resolveLinkToInclude(LinkTextData linkTextData, AsciiDoctorEditor editor) {

		String foundText = linkTextData.text;
		String includeFileName = AsciiDocStringUtils.resolveFilenameOfIncludeOrNull(foundText);
		if (includeFileName != null) {
			Region targetRegion = createTargetRegion(linkTextData);
			return new IHyperlink[] {
					new AsciiDoctorEditorOpenIncludeHyperlink(targetRegion, includeFileName, editor) };
		}
		
		return null;
	}
	
	protected IHyperlink[] resolveLinkToDiagram(LinkTextData linkTextData, AsciiDoctorEditor editor) {

		String foundText = linkTextData.text;
		String diagramFileName = AsciiDocStringUtils.resolveFilenameOfDiagramMacroOrNull(foundText);
		if (diagramFileName != null) {
			Region targetRegion = createTargetRegion(linkTextData);
			return new IHyperlink[] {
					new AsciiDoctorEditorOpenDiagramHyperlink(targetRegion, diagramFileName, editor) };
		}
		
		return null;
	}

}
