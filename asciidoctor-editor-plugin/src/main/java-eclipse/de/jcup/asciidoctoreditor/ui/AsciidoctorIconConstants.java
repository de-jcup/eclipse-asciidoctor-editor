package de.jcup.asciidoctoreditor.ui;

public class AsciidoctorIconConstants {
    
    public static final String PATH_ICON_ASCIIDOCTOR_EDITOR = commonPath("asciidoctor-editor.png");
    public static final String PATH_OUTLINE_ICON_HEADLINE = outlinePath("headline.gif");
    public static final String PATH_OUTLINE_ICON_INCLUDE = outlinePath("gotoobj_tsk.png");
    public static final String PATH_OUTLINE_ICON_ERROR = outlinePath("error_tsk.png");
    public static final String PATH_OUTLINE_ICON_INFO = outlinePath("info_tsk.png");
    public static final String PATH_OUTLINE_ICON_INLINE_ANCHOR = outlinePath("inline_anchor.gif");
    public static final String PATH_OUTLINE_ICON_IMAGE = outlinePath("image.gif");
    public static final String PATH_OUTLINE_ICON_DITAA = commonPath("ditaa-asciidoctor-editor.png");
    public static final String PATH_OUTLINE_ICON_PLANTUML = commonPath("plantuml-asciidoctor-editor.png");
    
    static String outlinePath(String name) {
        return "icons/outline/"+name;
    }

    private static String commonPath(String name) {
        return "icons/"+name;
    }
    
}
