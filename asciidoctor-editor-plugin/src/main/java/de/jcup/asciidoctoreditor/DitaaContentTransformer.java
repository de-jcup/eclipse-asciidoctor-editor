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

public class DitaaContentTransformer implements ContentTransformer{

	@Override
	public String transform(String origin) {
		StringBuilder sb = new StringBuilder();
		if (origin!=null){
			sb.append("[ditaa]\n----\n");
			sb.append(origin);
			sb.append("\n----\n");
		}
		return sb.toString();
	}

	@Override
	public boolean isTransforming(Object data) {
		return true;
	}

}
