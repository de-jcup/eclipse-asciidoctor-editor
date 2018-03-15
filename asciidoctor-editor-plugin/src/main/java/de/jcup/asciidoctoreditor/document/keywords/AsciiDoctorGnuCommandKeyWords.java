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

public enum AsciiDoctorGnuCommandKeyWords implements DocumentKeyWord {

	CP("http://tldp.org/LDP/abs/html/basic.html"),

	CHMOD("http://tldp.org/LDP/abs/html/basic.html"),

	SUDO("http://tldp.org/LDP/abs/html/system.html"),

	GREP("https://www.gnu.org/software/grep/manual/grep.html"),

	CAT("http://tldp.org/LDP/abs/html/basic.html"),

	FILTER(),

	UNAME("http://tldp.org/LDP/abs/html/system.html"),

	RM("http://tldp.org/LDP/abs/html/basic.html"),

	RMDIR("http://tldp.org/LDP/abs/html/basic.html"),

	MKDIR("http://tldp.org/LDP/abs/html/basic.html"),

	TPUT("https://www.tldp.org/HOWTO/AsciiDoctorAccess-Prompt-HOWTO/x405.html"),

	TERMINFO("http://www.tldp.org/HOWTO/Text-Terminal-HOWTO-16.html"),

	PS("http://tldp.org/LDP/abs/html/system.html"),

	LS("http://tldp.org/LDP/abs/html/system.html"),

	AWK("https://www.tldp.org/LDP/abs/html/abs-guide.html#AWK"),

	SED("http://tldp.org/LDP/abs/html/x23170.html"),

	WC("http://man7.org/linux/man-pages/man1/wc.1.html"),

	TR("http://linuxcommand.org/lc3_man_pages/tr1.html"),

	MV("http://tldp.org/LDP/abs/html/basic.html"),

	TAR("http://tldp.org/LDP/abs/html/filearchiv.html"),

	SSH("https://linux.die.net/man/1/ssh"),

	PING("http://www.tldp.org/LDP/GNU-Linux-Tools-Summary/html/c8319.htm"),

	TOUCH("https://linux.die.net/man/1/touch"),

	GZIP("http://tldp.org/LDP/abs/html/filearchiv.html#GZIPREF"),

	TEE("https://linux.die.net/man/1/tee"),

	;

	private String text;

	private AsciiDoctorGnuCommandKeyWords() {
		this(null);
	}

	private AsciiDoctorGnuCommandKeyWords(String linkToDocumentation) {
		this.text = name().toLowerCase();
		this.tooltip = TooltipTextSupport.getTooltipText(text);
		this.linkToDocumentation = linkToDocumentation;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public boolean isBreakingOnEof() {
		return true;
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
