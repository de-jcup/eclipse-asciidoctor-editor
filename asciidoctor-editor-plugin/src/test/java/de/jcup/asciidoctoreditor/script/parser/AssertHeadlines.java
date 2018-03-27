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

}