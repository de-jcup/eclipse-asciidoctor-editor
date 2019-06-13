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
package de.jcup.asciidoctoreditor.script;

public class AsciiDoctorErrorBuilder {

	private static final String ADOC_MARKER = ".adoc:";

	public AsciiDoctorMarker build(String originMessage){
		int start=-1;
		int end=-1;
		String message = null;
		if (originMessage==null){
			message="Unknown failure";
		}else{
			message = handleNotNullMessages(originMessage);
		}
		/* fall back to origin message */
		if (message==null){
			message = originMessage;
		}
		AsciiDoctorMarker error = new AsciiDoctorMarker(start, end, message.trim());
		return error;
	}

	protected String handleNotNullMessages(String originMessage) {
		int indexOfAdocMarker = originMessage.indexOf(ADOC_MARKER);
		if (indexOfAdocMarker!=-1){
			return originMessage.substring(indexOfAdocMarker+ADOC_MARKER.length());
		}
		return null;
	}
}
