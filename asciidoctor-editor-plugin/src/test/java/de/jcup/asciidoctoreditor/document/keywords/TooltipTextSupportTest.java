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
package de.jcup.asciidoctoreditor.document.keywords;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

import de.jcup.asciidoctoreditor.ResourceInputStreamProvider;
import de.jcup.asciidoctoreditor.document.keywords.TooltipTextSupport;

public class TooltipTextSupportTest {

	private TooltipTextSupport supportToTest;

	@Before
	public void before(){
		supportToTest = new TooltipTextSupport();
		supportToTest.setResourceInputStreamProvider(new TestResourceInputstreamProvider());
	}
	
	@Test
	public void support_get_null_returns_empty_string() {
		/* execute */
		String result = supportToTest.get(null);
		
		/* test */
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void support_get_ifdef_returns_not_empty_string() {
		/* execute */
		String result = supportToTest.get("ifdef");
		/* test */
		assertFalse(result.isEmpty());
	}
	
	@Test
	public void support_get_unknown_returns_empty_string() {
		/* execute */
		String result = supportToTest.get("unknown");
		/* test */
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void support_no_resource_handler_set_get_unknown_returns_empty_string() {
		/* prepare */
		supportToTest.setResourceInputStreamProvider(null);
		
		/* execute */
		String result = supportToTest.get("unknown");
		/* test */
		assertTrue(result.isEmpty());
	}

	private static class TestResourceInputstreamProvider implements ResourceInputStreamProvider{
		/* eclipse junit execution*/
		private static File TOOLTIPS_FOLDER = new File("./tooltips");
		static{
			if (!TOOLTIPS_FOLDER.exists()){
				/* gradle */
				TOOLTIPS_FOLDER=new File("./asciidoctor-editor-plugin/tooltips");
			}
		}
		@Override
		public InputStream getStreamFor(String path) throws IOException {
			File file = new File(TOOLTIPS_FOLDER.getParentFile(),path);
			return new FileInputStream(file);
		}
		
	}
}