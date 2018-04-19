/*
 * Copyright 2018 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
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
