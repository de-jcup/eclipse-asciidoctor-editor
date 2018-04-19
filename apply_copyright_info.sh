#!/bin/bash
RED='\033[0;31m'
LIGHT_RED='\033[1;31m'
LIGHT_GREEN='\033[1;32m'
BROWN='\033[0;33m'
NC='\033[0m' # No Color

echo 
echo "Start applying missing copyright information"
echo 
find -iname \*.java | while read file ; do
if ! grep -q Copyright $file
  then
    echo -e "${BROWN}$file${NC} - ${LIGHT_GREEN}appending copyright.${NC}"
	cat asciidoctor-editor-other/copyright-java.txt $file >$file.new && mv $file.new $file
fi

done
