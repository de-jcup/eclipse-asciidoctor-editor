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
package de.jcup.asciidoctoreditor;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A very simple html output parser - works only with asciidoc generated HTML
 * output.
 * 
 * @author albert
 *
 */
public class AsciidoctorHTMLOutputParser {

    // regular expression: <\s*img\s*src\s*=\s*\"([^\"]*)\"
    private static final Pattern pattern = Pattern.compile("<\\s*img\\s*src\\s*=\\s*\\\"([^\\\"]*)\\\"");

    /**
     * Finds all text content inside "<img src=".*"; -
     * 
     * @param html
     * @return
     */
    public Set<String> findImageSourcePathes(String html) {
        Set<String> pathes = new LinkedHashSet<>();
        if (html == null) {
            return pathes;
        }
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            pathes.add(matcher.group(1));
        }
        return pathes;
    }
}
