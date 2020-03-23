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

public enum AsciiDoctorPlantUMLDocumentIdentifiers implements AsciiDoctorDocumentIdentifier {
	
	PLANTUML_NOTE,
	
	PLANTUML_PREPROCESSOR,
	
	PLANTUML_KEYWORD,
	
	PLANTUML_COLOR,
	
	PLANTUML_SKINPARAMETER,
	
	PLANTUML_TYPE,
	
	PLANTUML_DOUBLE_STRING,
	
	PLANTUML_COMMENT,

	PLANTUML_DIVIDER,
	
	PLANTUML_ARROW,

	PLANTUML_LABEL,
	;


	@Override
	public String getId() {
		return name();
	}
	public static String[] allIdsToStringArray(){
		return allIdsToStringArray(null);
	}
	public static String[] allIdsToStringArray(String additionalDefaultId){
		AsciiDoctorPlantUMLDocumentIdentifiers[] values = values();
		int size = values.length;
		if (additionalDefaultId!=null){
			size+=1;
		}
		String[] data = new String[size];
		int pos=0;
		if (additionalDefaultId!=null){
			data[pos++]=additionalDefaultId;
		}
		for (AsciiDoctorPlantUMLDocumentIdentifiers d: values){
			data[pos++]=d.getId();
		}
		return data;
	}

}
