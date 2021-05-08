/*
 * Copyright 2021 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
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
