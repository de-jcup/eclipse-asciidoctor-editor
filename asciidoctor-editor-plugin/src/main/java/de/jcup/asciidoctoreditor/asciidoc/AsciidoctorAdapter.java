/*
 * Copyright 2019 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.asciidoc;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import de.jcup.asp.api.asciidoc.AsciidocAttributes;
import de.jcup.asp.api.asciidoc.AsciidocOptions;
import de.jcup.asp.client.AspClientProgressMonitor;

/**
 * Implementations convert the given Asciidoc file to target format via Asciidoctor (directly or via ASP server)
 *
 */
public interface AsciidoctorAdapter {

    /**
     * Converts given file with asciidoc
     * 
     * @param editorFileOrNull - the file name is used to show up in console
     * @param asciiDocFile     - the real asciidoc file to render. If not
     *                         processed/prepared/generated, the asciidoc file is
     *                         the same as the editor file
     * @param options
     * @param attributes
     * @param monitor
     */
    void convertFile(File editorFileOrNull, File asciiDocFile, AsciidocOptions options, AsciidocAttributes attributes, AspClientProgressMonitor monitor);

    default public Map<String, Object> resolveAttributes(File fileOrDirectory) {
        if (fileOrDirectory == null) {
            return Collections.emptyMap();
        }
        return AsciiDocAttributeResolver.DEFAULT.resolveAttributes(fileOrDirectory);

    }

}
