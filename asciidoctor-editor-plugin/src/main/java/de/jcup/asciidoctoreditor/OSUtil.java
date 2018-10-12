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

public class OSUtil {

	private static Boolean isWindows;
	static{
		String os = System.getProperty("os.name");
		if (os==null || os.toLowerCase().indexOf("windows")!=-1){
			isWindows=Boolean.TRUE;
		}else{
			isWindows=Boolean.FALSE;
		}
	}
	public static boolean isWindows() {
		return isWindows.booleanValue();
	}
}
