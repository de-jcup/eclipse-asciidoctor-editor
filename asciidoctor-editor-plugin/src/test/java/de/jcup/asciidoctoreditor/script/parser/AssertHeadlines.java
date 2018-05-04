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

import de.jcup.asciidoctoreditor.script.AsciiDoctorHeadline;

public class AssertHeadlines {

	private List<AsciiDoctorHeadline> headlines;

	public AssertHeadlines(List<AsciiDoctorHeadline> headlines) {
		this.headlines = headlines;
	}
	public AssertHeadlines hasHeadlines(int size) {
		assertEquals("Amount of headlines differs",size, headlines.size());
		return this;
	}
	public AssertHeadline hasHeadline(String name) {
		assertNotNull(name);
		Iterator<AsciiDoctorHeadline> it = headlines.iterator();
		while (it.hasNext()) {

			AsciiDoctorHeadline headlineFound = it.next();
			if (name.equals(headlineFound.getName())) {
				return new AssertHeadline(headlineFound);
			}
		}
		fail("no headline found:" + name);
		return null;
	}

	public static AssertHeadlines assertHeadlines(List<AsciiDoctorHeadline> headlines) {
		assertNotNull(headlines);
		return new AssertHeadlines(headlines);
	}

	public class AssertHeadline {
		private AsciiDoctorHeadline headline;

		private AssertHeadline(AsciiDoctorHeadline headline) {
			assertNotNull(headline);
			this.headline = headline;
		}

		public AssertHeadline withDeep(int deep) {
			assertEquals(headline.getName()+":Deep not as expected!", deep, this.headline.getDeep());
			return this;
		}

		public AssertHeadline withPosition(int position) {
			assertEquals(headline.getName()+":Position not as expected!", position, this.headline.getPosition());
			return this;
		}

		public AssertHeadline withEnd(int end) {
			assertEquals(headline.getName()+":End not as expected!", end, this.headline.getEnd());
			return this;
		}

		public AssertHeadlines and() {
			return AssertHeadlines.this;
		}
	}

	public AssertHeadlines hasNoHeadlines() {
		return hasHeadlines(0);
	}

}