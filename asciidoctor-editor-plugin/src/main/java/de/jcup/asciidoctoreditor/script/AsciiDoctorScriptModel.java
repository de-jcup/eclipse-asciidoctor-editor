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
package de.jcup.asciidoctoreditor.script;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.jcup.asciidoctoreditor.script.parser.ParseToken;

public class AsciiDoctorScriptModel {

	Collection<AsciiDoctorHeadline> headlines = new ArrayList<>();
	Collection<AsciiDoctorInclude> includes = new ArrayList<>(); 
	Collection<AsciiDoctorMarker> errors = new ArrayList<>();
	Collection<AsciiDoctorInlineAnchor> inlineAnchors = new ArrayList<>();
	List<ParseToken> debugTokenList;

	public Collection<AsciiDoctorHeadline> getHeadlines() {
		return headlines;
	}

	public Collection<AsciiDoctorInclude> getIncludes() {
		return includes;
	}
	
	public Collection<AsciiDoctorInlineAnchor> getInlineAnchors() {
		return inlineAnchors;
	}

	public Collection<AsciiDoctorMarker> getErrors() {
		return errors;
	}

	public boolean hasErrors() {
		return !getErrors().isEmpty();
	}

	/**
	 * Returns a debug token list - if list is null, a new one will be created
	 * 
	 * @return debug token list, never <code>null</code>
	 */
	public List<ParseToken> getDebugTokens() {
		if (debugTokenList == null) {
			debugTokenList = new ArrayList<>();
		}
		return debugTokenList;
	}

	public boolean hasDebugTokens() {
		return debugTokenList != null;
	}


}
