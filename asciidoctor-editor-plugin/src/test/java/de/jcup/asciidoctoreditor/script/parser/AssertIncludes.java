package de.jcup.asciidoctoreditor.script.parser;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import de.jcup.asciidoctoreditor.script.AsciiDoctorInclude;

public class AssertIncludes {

	private List<AsciiDoctorInclude> includes;

	public AssertIncludes(List<AsciiDoctorInclude> headlines) {
		this.includes = headlines;
	}
	public AssertIncludes hasIncludes(int size) {
		assertEquals("Amount of includes differs",size, includes.size());
		return this;
	}
	public AssertInclude hasInclude(String target) {
		assertNotNull(target);
		Iterator<AsciiDoctorInclude> it = includes.iterator();
		while (it.hasNext()) {

			AsciiDoctorInclude headlineFound = it.next();
			if (target.equals(headlineFound.getLabel())) {
				return new AssertInclude(headlineFound);
			}
		}
		fail("no include found with label:" + target);
		return null;
	}

	public static AssertIncludes assertIncludes(List<AsciiDoctorInclude> headlines) {
		assertNotNull(headlines);
		return new AssertIncludes(headlines);
	}

	public class AssertInclude {
		private AsciiDoctorInclude include;

		private AssertInclude(AsciiDoctorInclude headline) {
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