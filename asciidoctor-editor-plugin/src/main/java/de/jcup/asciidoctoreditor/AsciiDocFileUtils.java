package de.jcup.asciidoctoreditor;

import java.io.File;
import java.io.IOException;

public class AsciiDocFileUtils {

	public static File createTempFileForConvertedContent(long tempId, String filename) throws IOException {
		String tempDir = System.getProperty("java.io.tmpdir");
		File newTempFolder = new File(tempDir, "asciidoctor-editor-temp");

		if (!newTempFolder.exists() && !newTempFolder.mkdirs()) {
			throw new IOException("Was not able to create tempfolder:" + newTempFolder);
		}
		
		File newTempSubFolder = new File(newTempFolder, "editor_"+tempId);
		if (!newTempSubFolder.exists() && !newTempSubFolder.mkdirs()) {
			throw new IOException("not able to create temp folder:"+newTempSubFolder);
		}
		

		File newTempFile = new File(newTempSubFolder, filename);
		if (newTempFile.exists()){
			if (!newTempFile.delete()){
				throw new IOException("Unable to delete old tempfile:"+newTempFile);
			}
		}
		newTempFile.deleteOnExit();
		return newTempFile;
	}
}
