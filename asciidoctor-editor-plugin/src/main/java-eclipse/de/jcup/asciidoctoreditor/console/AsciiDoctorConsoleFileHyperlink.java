package de.jcup.asciidoctoreditor.console;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.IHyperlink;

import de.jcup.asciidoctoreditor.AsciiDoctorEclipseLogAdapter;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class AsciiDoctorConsoleFileHyperlink implements IHyperlink {
    File file;

    public AsciiDoctorConsoleFileHyperlink(File file) {
        this.file = file;
    }

    @Override
    public void linkActivated() {
        if (file==null) {
            return;
        }
        if (!file.exists()) {
            MessageDialog.openWarning(EclipseUtil.getActiveWorkbenchShell(), "Link problem", "The file "+file.getAbsolutePath()+" does not exist!");
            return;
        }
        try {
            EclipseUtil.openInEditor(file);
        } catch (PartInitException e) {
            AsciiDoctorEclipseLogAdapter.INSTANCE.logError("Cannot open " + file.getAbsolutePath(), e);
        }
    }

    @Override
    public void linkEntered() {
    }

    @Override
    public void linkExited() {
    }
}
