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
 package de.jcup.asciidoctoreditor.document;

public enum AsciiDoctorDocumentIdentifiers implements AsciiDoctorDocumentIdentifier {
	
	COMMENT,
	
	HYPERLINK,
	
	TEXT_BLOCK,
	
	INCLUDE_KEYWORD,
	
	ASCIIDOCTOR_COMMAND,
	
	KNOWN_VARIABLES,
	
	VARIABLES,
	
	TEXT_BOLD,
	
	TEXT_ITALIC,

	TEXT_MONOSPACED,
	
	HEADLINE,
	
	
	;


	@Override
	public String getId() {
		return name();
	}
	public static String[] allIdsToStringArray(){
		return internal_allIdsToStringArray();
	}
	public static String[] allIdsToStringArray(String ...additionalDefaultIds){
	    return internal_allIdsToStringArray(additionalDefaultIds);
	}
	
	private static String[] internal_allIdsToStringArray(String ...additionalDefaultIds){
		AsciiDoctorDocumentIdentifiers[] values = values();
		int size = values.length;
			size+=additionalDefaultIds.length;
		String[] data = new String[size];
		int pos=0;
		for (String additionalDefaultId: additionalDefaultIds) {
		    if (additionalDefaultId!=null){
		        data[pos++]=additionalDefaultId;
		    }
		}
		for (AsciiDoctorDocumentIdentifiers d: values){
			data[pos++]=d.getId();
		}
		return data;
	}
	
	public static boolean isContaining(String contentType) {
		if (contentType==null){
			return false;
		}
		for (AsciiDoctorDocumentIdentifiers identifier: values()){
			if (identifier.getId().equals(contentType)){
				return true;
			}
		}
		
		return false;
	}

}
