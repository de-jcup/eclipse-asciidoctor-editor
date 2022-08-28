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
package de.jcup.asciidoctoreditor.diagram.plantuml;

import de.jcup.asciidoctoreditor.AsciidoctorEditorOutlineSupport;
import de.jcup.asciidoctoreditor.outline.AsciiDoctorPlantUMLContentOutlinePage;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModel;
import de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModelBuilder;

public class AsciidoctorPlantUMLEditorOutlineSupport extends AsciidoctorEditorOutlineSupport {

    private AsciiDoctorPlantUMLContentOutlinePage outlinePage;

    public AsciidoctorPlantUMLEditorOutlineSupport(AsciiDoctorPlantUMLEditor editor) {
        super(editor);
    }

    @Override
    protected AsciiDoctorScriptModelBuilder createModelBuilder() {
        return new PlantumlScriptModelBuilder();
    }

    protected void validate(AsciiDoctorScriptModel model) {
        /* we currently do not validate PlantUML */
    }

    /**
     * @return outline page, never <code>null</code>. If non exists a new one will
     *         be created
     */
    public AsciiDoctorPlantUMLContentOutlinePage getOutlinePage() {
        if (outlinePage == null) {
            outlinePage = new AsciiDoctorPlantUMLContentOutlinePage(getEditor());
        }
        return outlinePage;
    }

    @Override
    public AsciiDoctorPlantUMLEditor getEditor() {
        return (AsciiDoctorPlantUMLEditor) super.getEditor();
    }

}
