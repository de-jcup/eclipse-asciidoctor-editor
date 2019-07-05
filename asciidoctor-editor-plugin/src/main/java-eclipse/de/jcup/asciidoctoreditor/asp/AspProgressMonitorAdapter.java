package de.jcup.asciidoctoreditor.asp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import de.jcup.asp.client.AspClientProgressMonitor;

public class AspProgressMonitorAdapter implements AspClientProgressMonitor{
    private static final IProgressMonitor NULL_PROGRESS = new NullProgressMonitor();
    private IProgressMonitor monitor;
    
    public AspProgressMonitorAdapter(IProgressMonitor monitor) {
        if (monitor==null) {
            monitor=NULL_PROGRESS;
        }
        this.monitor=monitor;
    }
    
    @Override
    public boolean isCanceled() {
        return monitor.isCanceled();
    }

}
