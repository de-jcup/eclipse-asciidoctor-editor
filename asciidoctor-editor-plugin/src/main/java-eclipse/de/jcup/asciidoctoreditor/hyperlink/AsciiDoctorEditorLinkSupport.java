/*
 * Copyright 2018 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.hyperlink;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import de.jcup.asciidoctoreditor.AbstractAsciiDoctorEditorSupport;
import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.AsciiDoctorSourceViewerConfiguration;

public class AsciiDoctorEditorLinkSupport extends AbstractAsciiDoctorEditorSupport{
    

    public AsciiDoctorEditorLinkSupport(AsciiDoctorEditor editor){
        super(editor);
    }

    /**
     * Tries to resolve current cursor location as hyperlink and open it. When
     * more then one possibilities are found, only first one is used
     */
    public void openHyperlinkAtCurrentCursorPosition() {
        SourceViewerConfiguration conf = getSourceViewerConfiguration();
        if (!(conf instanceof AsciiDoctorSourceViewerConfiguration)) {
            return;
        }
        AsciiDoctorSourceViewerConfiguration asciiConf = (AsciiDoctorSourceViewerConfiguration) conf;
        IHyperlinkDetector[] detectors = asciiConf.getHyperlinkDetectors(getSourceViewer());
        if (detectors == null) {
            return;
        }
        IRegion region = new IRegion() {

            @Override
            public int getOffset() {
                return getEditor().getLastCaretPosition();
            }

            @Override
            public int getLength() {
                return 0;
            }
        };
        for (IHyperlinkDetector detector : detectors) {
            if (detector == null) {
                continue;
            }
            IHyperlink[] hyperlinks = detector.detectHyperlinks(getSourceViewer(), region, false);
            if (hyperlinks == null) {
                continue;
            }
            for (IHyperlink hyperLink : hyperlinks) {
                if (hyperLink != null) {
                    hyperLink.open();
                    return;
                }
            }
        }
    }
    
    private ISourceViewer getSourceViewer() {
        return getEditor().getAsciiDoctorSourceViewer();
    }

    private SourceViewerConfiguration getSourceViewerConfiguration() {
        return getEditor().getAsciiDoctorSourceViewerConfiguration();
    }

    
}
