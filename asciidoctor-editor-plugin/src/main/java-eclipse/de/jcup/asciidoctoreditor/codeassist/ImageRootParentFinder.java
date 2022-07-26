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
package de.jcup.asciidoctoreditor.codeassist;

import java.io.File;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;

public class ImageRootParentFinder implements RootParentFinder {

    private ImageBaseParentResolver resolver = new ImageBaseParentResolver();
    
    @Override
    public File findRootParent() {
        AsciiDoctorEditor editor = AsciiDoctorEditorUtil.findActiveAsciidoctorEditorOrNull();
        if (editor == null) {
            return null;
        }
        File file = editor.getEditorFileOrNull();
        if (file == null) {
            return null;
        }
        return resolver.getBaseParentDir(file);
    }

}
