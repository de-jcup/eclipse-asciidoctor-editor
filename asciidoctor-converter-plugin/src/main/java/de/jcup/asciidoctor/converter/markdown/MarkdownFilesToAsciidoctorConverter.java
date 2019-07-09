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
package de.jcup.asciidoctor.converter.markdown;

import de.jcup.asciidoctor.converter.AbstractToAsciidoctorConverter;
import nl.jworks.markdown_to_asciidoc.Converter;

public class MarkdownFilesToAsciidoctorConverter extends AbstractToAsciidoctorConverter{
    
    protected String convertImpl(String markdown) {
        if (markdown==null) {
            return "";
        }
        return Converter.convertMarkdownToAsciiDoc(markdown);
    }

    @Override
    public String getName() {
        return "md2asciidoc";
    }

    @Override
    protected String getAcceptedFileEnding() {
        return ".md";
    }

}
