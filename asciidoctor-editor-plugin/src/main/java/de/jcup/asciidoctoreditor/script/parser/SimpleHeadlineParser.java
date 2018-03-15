package de.jcup.asciidoctoreditor.script.parser;

import java.util.ArrayList;
import java.util.List;

import de.jcup.asciidoctoreditor.script.AsciiDoctorHeadline;

public class SimpleHeadlineParser {

	public List<AsciiDoctorHeadline> parse(String asciidoctorScript) {
		List<AsciiDoctorHeadline> list = new ArrayList<AsciiDoctorHeadline>();
		if (asciidoctorScript==null){
			return list;
		}
		String[] splitted = asciidoctorScript.split("\n");
		for (String splitpart: splitted){
			if (splitpart==null){
				continue;
			}
			if (!splitpart.startsWith("=")){
				continue;
			}
			int deep=0;
			for (int i=0;i<splitpart.length();i++){
				if (splitpart.charAt(i)!='='){
					break;
				}
				deep++;
			}
			String headlineText=splitpart.substring(deep).trim();
			AsciiDoctorHeadline headline = new AsciiDoctorHeadline(deep,headlineText);
			list.add(headline);
		}
		return list;
	}

}
