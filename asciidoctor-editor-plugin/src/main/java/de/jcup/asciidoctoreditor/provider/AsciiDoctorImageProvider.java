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
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;

public class AsciiDoctorImageProvider {
	private static ImageFilesFilter IMAGE_FILTER = new ImageFilesFilter();
	private String cachedSourceImagesPath;
	private AsciiDoctorProviderContext context;

	AsciiDoctorImageProvider(AsciiDoctorProviderContext context) {
		if (context==null ){
			throw new IllegalArgumentException("context may never be null!");
		}
		this.context = context;
		context.imageProvider = this;
	}

	private void copyImagesToOutputFolder(String sourcePath, File target) {
		
		File cachedImagesFile = new File(sourcePath);
		if (!cachedImagesFile.exists()) {
			return;
		}
		try {
			FileUtils.copyDirectory(cachedImagesFile, target, IMAGE_FILTER);
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
		context.targetImagesDir=targetImagesDir;

	}
	
	private static class ImageFilesFilter implements FileFilter{

		@Override
		public boolean accept(File file) {
			if (file==null || ! file.exists()){
				return false;
			}
			if (file.isDirectory()){
				return true;
			}
			String ext = FilenameUtils.getExtension(file.getName());
			if (ext==null || ext.isEmpty()){
				return false;
			}
			String e = ext.toLowerCase();
			if ("png".equals(e)){
				return true;
			}
			if ("jpg".equals(e)){
				return true;
			}
			if ("gif".equals(e)){
				return true;
			}
			if ("svg".equals(e)){
				return true;
			}
			if ("bmp".equals(e)){
				return true;
			}
			if ("tiff".equals(e)){
				return true;
			}
			return false;
		}
		
	}

	protected String resolveImagesDirPath(File baseDir) {

		Object imagesDir = context.attributesProvider.getCachedAttributes().get("imagesdir");

		String imagesDirPath = null;
		if (imagesDir != null) {
			imagesDirPath = imagesDir.toString();
			if (imagesDirPath.startsWith("./")) {
				File imagePathNew = new File(baseDir, imagesDirPath.substring(2));
				imagesDirPath = imagePathNew.getAbsolutePath();
			}
		} else {
			/* fallback when not defined - as defined at https://asciidoctor.org/docs/asciidoctor-pdf/#image-paths*/
			imagesDirPath = baseDir.getAbsolutePath();
		}
		return imagesDirPath;
	}

	public void reset() {
		this.cachedSourceImagesPath=null;
	}

}
