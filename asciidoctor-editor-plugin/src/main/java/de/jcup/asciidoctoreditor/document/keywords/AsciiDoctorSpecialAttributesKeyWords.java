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

public enum AsciiDoctorSpecialAttributesKeyWords implements DocumentKeyWord {


	IMAGESDIR("https://asciidoctor.org/docs/user-manual/#setting-the-location-of-images"),
	
	ICONS("https://asciidoctor.org/docs/user-manual/#icons"),
	
	TOC("https://asciidoctor.org/docs/user-manual/#user-toc")
	;

	private String text;
	
	private AsciiDoctorSpecialAttributesKeyWords(String linkToOnlineDocumentation) {
		this.text = ":" + name().toLowerCase()+":";
		tooltip = TooltipTextSupport.getTooltipText(name().toLowerCase());
		if (tooltip == null || tooltip.isEmpty()) {
			tooltip = "An internal asciidoctor attribute. See online documentation for mor information.";
		}
		this.linkToDocumentation = linkToOnlineDocumentation;
		if (this.linkToDocumentation==null){
			this.linkToDocumentation="https://asciidoctor.org/docs/user-manual";
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
