package de.jcup.asciidoctoreditor.preview;

import java.net.URI;
import java.nio.file.Path;
import java.util.regex.Pattern;

import de.jcup.asciidoctoreditor.asciidoc.AsciiDoctorWrapper;

public class AbsolutePathPatternFactory {

    public Pattern createRemoveAbsolutePathToTempFolderPattern(AsciiDoctorWrapper wrapper) {
        Path folder = wrapper.getTempFolder();
        return createRemoveAbsolutePathPattern(folder);
    }
    
    public Pattern createRemoveAbsolutePathToBaseFolderPattern(AsciiDoctorWrapper wrapper) {
        Path folder = wrapper.getContext().getBaseDir().toPath();
        return createRemoveAbsolutePathPattern(folder);
    }

    private Pattern createRemoveAbsolutePathPattern(Path folder) {
        // Convert to URI as asciidoc convert file path to URI in html document.
        // So if the path contains a space or a special character it will be percent
        // encoded
        URI absolutePathToTempFolder = folder.toFile().toURI();
        String path = absolutePathToTempFolder.getRawPath();
        if (isWindowsOS() && path.startsWith("/")) {
            path = path.substring(1);
        }
        return Pattern.compile(Pattern.quote(path));
    }
    
    private boolean isWindowsOS() {
        String osName = System.getProperty("os.name");
        return osName != null && osName.toLowerCase().contains("windows");
    }
}
