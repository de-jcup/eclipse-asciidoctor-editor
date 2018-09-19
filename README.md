![Asciidoctor Editor Logo](https://github.com/de-jcup/eclipse-asciidoctor-editor/blob/master/asciidoctor-editor-other/images/asciidoctor-editor-logo.png)

[![Build status:](https://travis-ci.org/de-jcup/eclipse-asciidoctor-editor.svg?branch=master)](https://travis-ci.org/de-jcup/eclipse-asciidoctor-editor)

[![Drag to your running Eclipse* workspace. *Requires Eclipse Marketplace Client](https://marketplace.eclipse.org/sites/all/themes/solstice/public/images/marketplace/btn-install.png)](http://marketplace.eclipse.org/marketplace-client-intro?mpc_install=3976500 "Drag to your running Eclipse* workspace. *Requires Eclipse Marketplace Client")

# In a nutshell

This an eclipse plugin for editing asciidoctor files

Features:

- Vertical or horizontal splitted Text and Preview on same Editor
- Syntax highlighting in Text Editor
  (Colors can be customized, defaults for dark themes exists too)
- Preview panel is same output as generated HTML by ascii doctor
  (so WYSIWYG)
- Preview panel immediately updated on text save

For more information 
- about the plugin take a look at https://github.com/de-jcup/eclipse-asciidoctor-editor/wiki
- about asciidoctor please  look at https://asciidoctor.org/docs/asciidoc-syntax-quick-reference

# Plugin developers
## How to build this plugin ?
### Setup 
- call `git clone https://github.com/de-jcup/eclipse-asciidoctor-editor.git` 
- open a shell and go into `eclipse-asciidoctor-editor` folder
- call `gradlew eclipse`
- After this is done open your eclipse and import *ALL* existing eclipse projects from `eclipse-asciidoctor-editor` into your workspace
### Build
- Gradle parts are only used for automated testing
- To build the editor plugin, please open "asciidoctor-editor-updatesite/site.xml"
  with eclipse site editor and build Editor feature by pressing "Build" button inside.
### Execute (development phase)
- Simply start as Eclipse Application by a new launch configuration in eclipse 


## License
Eclipse asciidoctor editor is under Apache 2.0 license (http://www.apache.org/licenses/LICENSE-2.0)

<a href="http://with-eclipse.github.io/" target="_blank">
<img alt="with-Eclipse logo" src="http://with-eclipse.github.io/with-eclipse-0.jpg" />
</a>

