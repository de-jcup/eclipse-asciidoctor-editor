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
 package de.jcup.asciidoctoreditor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TestScriptLoader {
	private static File testScriptRootFolder = new File("./asciidoctoreditor-other/testscripts");
	static{
		if (!testScriptRootFolder.exists()){
			// workaround for difference between eclipse test and gradle execution (being in root folder...)
			testScriptRootFolder = new File("./../asciidoctoreditor-other/testscripts");
		}
	}
	
	public static List<String> fetchAllTestScriptNames() {
		assertTestscriptFolderExists();
		List<String> list = new ArrayList<>();
		for (File file: testScriptRootFolder.listFiles()){
			list.add(file.getName());
		}
		return list;
	}
	
	public static String loadScriptFromTestScripts(String testScriptName) throws IOException{
		assertTestscriptFolderExists();
		
		File file = new File(testScriptRootFolder,testScriptName);
		if (!file.exists()){
			throw new IllegalArgumentException("Test case corrupt! Test script file does not exist:"+file);
		}
		StringBuilder sb = new StringBuilder();
		try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))){
			String line = null;
			while ((line=br.readLine())!=null){
				sb.append(line);
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	private static void assertTestscriptFolderExists() {
		if (!testScriptRootFolder.exists()){
			throw new IllegalArgumentException("Test setup corrupt! Root folder of test scripts not found:"+testScriptRootFolder);
		}
	}
}
