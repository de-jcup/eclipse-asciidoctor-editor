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
