#!/bin/bash 
echo "Plantuml must be installed at your system. If you got an error code please do an 'sudo apt install plantuml' and try again!"
plantuml -language > ./src/main/resources/plantuml.language.txt