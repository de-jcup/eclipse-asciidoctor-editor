What is this subproject ?
-------------------------

It handles gradle dependencies to asp
It does also copy the downloaded asp dependencies to eclipse projects where we have no automatated gradle classpathes...

`./gradlew installLibraries` will download and install libraries from bintray repository

 (installLibaries is also automatically done by assemble/normal build)