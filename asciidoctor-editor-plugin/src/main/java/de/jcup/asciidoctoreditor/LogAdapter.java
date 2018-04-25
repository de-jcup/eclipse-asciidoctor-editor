package de.jcup.asciidoctoreditor;

public interface LogAdapter {

	public void logInfo(String message);

	public void logWarn(String message);

	public void logError(String string, Throwable t);
}
