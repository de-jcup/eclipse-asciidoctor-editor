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
package de.jcup.asciidoctoreditor.toolbar;

import org.eclipse.jface.resource.ImageDescriptor;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;

public class BoldFormatAction extends FormatTextAction {

    private static ImageDescriptor IMG_DESCRIPTOR_BOLD = createToolbarImageDescriptor("format_bold.png");

    public BoldFormatAction(AsciiDoctorEditor editor) {
        super(editor, "Bold (Ctrl + b)", IMG_DESCRIPTOR_BOLD);
    }

    @Override
    protected String formatPrefix() {
        return "*";
    }

    @Override
    protected String formatPostfix() {
        return "*";
    }

}
