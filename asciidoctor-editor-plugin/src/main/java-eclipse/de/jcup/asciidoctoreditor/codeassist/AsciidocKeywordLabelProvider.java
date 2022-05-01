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
package de.jcup.asciidoctoreditor.codeassist;

import static de.jcup.asciidoctoreditor.ui.AsciidoctorIconConstants.*;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE.SharedImages;

import de.jcup.asciidoctoreditor.AsciiDoctorEditorActivator;
import de.jcup.asciidoctoreditor.document.keywords.DocumentKeyWords;
import de.jcup.eclipse.commons.keyword.DocumentKeyWord;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class AsciidocKeywordLabelProvider extends LabelProvider {

    @Override
    public Image getImage(Object element) {
        if (!(element instanceof String)) {
            return super.getImage(element);
        }
        String word = (String) element;
        String path = null;
        for (DocumentKeyWord keyword : DocumentKeyWords.getAll()) {
            if (word.equalsIgnoreCase(keyword.getText())) {
//                path = PATH_ICON_ASCIIDOCTOR_EDITOR;
                return PlatformUI.getWorkbench().getSharedImages().getImage(SharedImages.IMG_OBJS_BKMRK_TSK);
            }
        }
        if (path == null) {
            String x = ((String) element).toLowerCase();
            if (x.startsWith("=")) {
                path = PATH_OUTLINE_ICON_HEADLINE;
            } else if (x.startsWith("[[")) {
                path = PATH_OUTLINE_ICON_INLINE_ANCHOR;
            } else if (x.startsWith("include::")) {
                path = PATH_OUTLINE_ICON_INCLUDE;
            }
        }
        if (path == null) {
            return super.getImage(element);
        }
        return EclipseUtil.getImage(path, AsciiDoctorEditorActivator.getDefault());
    }
}
