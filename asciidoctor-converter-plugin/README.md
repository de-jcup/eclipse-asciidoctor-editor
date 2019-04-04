# Converter plugin for asciidoctor editor

## Currently in scope:
### Markdown to Asciidoc
Using:
https://github.com/bodiam/markdown-to-asciidoc/wiki

## Changed build behaviour
### Why ?
I was always sad about adding java libraries to git only for eclipse builds with PDT working.
So here another approach with gradle

### Empty lib folder
The `build.properties` file does contain the dependencies to lib/* already (text form) and must not be changed
(except when a new library version is used)

With gradle task `installLibraries` the libraries defined for this project are automatically copied 
to "lib" folder.

This must be done only when initial checkout is done!