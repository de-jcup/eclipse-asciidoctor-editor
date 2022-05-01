#!/bin/bash

echo "#####################################"
echo "### Render with native asciidoc #####"
echo "#####################################"
echo "This test script does render different variants for PUML inclusions by asciidoctor"

# just clear former output
source clear-output.sh

echo "> Render asciidoc document which includes a plantuml file (not including others)"
asciidoctor -r asciidoctor-diagram ./document-with-diagram-no-includes-itself.adoc

echo "> Render asciidoc document which includes a plantuml file, which does itself include others"
asciidoctor -r asciidoctor-diagram ./document-with-diagram-using-includes.adoc

echo "> Render asciidoc document which includes a plantuml file, which does itself include others - but uses no './'"
asciidoctor -r asciidoctor-diagram ./document-with-diagram-using-includes-but-not-dot-slash-prefix.adoc
