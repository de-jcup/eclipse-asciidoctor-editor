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
 package de.jcup.asciidoctoreditor.script;

public class AsciiDoctorInclude {

	String label;
	int position;
	int lengthToNameEnd;
	public int end;
	private String fullExpression;
	
	public AsciiDoctorInclude(String fullExpression, String target, int position, int end, int lengthTonNameEnd){
		this.label=target;
		this.fullExpression=fullExpression;
		this.position=position;
		this.end=end;
		this.lengthToNameEnd=lengthTonNameEnd;
	}

	public int getLengthToNameEnd() {
		return lengthToNameEnd;
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
	
	@Override
	public String toString() {
		return "include:"+label+"[pos:"+position+",end:"+end+",lengthToNameEnd:"+lengthToNameEnd+"]";
	}

	public String getFullExpression() {
		return fullExpression;
	}

}
