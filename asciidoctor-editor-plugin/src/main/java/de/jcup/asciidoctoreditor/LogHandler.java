package de.jcup.asciidoctoreditor;

public interface LogHandler {

    public void logInfo(String message);
    
    public void logWarn(String message, Throwable t);
    
    public void logError(String message, Throwable t);
    
    public default void logError(String message) {
        logError(message,null);
    }
    
    public default void logWarn(String message) {
        logWarn(message,null);
    }
    
}
