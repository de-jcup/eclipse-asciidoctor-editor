#!/bin/bash
BINTRAY_USER=$1
BINTRAY_API_KEY=$2
BINTRAY_VERSION=$3
BINTRAY_GPG_PP=$4

function show_help_and_exit() {
    echo "Usage:"
    echo " deploy.sh bintrayUser apiKey version gpg-passphrase"
    echo " "
    echo " ATTENTION! use always dedicated version because this version and all its content will be signed!"
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
if [ -z "$BINTRAY_VERSION" ]; then
    echo "bintray version not set"
    show_help_and_exit;
fi
if [ -z "$BINTRAY_GPG_PP" ]; then
    echo "bintray version not set"
    show_help_and_exit;
fi
./pushToBintray.sh ${BINTRAY_USER} ${BINTRAY_API_KEY} de-jcup asciidoctoreditor update-site ${BINTRAY_VERSION} ./asciidoctor-editor-updatesite/ ${BINTRAY_GPG_PP}

