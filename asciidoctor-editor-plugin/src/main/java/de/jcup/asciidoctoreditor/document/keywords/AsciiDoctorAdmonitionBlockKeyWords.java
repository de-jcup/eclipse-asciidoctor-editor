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
package de.jcup.asciidoctoreditor.document.keywords;

import de.jcup.eclipse.commons.keyword.DocumentKeyWord;
import de.jcup.eclipse.commons.keyword.TooltipTextSupport;

public enum AsciiDoctorAdmonitionBlockKeyWords implements DocumentKeyWord {

	TIP("https://asciidoctor.org/docs/asciidoc-syntax-quick-reference/#admon-bl"),

	NOTE("https://asciidoctor.org/docs/asciidoc-syntax-quick-reference/#admon-bl"),

	IMPORTANT("https://asciidoctor.org/docs/asciidoc-syntax-quick-reference/#admon-bl"),

	WARNING("https://asciidoctor.org/docs/asciidoc-syntax-quick-reference/#admon-bl"),

	CAUTION("https://asciidoctor.org/docs/asciidoc-syntax-quick-reference/#admon-bl"),;

	private String text;

	private AsciiDoctorAdmonitionBlockKeyWords(String linkToOnlineDocumentation) {
		this.text = name() + "]"; // we do not use "["+name()+"]", for details
									// see AsciiDoctorTextHover.java: it uses
									// SimpleStringUtils.nextReducedVariableWord(text,
									// offset); so [ will be removed
									// not very smart - but it works...
		tooltip = TooltipTextSupport.getTooltipText("admonition-blocks");
		if (tooltip == null || tooltip.isEmpty()) {
			tooltip = "An internal asciidoctor attribute. See online documentation for mor information.";
		}
		this.linkToDocumentation = linkToOnlineDocumentation;
		if (this.linkToDocumentation == null) {
			this.linkToDocumentation = "https://asciidoctor.org/docs/user-manual";
		}
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public boolean isBreakingOnEof() {
		return false;
	}

	private String tooltip;
	private String linkToDocumentation;

	@Override
	public String getTooltip() {
		return tooltip;
	}

	@Override
	public String getLinkToDocumentation() {
		return linkToDocumentation;
	}

}
