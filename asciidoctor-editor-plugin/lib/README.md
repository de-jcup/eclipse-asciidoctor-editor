# Why using libs here and not gradle

Ugly style. I would appreciatte using gradle for eclipse projects builds. But 
I have currently no working way to get all dependencies (of eclipse) by gradle.
So I cannot build eclipse project by gradle but only via ui. So we must checkin libraries...

if there are updates please the copyRuntimeLibs task in asciidoctor-editor-other project!

Call with: `:asciidoctor-editor-other:copyRuntimeLibs`
