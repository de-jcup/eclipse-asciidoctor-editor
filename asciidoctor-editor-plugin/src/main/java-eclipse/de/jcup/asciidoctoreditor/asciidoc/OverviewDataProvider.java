package de.jcup.asciidoctoreditor.asciidoc;

import java.io.File;

public interface OverviewDataProvider {

    File getTempGenFolder();

    File getBaseDir();

    String getCachedSourceImagesPath();

}
