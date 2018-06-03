package de.jcup.asciidoctoreditor;

import java.io.File;
import java.nio.file.Path;

import org.asciidoctor.Asciidoctor;

public class AsciiDoctorSupportContext {
	LogAdapter logAdapter;
	File asciidocFile;
	
	
	public void setAsciidocFile(File asciidocFile) {
		this.asciidocFile = asciidocFile;
		this.baseDir = baseDirSupport.findBaseDir();
	}
	
	File baseDir;
	Path outputFolder;
	boolean tocVisible;
	
	AsciiDoctorBaseDirectorySupport baseDirSupport;
	AsciiDoctorOutputImageSupport imageSupport;
	AsciiDoctorAttributesSupport attributesSupport;
	Asciidoctor asciidoctor;
	AsciiDoctorOptionsSupport optionsSupport;


	public void reset() {
		this.baseDir=null;
		this.outputFolder=null;
		this.asciidocFile=null;
		
	}
	
}
