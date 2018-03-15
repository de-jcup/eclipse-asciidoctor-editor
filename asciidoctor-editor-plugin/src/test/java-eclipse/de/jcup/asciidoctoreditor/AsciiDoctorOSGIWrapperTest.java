package de.jcup.asciidoctoreditor;

import static org.junit.Assert.*;

import org.junit.Test;

public class AsciiDoctorOSGIWrapperTest {

	@Test
	public void wrapperCreationIsPossible() {
		new AsciiDoctorOSGIWrapper();
	}

	@Test
	public void wrapperCreatesNonEmptyHTMLforHeadline() {
		String html = new AsciiDoctorOSGIWrapper().convertToHTML("= headline");
		
		assertNotNull(html);
		assertFalse(html.isEmpty());
	}


}
