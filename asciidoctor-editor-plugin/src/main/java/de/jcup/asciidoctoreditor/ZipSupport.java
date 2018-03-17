package de.jcup.asciidoctoreditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipSupport {

	public void unzip(File zipFile, File targetFolder) throws IOException {
		if (zipFile == null){
			throw new IllegalArgumentException("zipfile may not be null");
		}
		
		if (targetFolder == null){
			throw new IllegalArgumentException("target folder may not be null");
		}
		if (!zipFile.exists()) {
			throw new FileNotFoundException("file not found:" + zipFile.getAbsolutePath());
		}
		if (!targetFolder.exists()) {
			targetFolder.mkdirs();
		}
		if (! targetFolder.isDirectory()){
			throw new IOException("Target is not a directory:"+targetFolder.getAbsolutePath());
		}
		ZipEntry zipEntry = null;
		String fileName = null;
		File newFile=null;
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
			byte[] buffer = new byte[1024];
			zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				if (! zipEntry.isDirectory()){
					fileName = zipEntry.getName();
					newFile = new File(targetFolder, fileName);
					File parentFile = newFile.getParentFile();
					if (!parentFile.exists()){
						if (!parentFile.mkdirs()){
							throw new IOException("Was not able to create directory:"+parentFile.getAbsolutePath());
						}
					}
					if (newFile.exists()){
						newFile.delete();
					}
					newFile.createNewFile();
					try(FileOutputStream fos = new FileOutputStream(newFile)){
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
						fos.close();
					}
				}
				
				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
		}catch(IOException e){
			throw new IOException("Problems on zipentry:"+zipEntry+" - new file:"+newFile,e);
		}
	}
}
