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
package de.jcup.asciidoctoreditor.script.parser;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import de.jcup.asciidoctoreditor.script.AsciiDoctorInlineAnchor;

public class AssertInlineAnchors {

	private List<AsciiDoctorInlineAnchor> includes;

	public AssertInlineAnchors(List<AsciiDoctorInlineAnchor> headlines) {
		this.includes = headlines;
	}
	public AssertInlineAnchors hasInlineAnchors(int size) {
		assertEquals("Amount of includes differs",size, includes.size());
		return this;
	}
	public AssertInlineAnchor hasInlineAnchor(String target) {
		assertNotNull(target);
		Iterator<AsciiDoctorInlineAnchor> it = includes.iterator();
		while (it.hasNext()) {

			AsciiDoctorInlineAnchor headlineFound = it.next();
			if (target.equals(headlineFound.getLabel())) {
				return new AssertInlineAnchor(headlineFound);
			}
		}
		fail("no inlineAnchor found with label:" + target);
		return null;
	}

	public static AssertInlineAnchors assertInlineAnchors(List<AsciiDoctorInlineAnchor> headlines) {
		assertNotNull(headlines);
		return new AssertInlineAnchors(headlines);
	}

	public class AssertInlineAnchor {
		private AsciiDoctorInlineAnchor inlineAnchor;

		private AssertInlineAnchor(AsciiDoctorInlineAnchor headline) {
			assertNotNull(headline);
			this.inlineAnchor = headline;
		}

		public AssertInlineAnchor withPosition(int position) {
			assertEquals(inlineAnchor.getLabel()+":Position not as expected!", position, this.inlineAnchor.getPosition());
			return this;
		}

		public AssertInlineAnchor withEnd(int end) {
			assertEquals(inlineAnchor.getLabel()+":End not as expected!", end, this.inlineAnchor.getEnd());
			return this;
		}

		public AssertInlineAnchors and() {
			return AssertInlineAnchors.this;
		}

		public AssertInlineAnchor withId(String id) {
			assertEquals(inlineAnchor.getLabel()+":ID not as expected!", id, this.inlineAnchor.getId());
			return this;
		}
	}

}