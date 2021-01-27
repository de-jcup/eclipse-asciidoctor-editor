package de.jcup.asciidoctoreditor;

import java.io.PrintStream;

public class PrintStreamLogHandler implements LogHandler{

    @Override
    public void logError(String message, Throwable t) {
        log(System.err, "ERROR:",message,t);
    }

    @Override
    public void logInfo(String message) {
       log(System.out, "INFO:",message,null);
    }

    @Override
    public void logWarn(String message, Throwable t) {
        log(System.err, "WARN:",message,t);
    }
    
    private void log(PrintStream p, String prefix, String message, Throwable t) {
        p.print(prefix);
        if (message!=null) {
            p.println(message);
        }
        if (t!=null) {
            t.printStackTrace(p);
        }
    }

}
