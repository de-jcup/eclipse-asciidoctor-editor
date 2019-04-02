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
