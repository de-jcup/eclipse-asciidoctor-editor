package de.jcup.asciidoctoreditor.asciidoc;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;

public interface PDFSupport {

    File getTargetPDFFileOrNull();

    void convertPDF(ConversionData data, IProgressMonitor monitor) throws Exception;

}
