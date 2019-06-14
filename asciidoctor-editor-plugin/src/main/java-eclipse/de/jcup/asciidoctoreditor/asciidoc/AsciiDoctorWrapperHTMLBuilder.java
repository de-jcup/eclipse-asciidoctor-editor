/*
 * Copyright 2018 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.asciidoctoreditor.asciidoc;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import de.jcup.asciidoctoreditor.PluginContentInstaller;
import de.jcup.asciidoctoreditor.provider.AsciiDoctorProviderContext;

public class AsciiDoctorWrapperHTMLBuilder {

    private AsciiDoctorProviderContext context;

    public AsciiDoctorWrapperHTMLBuilder(AsciiDoctorProviderContext context){
        this.context=context;
    }
    
    public String buildHTMLWithCSS(String html, int refreshAutomaticallyInSeconds) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildPrefixHTML(refreshAutomaticallyInSeconds));
        sb.append(html);
        if (refreshAutomaticallyInSeconds > 0) {
            sb.append("<script type=\"text/javascript\">pageloadEvery(" + refreshAutomaticallyInSeconds * 1000 + ");</script>");
        }
        sb.append("<script type=\"text/javascript\">\n");
        sb.append("function doScrollTo(anchorId){\n");
        sb.append("   element = document.getElementById(anchorId);\n");
        sb.append("   if (element !=null) {\n");
        sb.append("        element.scrollIntoView();\n");
        sb.append("   }\n");
        sb.append("}\n");
        sb.append("</script>");
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();
    }
    
    private String buildPrefixHTML(int refreshAutomaticallyInSeconds) {
        
        List<File> list = new ArrayList<>();

//        File unzipFolder = PluginContentInstaller.INSTANCE.getLibsUnzipFolder();
        File cssFolder = PluginContentInstaller.INSTANCE.getCSSFolder();
        File addonsFolder = PluginContentInstaller.INSTANCE.getAddonsFolder();
        
//        list.add(new File(unzipFolder, "/gems/asciidoctor-1.5.6.1/data/stylesheets/asciidoctor-default.css"));
//        list.add(new File(unzipFolder, "/gems/asciidoctor-1.5.6.1/data/stylesheets/coderay-asciidoctor.css"));
        list.add(new File(cssFolder, "/font-awesome/css/font-awesome.min.css"));
        list.add(new File(cssFolder, "/dejavu/dejavu.css"));
        list.add(new File(cssFolder, "/MathJax/MathJax.js"));
        list.add(new File(addonsFolder, "/javascript/document-autorefresh.js"));
        
        StringBuilder prefixSb = new StringBuilder();
        prefixSb.append("<html>\n");
        prefixSb.append("<head>\n");
        prefixSb.append("  <meta charset=\"UTF-8\">\n");
        prefixSb.append("  <!--[if IE]><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><![endif]-->\n");
        prefixSb.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        prefixSb.append("  <meta name=\"generator\" content=\"Eclipse Asciidoctor Editor\">\n");
        prefixSb.append("  <title>AsciiDoctor Editor temporary output</title>\n");
        for (File file : list) {
            prefixSb.append(createLinkToFile(file));
        }
        prefixSb.append("</head>\n");

        prefixSb.append("<body ");
        if (context.isTOCVisible()) {
            prefixSb.append("class=\"article toc2 toc-left\">");
        } else {
            prefixSb.append("class=\"article\" style=\"margin-left:10px\">");
        }
        return prefixSb.toString();
    }
    
    protected String createLinkToFile(File file) {
        String pathToFile;
        try {
            pathToFile = file.toURI().toURL().toExternalForm();
        } catch (MalformedURLException e) {
            pathToFile = file.getAbsolutePath();
        }
        if (pathToFile.endsWith(".js")) {
            return "<script type=\"text/javascript\" src=\"" + pathToFile + "\"></script>\n";
        }
        return "<link rel=\"stylesheet\" href=\"" + pathToFile + "\">\n";
    }

}
