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
package de.jcup.asciidoctoreditor.globalmodel;

import java.io.File;

public class AsciidocDiagramNode extends AbstractAsciidocNode {

    File diagramFile;

    public File getDiagramFile() {
        return diagramFile;
    }

    @Override
    public String toString() {
        return resolveSafeAsciidocFileName() + " - diagram::" + resolveSafeDiagramFileName() + "[]";
    }

    private String resolveSafeDiagramFileName() {
        String target = "null";
        if (diagramFile != null) {
            target = diagramFile.getName();
        }
        return target;
    }
}
