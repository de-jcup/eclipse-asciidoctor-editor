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
package de.jcup.asciidoctoreditor.template;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.templates.Template;

import de.jcup.eclipse.commons.templates.TemplateSupportConfig;

public class AsciidoctorEditorTemplateSupportConfig implements TemplateSupportConfig {

    @Override
    public String getTemplatesKey() {
        return "de.jcup.asciidoctoreditor.templates";
    }

    @Override
    public List<String> getContextTypes() {
        return Arrays.asList("de.jcup.asciidoctoreditor.template.contexttype");
    }

    @Override
    public String getTemplateImagePath(Template template) {
        return null;
    }

}
