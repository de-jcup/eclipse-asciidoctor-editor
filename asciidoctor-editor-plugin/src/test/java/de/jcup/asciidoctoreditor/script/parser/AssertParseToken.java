/*
 * Copyright 2017 Albert Tregnaghi
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

import de.jcup.asciidoctoreditor.script.parser.ParseToken;

public class AssertParseToken {

	public static AssertParseToken assertThat(ParseToken token){
		return new AssertParseToken(token);
	}

	private ParseToken token;
	
	public AssertParseToken(ParseToken token) {
		assertNotNull("Parse token may not be null!", token);
		this.token=token;
	}
	
	public AssertParseToken hasStart(int position){
		assertEquals("start posistion not as expected",position,token.start);
		return this;
	}
	
	public AssertParseToken hasEnd(int position){
		assertEquals("end posistion not as expected", position,token.end);
		return this;
	}
	
}
