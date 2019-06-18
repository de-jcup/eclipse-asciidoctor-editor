In future this folder will be automatically filled by gradle `eclipse` task.

To prevent building .project etc. the logic will be in buildscript for `asciidoctor-editor-other`

libs here will most time have no version number - reason: easier to maintain eclipse builds (no changes in build.properties and more...)
For version info of lib, refer to `META-INF` inside.