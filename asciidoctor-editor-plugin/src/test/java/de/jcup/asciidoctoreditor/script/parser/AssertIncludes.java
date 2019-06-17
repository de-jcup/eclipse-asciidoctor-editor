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

import de.jcup.asciidoctoreditor.script.AsciiDoctorFileReference;

public class AssertIncludes {

	private List<AsciiDoctorFileReference> includes;

	public AssertIncludes(List<AsciiDoctorFileReference> headlines) {
		this.includes = headlines;
	}
	public AssertIncludes hasIncludes(int size) {
		assertEquals("Amount of includes differs",size, includes.size());
		return this;
	}
	public AssertInclude hasInclude(String target) {
		assertNotNull(target);
		Iterator<AsciiDoctorFileReference> it = includes.iterator();
		while (it.hasNext()) {

			AsciiDoctorFileReference headlineFound = it.next();
			if (target.equals(headlineFound.getLabel())) {
				return new AssertInclude(headlineFound);
			}
		}
		fail("no include found with label:" + target);
		return null;
	}

	public static AssertIncludes assertIncludes(List<AsciiDoctorFileReference> headlines) {
		assertNotNull(headlines);
		return new AssertIncludes(headlines);
	}

	public class AssertInclude {
		private AsciiDoctorFileReference include;

		private AssertInclude(AsciiDoctorFileReference headline) {
			assertNotNull(headline);
			this.include = headline;
		}

		public AssertInclude withPosition(int position) {
			assertEquals(include.getLabel()+":Position not as expected!", position, this.include.getPosition());
			return this;
		}

		public AssertInclude withEnd(int end) {
			assertEquals(include.getLabel()+":End not as expected!", end, this.include.getEnd());
			return this;
		}

		public AssertIncludes and() {
			return AssertIncludes.this;
		}
	}

}