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
	/* Reserved words : https://www.gnu.org/software/asciidoctor/manual/html_node/Reserved-Word-Index.html
	/* ----------------*/
	
	/* C */
	CASE("https://www.gnu.org/software/asciidoctor/manual/html_node/Conditional-Constructs.html#index-case"),

	/* D */ 
	DO("https://www.gnu.org/software/asciidoctor/manual/html_node/Looping-Constructs.html#index-do"),
	DONE("https://www.gnu.org/software/asciidoctor/manual/html_node/Looping-Constructs.html#index-do","do"),

	/* E */
	ELIF("https://www.gnu.org/software/asciidoctor/manual/html_node/Conditional-Constructs.html#index-elif","if"),
	ELSE("https://www.gnu.org/software/asciidoctor/manual/html_node/Conditional-Constructs.html#index-elif","if"),
	ESAC("https://www.gnu.org/software/asciidoctor/manual/html_node/Conditional-Constructs.html#index-case","case"),
	
	/* F */
	FI("https://www.gnu.org/software/asciidoctor/manual/html_node/Conditional-Constructs.html#index-if","if"),
	FOR("https://www.gnu.org/software/asciidoctor/manual/html_node/Looping-Constructs.html#index-for"),
	FUNCTION("https://www.gnu.org/software/asciidoctor/manual/html_node/Shell-Functions.html#index-function"),

	/* I*/
	IF("https://www.gnu.org/software/asciidoctor/manual/html_node/Conditional-Constructs.html#index-if"),
	IN("https://www.gnu.org/software/asciidoctor/manual/html_node/Conditional-Constructs.html#index-in","case"),

	/* S */
	SELECT("https://www.gnu.org/software/asciidoctor/manual/html_node/Conditional-Constructs.html#index-select"),
	
	/* T */
	THEN("https://www.gnu.org/software/asciidoctor/manual/html_node/Conditional-Constructs.html#index-then","if"),
	TIME("https://www.gnu.org/software/asciidoctor/manual/html_node/Pipelines.html#index-time"),

	/* U */
	UNTIL("https://www.gnu.org/software/asciidoctor/manual/html_node/Looping-Constructs.html#index-until"),
	
	/* W */
	WHILE("https://www.gnu.org/software/asciidoctor/manual/html_node/Looping-Constructs.html#index-while"),

	

	/* Built in commands */
	// see https://askubuntu.com/questions/512918/how-do-i-list-all-available-shell-builtin-commands
	ALIAS                 ("http://tldp.org/LDP/abs/html/aliases.html"),
	BG                    ("https://www.unix.com/man-page/linux/1/bg/"),
	BIND                  ("http://man7.org/linux/man-pages/man2/bind.2.html"),
	BREAK                 ("http://tldp.org/LDP/abs/html/loopcontrol.html"),
	BUILTIN               ,
	CALLER                ("http://tldp.org/LDP/abs/html/internal.html#CALLERREF"),
	CD                    ("http://linuxcommand.org/lc3_man_pages/cdh.html"),
	COMMAND               ,
	COMPGEN               ,
	COMPLETE              ,
	COMPOPT               ,
	CONTINUE              ,
	DECLARE               ,
	DIRS                  ,
	DISOWN                ,
	ECHO                  ("https://www.gnu.org/software/asciidoctor/manual/html_node/AsciiDoctorAccess-Builtins.html"),
	ENABLE                ,
	EVAL                  ,
	EXEC                  ,
	EXIT                  ("http://tldp.org/LDP/abs/html/exit-status.html"),
	EXPORT                ,
	FALSE                 ,
	FC                    ,
	FG                    ,
	GETOPTS               ,
	HASH                  ("https://www.gnu.org/software/asciidoctor/manual/html_node/Bourne-Shell-Builtins.html"),
	HELP                  ,
	HISTORY               ,
	JOBS                  ,
	KILL                  ("http://man7.org/linux/man-pages/man1/kill.1.html"),
	LET                   ,
	LOCAL                 ,
	LOGOUT                ,
	MAPFILE               ,
	POPD                  ,
	PRINTF                ,
	PUSHD                 ,
	PWD                   ,
	READ                  ,
	READARRAY             ,
	READONLY              ,
	RETURN                ,
	SET                   ,
	SHIFT                 ("http://tldp.org/LDP/AsciiDoctorAccess-Beginners-Guide/html/sect_09_07.html"),
	SHOPT                 ,
	SOURCE                ,
	SUSPEND               ,
	TEST                  ,
	TIMES                 ,
	TRAP                  ("http://tldp.org/LDP/abs/html/debugging.html"),
	TRUE                  ,
	TYPE                  ,
	TYPESET               ,
	ULIMIT                ("http://tldp.org/LDP/abs/html/system.html"),
	UMASK                 ,
	UNALIAS               ,
	UNSET                 ,
	WAIT                  ,
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
