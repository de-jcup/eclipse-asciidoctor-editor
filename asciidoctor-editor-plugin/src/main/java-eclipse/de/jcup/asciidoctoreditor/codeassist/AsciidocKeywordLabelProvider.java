package de.jcup.asciidoctoreditor.codeassist;

import static de.jcup.asciidoctoreditor.AsciidoctorIconConstants.*;

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
        for (DocumentKeyWord keyword: DocumentKeyWords.getAll()) {
            if (word.equalsIgnoreCase(keyword.getText())){
//                path = PATH_ICON_ASCIIDOCTOR_EDITOR;
                return PlatformUI.getWorkbench().getSharedImages().getImage(SharedImages.IMG_OBJS_BKMRK_TSK);
            }
        }
        if (path==null) {
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
