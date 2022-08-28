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
package de.jcup.asciidoctoreditor.asciidoc;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class AsciidoctorConfigFile {

    private String content;
    private String asciidoctorconfigdir;
    private Path location;

    public AsciidoctorConfigFile(String content, Path location) {
        if (location == null) {
            throw new IllegalArgumentException("location amy not be null");
        }
        if (content == null) {
            throw new IllegalArgumentException("content amy not be null");
        }
        this.content = content;
        this.location = location;

        this.asciidoctorconfigdir = location.getParent().toAbsolutePath().toString();

    }

    public Path getLocation() {
        return location;
    }

    public String getAsciidoctorconfigdir() {
        return asciidoctorconfigdir;
    }

    public String getContent() {
        return content;
    }

    public String getContentCustomized() {
        return ":asciidoctorconfigdir: " + asciidoctorconfigdir + "\n" + content;
    }

    public Map<String, String> toContentCustomizedMap() {
        Map<String, String> map = new LinkedHashMap<>();
        String[] lines = getContentCustomized().split("\n");
        for (String line : lines) {
            if (!line.startsWith(":")) {
                continue;
            }
            String[] splitted = line.split(":");
            if (splitted.length < 3) {
                continue;
            }
            String key = splitted[1];
            String value = splitted[2];
            map.put(key.trim(), value.trim());
        }

        return map;
    }

}
