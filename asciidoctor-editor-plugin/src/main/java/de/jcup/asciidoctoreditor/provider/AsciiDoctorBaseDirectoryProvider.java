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
import java.io.FileFilter;

public class AsciiDoctorBaseDirectoryProvider {
	private static FileFilter ADOC_FILE_FILTER = new ADocFilter();
	private AsciiDoctorProviderContext context;

	AsciiDoctorBaseDirectoryProvider(AsciiDoctorProviderContext context) {
		if (context == null) {
			throw new IllegalArgumentException("context may never be null!");
		}
		this.context = context;
	}

	private File cachedBaseDir;

	private File findBaseDir(File startFrom) {
		if (cachedBaseDir == null) {
			cachedBaseDir = findBaseDirNotCached(startFrom);
		}
		return cachedBaseDir;
	}

	private File findBaseDirNotCached(File startFrom) {
		File file = resolveUnSaveBaseDir(startFrom);
		File tempFolder = new File(System.getProperty("java.io.tmpdir"));
		if (tempFolder.equals(file)){
			/* this is a fuse - we got this situation with https://github.com/de-jcup/eclipse-asciidoctor-editor/issues/97 . It will be fixed,
			 * but preventing those situations is very important. So this exception will break cycles and is also check in a test case
			 */
			throw new IllegalStateException("Tempfolder may never be the base dir folder!");
		}
		return file;
	}

	protected File resolveUnSaveBaseDir(File dir) {
		// very simple approach just go up until no longer any asciidoc files
		// are found
		// if no longer .adoc files assume this is the end and use directory
		if (dir == null) {
			return new File(".");// should not happen but fall back...
		}
		File parentFile = dir.getParentFile();
		if (containsADocFiles(parentFile)) {
			return findBaseDir(parentFile);
		}
		return dir;
	}

	private boolean containsADocFiles(File dir) {
		if (!dir.isDirectory()) {
			return false;
		}
		File[] files = dir.listFiles(ADOC_FILE_FILTER);
		if (files.length == 0) {
			return false;
		}
		return true;
	}

	private static class ADocFilter implements FileFilter {

		@Override
		public boolean accept(File file) {
			if (file == null || !file.isFile()) {
				return false;
			}
			if (!file.getName().endsWith(".adoc")) {
				return false;
			}
			return true;
		}

	}

	public File findBaseDir() {
		File asciiDocFile = context.getAsciiDocFile();
		if (asciiDocFile == null) {
			 throw new IllegalStateException("No asciidoc file set!");
		}
		return findBaseDir(asciiDocFile.getParentFile());
	}

	public void reset() {
		cachedBaseDir = null;
	}
}
