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
package de.jcup.asciidoctoreditor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CLITextUtil {
	private static final Pattern P = Pattern.compile("[\n\r\t]");
	
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
