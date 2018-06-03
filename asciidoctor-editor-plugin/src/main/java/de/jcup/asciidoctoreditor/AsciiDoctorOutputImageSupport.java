package de.jcup.asciidoctoreditor;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class AsciiDoctorOutputImageSupport implements AsciiDoctorSupport {

	private String cachedSourceImagesPath;
	private AsciiDoctorSupportContext context;

	AsciiDoctorOutputImageSupport(AsciiDoctorSupportContext context) {
		this.context = context;
		context.imageSupport = this;
	}

	private void copyImagesToOutputFolder(String sourcePath, File target) {
		
		File cachedImagesFile = new File(sourcePath);
		if (!cachedImagesFile.exists()) {
			return;
		}
		try {
			FileUtils.copyDirectory(cachedImagesFile, target);
		} catch (IOException e) {
			context.logAdapter.logError("Cannot copy images", e);
		}

	}

	public void ensureImages() {
		File targetImagesDir = new File(context.outputFolder.toFile(), "images");
		if (!targetImagesDir.exists()) {
			targetImagesDir.mkdirs();
			targetImagesDir.deleteOnExit();
		}
		if (cachedSourceImagesPath == null) {
			cachedSourceImagesPath = resolveImagesDirPath(context.baseDir);
		}
		copyImagesToOutputFolder(cachedSourceImagesPath, targetImagesDir);

	}

	protected String resolveImagesDirPath(File baseDir) {

		Object imagesDir = context.attributesSupport.getCachedAttributes().get("imagesdir");

		String imagesDirPath = null;
		if (imagesDir != null) {
			imagesDirPath = imagesDir.toString();
			if (imagesDirPath.startsWith("./")) {
				File imagePathNew = new File(baseDir, imagesDirPath.substring(2));
				imagesDirPath = imagePathNew.getAbsolutePath();
			}
		} else {
			imagesDirPath = baseDir.getAbsolutePath();
		}
		return imagesDirPath;
	}

}
