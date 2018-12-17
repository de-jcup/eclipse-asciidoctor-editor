package de.jcup.asciidoctoreditor;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

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
