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
package de.jcup.asciidoctoreditor.provider;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.asciidoctor.AsciiDocDirectoryWalker;
import org.asciidoctor.DirectoryWalker;
import org.asciidoctor.ast.DocumentHeader;

public class AsciiDoctorAttributesProvider {
	
	private Map<String, Object> cachedAttributes;
	private AsciiDoctorProviderContext context;

	AsciiDoctorAttributesProvider(AsciiDoctorProviderContext context){
		if (context==null ){
			throw new IllegalArgumentException("context may never be null!");
		}
		this.context=context;
	}

	protected Map<String, Object> getCachedAttributes() {
		if (cachedAttributes == null) {
			cachedAttributes = resolveAttributes(context.getBaseDir());
		}
		return cachedAttributes;
	}

	protected Map<String, Object> resolveAttributes(File baseDir) {
	    context.getLogAdapter().resetTimeDiff();
		Map<String, Object> map = new HashMap<>();
		Set<DocumentHeader> documentIndex = new HashSet<DocumentHeader>();
		DirectoryWalker directoryWalker = new AsciiDocDirectoryWalker(baseDir.getAbsolutePath());

		for (File file : directoryWalker.scan()) {
			documentIndex.add(context.getAsciiDoctor().readDocumentHeader(file));
		}
		for (DocumentHeader header : documentIndex) {
			map.putAll(header.getAttributes());
		}
		context.getLogAdapter().logTimeDiff("resolved attributes from base dir:"+baseDir);
		return map;
	}

	public void reset() {
		cachedAttributes=null;
	}

}
