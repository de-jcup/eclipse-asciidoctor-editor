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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class AsciiDocFileUtils {

	public static File createTempFileForConvertedContent(long tempId, String filename) throws IOException {
		File newTempSubFolder = createSelfDeletingTempSubFolder(tempId, "asciidoctor-editor-temp");

		File newTempFile = new File(newTempSubFolder, filename);
		if (newTempFile.exists()) {
			if (!newTempFile.delete()) {
				throw new IOException("Unable to delete old tempfile:" + newTempFile);
			}
		}
		newTempFile.deleteOnExit();
		return newTempFile;
	}

	/**
	 * Any IO problem will throw an {@link IllegalStateException}
	 * @param tempId
	 * @return path, never <code>null</code>
	 */
	public static Path createTempFolderForEditor(long tempId) {
		try {
			File newTempSubFolder = createSelfDeletingTempSubFolder(tempId, "asciidoctor-editor-gen");
			return newTempSubFolder.toPath();
		} catch (IOException e) {
			throw new IllegalStateException("Not able to create temp folder for editor", e);
		}
	}

	protected static File createSelfDeletingTempSubFolder(long tempId, String child) throws IOException {
		String tempDir = System.getProperty("java.io.tmpdir");
		File newTempFolder = new File(tempDir, child);

		if (!newTempFolder.exists() && !newTempFolder.mkdirs()) {
			throw new IOException("Was not able to create folder:" + newTempFolder);
		}
		newTempFolder.deleteOnExit();
		
		File newTempSubFolder = new File(newTempFolder, "editor_" + tempId);
		if (!newTempSubFolder.exists() && !newTempSubFolder.mkdirs()) {
			throw new IOException("not able to create temp folder:" + newTempSubFolder);
		}
		newTempSubFolder.deleteOnExit();
		return newTempSubFolder;
	}
	
}
