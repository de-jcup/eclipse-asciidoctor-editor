package de.jcup.asciidoctoreditor;

/**
 * An access class for system parts - has no static methods, means access can be
 * mocked in tests...
 *
 */
public class SystemAccess {

    public String getProperty(String key) {
        return System.getProperty(key);
    }
}
