package de.jcup.asciidoctoreditor;

public class SimpleExceptionUtils {

	public static String getRootMessage(Throwable t){
		if (t==null){
			return null;
		}
		String message = getRootMessage(t.getCause());
		if (message==null){
			return t.getMessage();
		}
		return message;
	}
}
