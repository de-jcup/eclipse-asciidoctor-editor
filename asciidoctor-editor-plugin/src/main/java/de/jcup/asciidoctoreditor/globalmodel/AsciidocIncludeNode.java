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

public class AsciidocIncludeNode extends AbstractAsciidocNode {

    AsciidocFile includedAsciidocFile;

    public AsciidocFile getIncludedAsciidocFile() {
        return includedAsciidocFile;
    }

    @Override
    public String toString() {
        return resolveSafeAsciidocFileName() + " - include::" + resolveSafeIncludedAsciidocFileName() + "[]";
    }

    private String resolveSafeIncludedAsciidocFileName() {
        String target = "null";
        if (includedAsciidocFile != null) {
            File file2 = includedAsciidocFile.getFile();
            target = file2.getName();
        }
        return target;
    }
}
