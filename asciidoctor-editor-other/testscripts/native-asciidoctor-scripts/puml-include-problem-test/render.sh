#!/bin/bash

echo "#####################################"
echo "### Render with native asciidoc #####"
echo "#####################################"
echo "This test script does render different variants for PUML inclusions by asciidoctor"

# just clear former output
source clear-output.sh

echo "> Does work: Render asciidoc document which includes a plantuml file (not including others)"
asciidoctor -r asciidoctor-diagram ./document-with-diagram-no-includes-itself.adoc

echo "> Does not work: Render asciidoc document which includes a plantuml file, which does itself include others"
asciidoctor -r asciidoctor-diagram ./document-with-diagram-using-includes.adoc

echo "> Does not work: Render asciidoc document which includes a plantuml file, which does itself include others"
asciidoctor -r asciidoctor-diagram ./document-with-diagram-using-includes.adoc
