package de.jcup.asciidoctoreditor.asciidoc;

import java.io.File;
import java.nio.file.Path;

import org.eclipse.core.runtime.IProgressMonitor;

import de.jcup.asciidoctoreditor.TemporaryFileType;
import de.jcup.asciidoctoreditor.UniqueIdProvider;

public interface PreviewSupport {

    void convert(ConversionData conversionData, AsciiDoctorBackendType backend, IProgressMonitor monitor) throws Exception;

    File getTempFileFor(File fileToConvertIntoHTML, UniqueIdProvider editorId, TemporaryFileType origin);

    String enrichHTML(String asciiDocHtml, int refreshAutomaticallyInSeconds);

    Path getProjectTempFolder();

    File getBaseDir();

    File getFileToRender();

}
