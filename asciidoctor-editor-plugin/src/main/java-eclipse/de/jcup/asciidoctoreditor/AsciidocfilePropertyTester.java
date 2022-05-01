/*
 * Copyright 2020 Albert Tregnaghi
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

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;

import de.jcup.asciidoctoreditor.util.AsciiDoctorEditorUtil;

public class AsciidocfilePropertyTester extends PropertyTester {

    private static final String IS_ASCIIDOC_FILE = "isAsciidocFile";

    /*
     * we use same setup as done inside
     * content-type="de.jcup.asciidoctoreditor.content.asciidoc", means:
     * asciidoc,adoc,asc,ad
     */
    public AsciidocfilePropertyTester() {
    }

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (!(receiver instanceof IFile)) {
            /* not supported */
            return false;
        }
        IFile file = (IFile) receiver;
        if (IS_ASCIIDOC_FILE.contentEquals(property)) {
            return testIsAsciidocfile(file);
        }
        return false;
    }

    private boolean testIsAsciidocfile(IFile file) {
        String extension = file.getFileExtension();
        return AsciiDoctorEditorUtil.isAsciidocFileExtension(extension);
    }

}
