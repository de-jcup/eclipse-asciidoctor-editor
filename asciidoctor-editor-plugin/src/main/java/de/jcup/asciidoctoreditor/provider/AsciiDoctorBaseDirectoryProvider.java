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

	private File findBaseDir(File dir) {
		if (cachedBaseDir == null) {
			cachedBaseDir = findBaseDirNotCached(dir);
		}
		return cachedBaseDir;
	}

	private File findBaseDirNotCached(File dir) {
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
		if (context.asciidocFile == null) {
			return new File(".");
		}
		return findBaseDir(context.asciidocFile.getParentFile());
	}

	public void reset() {
		cachedBaseDir = null;
	}
}
