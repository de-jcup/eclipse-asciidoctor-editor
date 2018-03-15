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

public class AsciiDoctorHeadline {

	String name;
	int position;
	int lengthToNameEnd;
	public int end;
	private int deep;
	
	public AsciiDoctorHeadline(int deep, String name){
		this.deep=deep;
		this.name=name;
	}

	public int getLengthToNameEnd() {
		return lengthToNameEnd;
	}
	
	public int getDeep() {
		return deep;
	}
	
	public String getName() {
		return name;
	}

	public int getPosition() {
		return position;
	}
	
	public int getEnd() {
		return end;
	}
	
	@Override
	public String toString() {
		return "function "+name+"()";
	}

}
