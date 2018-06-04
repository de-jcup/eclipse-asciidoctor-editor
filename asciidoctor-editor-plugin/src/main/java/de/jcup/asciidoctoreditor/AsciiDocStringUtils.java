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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class AsciiDocStringUtils {

	private static final String UTF_8 = "UTF-8";

	public static String resolveFilenameOfIncludeOrDiagram(String potentialInclude) {
		String fileName = resolveFilenameOfIncludeOrNull(potentialInclude);
		if (fileName != null) {
			return fileName;
		}
		return resolveFilenameOfDiagramMacroOrNull(potentialInclude);
	}

	public static String resolveFilenameOfDiagramMacroOrNull(String potentialInclude) {
		if (potentialInclude == null) {
			return null;
		}
		String fileName = resolveFilenameOfMacroOrNull(potentialInclude, "ditaa");
		if (fileName != null) {
			return fileName;
		}
		return resolveFilenameOfMacroOrNull(potentialInclude, "plantuml");
	}

	public static String resolveFilenameOfMacroOrNull(String potentialInclude, String macroName) {
		if (potentialInclude == null) {
			return null;
		}
		String prefix = macroName + "::";
		if (potentialInclude.startsWith(prefix)) {
			int index = potentialInclude.indexOf("[");
			if (index == -1) {
				return null;
			}
			String fileName = potentialInclude.substring(0, index);
			fileName = fileName.substring(prefix.length());
			return fileName;
		}
		return null;
	}

	/**
	 * Resolves filenames from fullstrings of an potential include.<br>
	 * <br>
	 * Example:<br>
	 * <code>include::src/xyz/filenamexyz.adoc[]</code><br>
	 * will be resolved to <br>
	 * <code>src/xyz/filenamexyz.adoc</code>
	 * 
	 * @param potentialInclude
	 * @return resolved filename of include or <code>null</code>
	 */
	public static String resolveFilenameOfIncludeOrNull(String potentialInclude) {
		if (potentialInclude == null) {
			return null;
		}
		if (potentialInclude.startsWith("include::")) {
			if (potentialInclude.endsWith(".adoc[]")) {
				String fileName = potentialInclude.substring("include::".length());
				fileName = fileName.substring(0, fileName.length() - 2);
				return fileName;
			}
		}
		return null;
	}

	public static class LinkTextData {
		LinkTextData() {

		}

		public String text;
		public int offsetLeft;
	}

	public static LinkTextData resolveLinkTextForIncludeOrHeadline(String line, int offset, int offsetInLine) {
		String leftChars = line.substring(0, offsetInLine);
		String rightChars = line.substring(offsetInLine);
		StringBuilder sb = new StringBuilder();
		int offsetLeft = offset;
		char[] left = leftChars.toCharArray();
		for (int i = left.length - 1; i >= 0; i--) {
			char c = left[i];
			if (Character.isWhitespace(c)) {
				break;
			}
			offsetLeft--;
			sb.insert(0, c);
		}
		for (char c : rightChars.toCharArray()) {
			if (Character.isWhitespace(c)) {
				break;
			}
			sb.append(c);
		}
		LinkTextData data = new LinkTextData();
		data.text = sb.toString();
		data.offsetLeft = offsetLeft;
		return data;
	}

	public static String readUTF8FileToString(File fileToRead) throws IOException {
		String originText = null;
		StringBuilder sb = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileToRead),UTF_8))) {
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			br.close();
			originText = sb.toString();
		}
		return originText;
	}

	public static File writeTextToUTF8File(String transformed, File newTempFile) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newTempFile),UTF_8))) {
			bw.write(transformed);
			bw.close();
			return newTempFile;
		}
	}
}
