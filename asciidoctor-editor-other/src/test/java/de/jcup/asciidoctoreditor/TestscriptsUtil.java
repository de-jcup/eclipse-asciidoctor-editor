package de.jcup.asciidoctoreditor;

import static org.junit.Assert.*;

import java.io.File;

public class TestscriptsUtil {

	static File testscriptFolder;
	
	static{
		testscriptFolder=new File("./testscripts/");
		if (!testscriptFolder.exists()){
			testscriptFolder=new File("./asciidoctor-editor-other/testscripts/");
		}
		if (!testscriptFolder.exists()){
			throw new IllegalStateException("testscripts folder not found");
		}
	}

	public static File assertFileInTestscripts(String relativePath){
		File file = new File(testscriptFolder,relativePath);
		if (!file.exists()){
			fail("file does not exist in testscripts folder:"+file);
		}
		return file;
	}
}
