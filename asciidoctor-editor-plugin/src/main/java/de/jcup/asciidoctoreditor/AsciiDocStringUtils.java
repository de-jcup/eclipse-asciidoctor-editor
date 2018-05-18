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

public class AsciiDocStringUtils {

	public static String resolveFilenameOfIncludeOrDiagram(String potentialInclude) {
		String fileName = resolveFilenameOfIncludeOrNull(potentialInclude);
		if (fileName!=null){
			return fileName;
		}
		return resolveFilenameOfDiagramMacroOrNull(potentialInclude);
	}
	
	public static String resolveFilenameOfDiagramMacroOrNull(String potentialInclude) {
		if (potentialInclude==null){
			return null;
		}
		String fileName = resolveFilenameOfMacroOrNull(potentialInclude, "ditaa");
		if (fileName!=null){
			return fileName;
		}
		return resolveFilenameOfMacroOrNull(potentialInclude, "plantuml");
	}
	
	public static String resolveFilenameOfMacroOrNull(String potentialInclude, String macroName) {
		if (potentialInclude==null){
			return null;
		}
		String prefix = macroName+"::";
		if (potentialInclude.startsWith(prefix)){
			int index = potentialInclude.indexOf("[");
			if (index==-1){
				return null;
			}
			String fileName = potentialInclude.substring(0,index);
			fileName= fileName.substring(prefix.length());
			return fileName;
		}
		return null;
	}
	
	/**
	 * Resolves filenames from fullstrings of an potential include.<br><br>
	 * Example:<br>
	 * <code>include::src/xyz/filenamexyz.adoc[]</code><br>
	 * will be resolved to <br>
	 * <code>src/xyz/filenamexyz.adoc</code>
	 * @param potentialInclude
	 * @return resolved filename of include or <code>null</code>
	 */
	public static String resolveFilenameOfIncludeOrNull(String potentialInclude) {
		if (potentialInclude==null){
			return null;
		}
		if (potentialInclude.startsWith("include::")){
			if (potentialInclude.endsWith(".adoc[]")){
				String fileName = potentialInclude.substring("include::".length());
				fileName=fileName.substring(0,fileName.length()-2);
				return fileName;
			}
		}
		return null;
	}
}
