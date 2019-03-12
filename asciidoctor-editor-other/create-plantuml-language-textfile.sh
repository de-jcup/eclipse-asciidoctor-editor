#!/bin/bash 
# This script generates a textfile which can be used for java code generation by
# --------------------------------
#  PlantUMLKeywordsGenerator.java
# --------------------------------
# See http://plantuml.com/developers for more information for details about plantuml language description and how it can be obtained
java -jar ./../asciidoctor-editor-libs/diagram/plantuml.jar -language > ./src/main/resources/plantuml.language.txt