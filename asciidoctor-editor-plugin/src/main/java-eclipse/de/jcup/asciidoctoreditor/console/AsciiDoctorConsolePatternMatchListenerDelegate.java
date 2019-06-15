package de.jcup.asciidoctoreditor.console;

import java.io.File;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IPatternMatchListenerDelegate;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

import de.jcup.asciidoctoreditor.AsciiDoctorEclipseLogAdapter;

public class AsciiDoctorConsolePatternMatchListenerDelegate implements IPatternMatchListenerDelegate{

    private TextConsole console;

    @Override
    public void connect(TextConsole console) {
        this.console=console;
    }

    @Override
    public void disconnect() {
        this.console=null;
    }

    @Override
    public void matchFound(PatternMatchEvent event) {
        if (console==null) {
            return;
        }
        int offset = event.getOffset();
        int length = event.getLength();
        try {
        String content = console.getDocument().get(offset, length);
        if (! content.startsWith("file:")){
            return;
        }
        String absoluteFilePath = content.substring("file:".length());
        File file = new File(absoluteFilePath);
      
        
        IHyperlink hyperlink = new AsciiDoctorConsoleFileHyperlink(file);
            console.addHyperlink(hyperlink, offset, length);
        } catch (BadLocationException e) {
            AsciiDoctorEclipseLogAdapter.INSTANCE.logError("Cannot add hyperlink", e);
        }
    }


}
