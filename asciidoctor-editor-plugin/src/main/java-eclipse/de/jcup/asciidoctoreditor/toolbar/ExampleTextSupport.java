/*
 * Copyright 2021 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.toolbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;
import de.jcup.eclipse.commons.resource.EclipseResourceInputStreamProvider;
import de.jcup.eclipse.commons.resource.ResourceInputStreamProvider;

public class ExampleTextSupport {
	private static final ExampleTextSupport INSTANCE = new ExampleTextSupport();
	
	public static String getExampleText(String path){
		return INSTANCE.get(path);
	}
	private Map<String, String> cache = new TreeMap<>();
	private ResourceInputStreamProvider resourceInputStreamProvider;

	ExampleTextSupport(){
	    resourceInputStreamProvider = new EclipseResourceInputStreamProvider(AsciiDoctorEditorActivator.PLUGIN_ID);
	}
	
	public static void setInputStreamProvider(ResourceInputStreamProvider resourceInputStreamProvider) {
		INSTANCE.setResourceInputStreamProvider(resourceInputStreamProvider);
	}
		
	
	public void setResourceInputStreamProvider(ResourceInputStreamProvider resourceInputStreamProvider) {
		this.resourceInputStreamProvider = resourceInputStreamProvider;
	}
	
	public String get(String path) {
		if (path == null) {
			return "";
		}
		String text = cache.get(path);

		if (text == null) {
			text = load(path);
			cache.put(path, text);
		}
		return text;
	}

	/**
	 * Load tool tip
	 * 
	 * @param path
	 * @return tool tip string - never <code>null</code>
	 */
	String load(String path) {
		if (path==null){
			throw new IllegalArgumentException("id may not be null");
		}
		String loaded = loadFrom(path);
		if (loaded!=null){
			/* text variant is kept as is */
			return loaded;
		}
		return ""; /* fall back */
	}

	private String loadFrom(String path) {
	
		try (InputStream inputStream = getInputStream(path)) {
			return loadFromStreamAsUTF_8(inputStream);
		} catch (IOException e) {
			/* should not happen - but if there are errors
			 * we just return an empty string
			 */
			return null;
		}
	}

	private InputStream getInputStream(String path) throws IOException {
		if (resourceInputStreamProvider==null){
			return null;
		}
		return resourceInputStreamProvider.getStreamFor(path);
	}

	private String loadFromStreamAsUTF_8(InputStream stream) throws IOException {
		if (stream == null) {
			return null;
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
		StringBuilder sb = new StringBuilder();
		
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
			sb.append("\n");
		}
		return sb.toString();
	}
}
