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
package de.jcup.asciidoctoreditor.script.parser.validator;

public enum AsciiDoctorEditorValidationErrorLevel {

	INFO("info"),
	
	WARNING("warning"), 

	ERROR("error"), 
	;
	

	private String id;

	private AsciiDoctorEditorValidationErrorLevel(String id) {
		if (id == null) {
			throw new IllegalArgumentException("id may not be null!");
		}
		this.id = id;
	}

	public String getId() {
		return id;
	}


	/**
	 * @param id
	 * @return error level, never <code>null</code>. If not identifiable by id the default will be returned: {@link AsciiDoctorEditorValidationErrorLevel#ERROR}
	 */
	public static AsciiDoctorEditorValidationErrorLevel fromId(String id) {
		if (WARNING.getId().equals(id)) {
			return WARNING;
		}
		if (INFO.getId().equals(id)) {
			return INFO;
		}
		return ERROR;
	}
}
