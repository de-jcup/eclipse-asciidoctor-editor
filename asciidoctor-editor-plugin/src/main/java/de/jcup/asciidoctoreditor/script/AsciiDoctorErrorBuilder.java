package de.jcup.asciidoctoreditor.script;

public class AsciiDoctorErrorBuilder {

	private static final String ADOC_MARKER = ".adoc:";

	public AsciiDoctorError build(String originMessage){
		int start=-1;
		int end=-1;
		String message = null;
		if (originMessage==null){
			message="Unknown failure";
		}else{
			message = handleNotNullMessages(originMessage);
		}
		/* fall back to origin message */
		if (message==null){
			message = originMessage;
		}
		AsciiDoctorError error = new AsciiDoctorError(start, end, message.trim());
		return error;
	}

	protected String handleNotNullMessages(String originMessage) {
		int indexOfAdocMarker = originMessage.indexOf(ADOC_MARKER);
		if (indexOfAdocMarker!=-1){
			return originMessage.substring(indexOfAdocMarker+ADOC_MARKER.length());
		}
		return null;
	}
}
