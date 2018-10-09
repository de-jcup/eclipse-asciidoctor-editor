package de.jcup.asciidoctoreditor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CLITextUtil {
	private static final Pattern P = Pattern.compile("[\n\t]");
	
	public static List<String> convertToList(String string) {
		List<String> list = new ArrayList<String>();
		if (string==null){
			return list;
		}
		String transformed = P.matcher(string).replaceAll(" ");
		String[] lines = transformed.split(" ");
		for (String line: lines){
			if (!line.isEmpty()){
				list.add(line);
			}
		}
		return list;
	}

}
