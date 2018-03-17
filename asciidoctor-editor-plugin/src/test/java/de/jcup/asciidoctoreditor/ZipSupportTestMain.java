package de.jcup.asciidoctoreditor;

import java.io.File;
import java.io.IOException;

public class ZipSupportTestMain {
	
	public static void main(String[] args) throws IOException {
		if (args.length!=2){
			System.err.println("usage zipfile targetFolder");
			System.exit(1);
		}
		File zipFile = new File(args[0]);
		File targetFolder = new File(args[1]);
		new ZipSupport().unzip(zipFile, targetFolder);
	}
}
