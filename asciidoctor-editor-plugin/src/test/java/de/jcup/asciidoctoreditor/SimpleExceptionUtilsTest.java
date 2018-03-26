package de.jcup.asciidoctoreditor;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class SimpleExceptionUtilsTest {
	
	@Test
	public void throwable_ioexception_runtime_exception_root_message_found() {
		/* prepare */
		IOException e = new IOException(new RuntimeException("root message"));
		Throwable t = new Throwable(e);

		/* test + execute */
		assertEquals("root message", SimpleExceptionUtils.getRootMessage(t));
	}

	@Test
	public void throwable_root_message_found() {
		/* prepare */
		Throwable t = new Throwable("root message");

		/* test + execute */
		assertEquals("root message", SimpleExceptionUtils.getRootMessage(t));
	}

	@Test
	public void throwable_message_null_is_null() {
		/* prepare */
		Throwable t = new Throwable();

		/* test + execute */
		assertEquals(null, SimpleExceptionUtils.getRootMessage(t));
	}

	@Test
	public void null_is_null() {

		/* test + execute */
		assertEquals(null, SimpleExceptionUtils.getRootMessage(null));
	}

}
