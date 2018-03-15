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

import org.junit.Test;

import de.jcup.asciidoctoreditor.document.keywords.DocumentKeyWord;
import de.jcup.asciidoctoreditor.document.keywords.DocumentKeyWords;

public class DocumentKeyWordsTest {

	@Test
	public void all_key_words_can_be_initialzed() {
		DocumentKeyWord[] results = DocumentKeyWords.getAll();
		assertNotNull(results);
	}

}
