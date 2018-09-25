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
