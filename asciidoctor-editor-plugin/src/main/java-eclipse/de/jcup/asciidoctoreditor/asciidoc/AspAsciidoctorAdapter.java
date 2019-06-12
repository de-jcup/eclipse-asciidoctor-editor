/*
 * Copyright 2018 Albert Tregnaghi
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
import java.util.HashMap;
import java.util.Map;

import de.jcup.asciidoctoreditor.console.AsciiDoctorConsoleUtil;
import de.jcup.asp.client.AspClient;
import de.jcup.asp.client.AspClientException;

public class AspAsciidoctorAdapter implements AsciidoctorAdapter {
    
    private AspClient client = new  AspClient();
    
    @Override
    public Map<String, Object> resolveAttributes(File baseDir) {
        try {
            return client.resolveAttributes(baseDir);
        } catch (AspClientException e) {
           AsciiDoctorConsoleUtil.error(e.getMessage());
        }
        return new HashMap<String, Object>();
    }

    @Override
    public void convertFile(File filename, Map<String, Object> options) {
        try {
            client.convertFile(filename.toPath(), options);
        } catch (AspClientException e) {
           AsciiDoctorConsoleUtil.error(e.getMessage());
        }
    }


}
