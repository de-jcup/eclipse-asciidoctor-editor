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

public class AsciiDoctorInlineAnchor {

	private int end;
	private int position;
	private String label;
	private String id;

	public AsciiDoctorInlineAnchor(String text, int position, int end) {
		this.label = text;
		this.end = end;
		this.position = position;

		this.id = createIDByLabel();
	}

	private String createIDByLabel() {
		if (label == null) {
			return null;
		}
		if (label.startsWith("[[")){
			if (! label.endsWith("]]")){
				return "illegal-noend-" + System.nanoTime(); 
			}
			return label.substring(2, label.length() - 2);
		}else if (label.startsWith("[#")){
			if ( label.endsWith("]]")){
				return "illegal-noend-" + System.nanoTime(); 
			}
			if (!label.endsWith("]")){
				return "illegal-noend-" + System.nanoTime(); 
			}
			return label.substring(2, label.length() - 1);
		}
		return "illegal-nostart-" + System.nanoTime(); 
	}

	public String getLabel() {
		return label;
	}

	public int getPosition() {
		return position;
	}

	public int getEnd() {
		return end;
	}

	public String getId() {
		return id;
	}

}
