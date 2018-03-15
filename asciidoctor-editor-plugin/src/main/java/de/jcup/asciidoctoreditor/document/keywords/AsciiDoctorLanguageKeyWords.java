/*
 * Copyright 2017 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an"AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.asciidoctoreditor.document.keywords;

// see http://docs.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html
public enum AsciiDoctorLanguageKeyWords implements DocumentKeyWord {
	/* @formatter:off*/
	/* ---------------- */
	/* ---- some keywords for internal eval etc.------------*/
	
	IFDEF("https://asciidoctor.org/docs/user-manual/#ifdef-directive"),
	IFNDEF("https://asciidoctor.org/docs/user-manual/#ifndef-directive"),

	;
	/* @formatter:on*/

	private String text;
	private boolean breaksOnEof;
	private AsciiDoctorLanguageKeyWords() {
		this(null);
	}
	private AsciiDoctorLanguageKeyWords(String linkToDocumentation) {
		this(linkToDocumentation,null);
	}
	
	private AsciiDoctorLanguageKeyWords(String linkToDocumentation, String tooltipId) {
		this.text = name().toLowerCase();
		this.breaksOnEof = false;
		if (tooltipId==null){
			tooltipId=text;
		}
		this.tooltip = TooltipTextSupport.getTooltipText(tooltipId);
		this.linkToDocumentation = linkToDocumentation;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public boolean isBreakingOnEof() {
		return breaksOnEof;
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
