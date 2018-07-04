package de.jcup.asciidoctoreditor;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.jcup.asciidoctoreditor.EndlessLoopPreventer.EndlessLoopException;

public class EndlessLoopPreventerTest {

	@Rule
	public ExpectedException expected = ExpectedException.none();
	
	@Test
	public void setting_max_2_and_calling_three_times_throws_loopexception() {
		/* prepare*/
		EndlessLoopPreventer preventer = new EndlessLoopPreventer(2);
		
		/* test */
		preventer.assertNoEndlessLoop();
		preventer.assertNoEndlessLoop();
		
		/* next call must throw endless loop exception */
		expected.expect(EndlessLoopException.class);
		preventer.assertNoEndlessLoop();
	}

}
