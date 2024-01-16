package de.jcup.asciidoctoreditor.asciidoc;

import java.nio.file.Path;

public interface RootLocationProvider {

    /**
     * Returns the root location
     * @return location, never <code>null</code>
     */
    public Path getRootLocation();
}
