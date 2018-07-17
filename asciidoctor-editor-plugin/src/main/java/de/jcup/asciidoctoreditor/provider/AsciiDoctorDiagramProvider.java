package de.jcup.asciidoctoreditor.provider;

import java.io.File;

public class AsciiDoctorDiagramProvider {

	private AsciiDoctorProviderContext context;

	public AsciiDoctorDiagramProvider(AsciiDoctorProviderContext context) {
		if (context==null ){
			throw new IllegalArgumentException("context may never be null!");
		}
		this.context = context;
	}

	public File getDiagramRootDirectory() {
		return context.getBaseDir();
	}

}
