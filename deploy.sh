#!/bin/bash
BINTRAY_USER=$1
BINTRAY_API_KEY=$2

function show_help_and_exit() {
	echo "Usage:"
	echo " deploy.sh bintrayUser apiKey"
	exit 1
}

if [ -z "$BINTRAY_USER" ]; then
	echo "bintray user not set"
	show_help_and_exit;
fi
if [ -z "$BINTRAY_API_KEY" ]; then
	echo "bintray api key not set"
	show_help_and_exit;
fi
./pushToBintray.sh ${BINTRAY_USER} ${BINTRAY_API_KEY} de-jcup asciidoctoreditor update-site current ./asciidoctor-editor-updatesite/