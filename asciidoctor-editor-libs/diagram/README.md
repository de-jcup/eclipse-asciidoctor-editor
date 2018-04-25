# Explanation about this library folder

Diagram support was not easy inside eclipse + jruby
because the osgi classloader which instantiates asciidoctorj
does only knows the jars available at MANIFEST.MF
But jars inside jars are not recognized.

So the diagrams were not supported (no diita, no plantuml, no graphviz

To enable it the easiest way was to extract necessary jars separetely and
to add it to normal eclipse classpath...



# Overview

from asciidoctor-diagram-1.5.4.1.jar
- asciidoctor-diagram-java-1.3.10.jar
- ditaamini-0.10.jar
- plantuml.jar

from JRUBY ... ruby.home...shared/json/ext
- generator.jar
- parser.jar
