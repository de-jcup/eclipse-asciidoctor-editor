#!/bin/bash
API=https://api.bintray.com
BINTRAY_USER=$1
BINTRAY_API_KEY=$2
BINTRAY_OWNER=$3
BINTRAY_REPO=$4
PCK_NAME=$5
PCK_VERSION=$6
GPG_PASSPHRASE=$7

function show_help_and_exit() {
    echo "Usage:"
    echo " $0 bintrayUser apiKey owner repo packagename version gpg-passphrase"
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
if [ -z "$BINTRAY_OWNER" ]; then
    echo "bintray owner not set"
    show_help_and_exit;
fi
if [ -z "$BINTRAY_REPO" ]; then
    echo "bintray repo not set"
    show_help_and_exit;
fi
if [ -z "$PCK_NAME" ]; then
    echo "bintray package name not set"
    show_help_and_exit;
fi
if [ -z "$PCK_VERSION" ]; then
    echo "bintray package version not set"
    show_help_and_exit;
fi
if [ -z "$GPG_PASSPHRASE" ]; then
    echo "bintray gpg passprase not set"
    show_help_and_exit;
fi

function main() {
sign_version
}

function sign_version() {
echo "${BINTRAY_USER}"
echo "${BINTRAY_API_KEY}"
echo "${BINTRAY_OWNER}"
echo "${BINTRAY_REPO}"
echo "${PCK_NAME}"
echo "${PCK_VERSION}"

# https://bintray.com/docs/api/#_content_signing
echo "GPG signing the version"
curl -X POST --header "X-GPG-PASSPHRASE: ${GPG_PASSPHRASE}" -u ${BINTRAY_USER}:${BINTRAY_API_KEY} https://api.bintray.com/gpg/${BINTRAY_OWNER}/${BINTRAY_REPO}/${PCK_NAME}/versions/${PCK_VERSION}

}


main "$@"