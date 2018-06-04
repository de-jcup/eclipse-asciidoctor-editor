package de.jcup.asciidoctoreditor.provider;

import java.io.File;
import java.nio.file.Path;

import org.asciidoctor.Asciidoctor;

import de.jcup.asciidoctoreditor.LogAdapter;

public class AsciiDoctorProviderContext {
	LogAdapter logAdapter;
	File asciidocFile;
	File baseDir;
	Path outputFolder;
	boolean tocVisible;

	AsciiDoctorBaseDirectoryProvider baseDirSupport;
	AsciiDoctorImageProvider imageSupport;
	AsciiDoctorAttributesProvider attributesSupport;
	Asciidoctor asciidoctor;
	AsciiDoctorOptionsProvider optionsSupport;
	File targetImagesDir;

	public AsciiDoctorProviderContext(Asciidoctor asciidoctor) {
		if (asciidoctor==null ){
			throw new IllegalArgumentException("asciidoctor may never be null!");
		}
		this.asciidoctor=asciidoctor;
		init();
	}

	public AsciiDoctorBaseDirectoryProvider getBaseDirSupport() {
		return baseDirSupport;
	}
	
	public AsciiDoctorImageProvider getImageSupport() {
		return imageSupport;
	}
	
	public AsciiDoctorAttributesProvider getAttributesSupport() {
		return attributesSupport;
	}
	
	
	public AsciiDoctorOptionsProvider getOptionsSupport() {
		return optionsSupport;
	}
	
	public void setAsciidocFile(File asciidocFile) {
		this.asciidocFile = asciidocFile;
		this.baseDir = baseDirSupport.findBaseDir();
	}

	protected void init() {
		attributesSupport = new AsciiDoctorAttributesProvider(this);
		imageSupport = new AsciiDoctorImageProvider(this);
		optionsSupport = new AsciiDoctorOptionsProvider(this);
		baseDirSupport = new AsciiDoctorBaseDirectoryProvider(this);
	}

	public void setOutputFolder(Path outputFolder) {
		this.outputFolder = outputFolder;
	}

	public void reset() {
		this.baseDir = null;
		this.outputFolder = null;
		this.asciidocFile = null;
		
		this.attributesSupport.reset();
		this.optionsSupport.reset();
		this.imageSupport.reset();
	}

	public Asciidoctor getAsciiDoctor() {
		return asciidoctor;
	}

}
