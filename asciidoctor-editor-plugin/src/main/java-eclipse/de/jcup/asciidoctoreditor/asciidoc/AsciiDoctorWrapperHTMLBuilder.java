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
import de.jcup.asciidoctoreditor.provider.AsciiDoctorWrapperContext;

public class AsciiDoctorWrapperHTMLBuilder {
    private static final String BODY = "<body";

    /*
     * Configuration to load for the bundled MathJax installation.
     *
     * Without a "config" parameter MathJax 2 initializes without any input or
     * output jax and does not typeset anything - formulas stay visible as raw
     * LaTeX source in the preview.
     *
     * TeX-MML-AM_HTMLorMML is the configuration the bundled installation
     * actually ships. It is a MathJax 2.6.0 reduced with minify-mathjax and
     * contains neither TeX-MML-AM_CHTML (the one Asciidoctor itself requests)
     * nor the CommonHTML output. See minify.cfg in
     * asciidoctor-editor-css/css/MathJax, which documents exactly this
     * parameter.
     */
    private static final String MATHJAX_CONFIG_QUERY = "?config=TeX-MML-AM_HTMLorMML";

    private AsciiDoctorWrapperContext context;

    public AsciiDoctorWrapperHTMLBuilder(AsciiDoctorWrapperContext context) {
        this.context = context;
    }

    public String buildHTMLWithCSS(String origin, boolean autoRefreshEnabled, int refreshAutomaticallyInSeconds) {
        StringBuilder sb = new StringBuilder();
        int bodyIndex = origin.indexOf(BODY);

        /*
         * isAlreadyCompleteHTML... Asciidoctor 2.x does provide correct html. 1.5.4 did
         * not ... , also installed one does not always return full html
         */
        boolean isAlreadyCompleteHTML = bodyIndex != -1;

        String content = origin;
        if (isAlreadyCompleteHTML) {
            if (context.isUsingOnlyLocalResources()) {
                int bodyEnd = origin.indexOf('>', bodyIndex + BODY.length());
                content = content.substring(bodyEnd + 1);

            }
            if (context.isInternalPreview()) {
                /* we add later the doScrollTo script - so we must remove the existing body */
                int endBody = content.indexOf("</body>");
                content = content.substring(0, endBody);

                String prefixHTML = buildLocalPrefixHTML();
                sb.append(prefixHTML);
            }else {
                if (autoRefreshEnabled && !context.isUsingOnlyLocalResources()) {
                    String headEnd = "</head>";
                    int headIndex = content.indexOf(headEnd);
                    if (headIndex!=-1) {
                        String before = content.substring(0,headIndex-1);
                        
                        String after = content.substring(headIndex+headEnd.length());
                        StringBuilder sb1= new StringBuilder();
                        sb1.append(before);
                        sb1.append(createLinkToFile(createFileToAutoRefreshJavascript()));
                        sb1.append(after);
                        
                        content = sb1.toString();
                        
                    }
                }
            }
        } else {
            /* no body found - fallback to local resources (like earlier ) */
            String prefixHTML = buildLocalPrefixHTML();
            sb.append(prefixHTML);
        }

        sb.append(content);
        if (autoRefreshEnabled) {
            sb.append("<script blockType=\"text/javascript\">pageloadEvery(" + refreshAutomaticallyInSeconds * 1000 + ");</script>");
        }
        if (context.isInternalPreview()) {
            sb.append("<script blockType=\"text/javascript\">\n");
            sb.append("function doScrollTo(anchorId){\n");
            sb.append("   element = document.getElementById(anchorId);\n");
            sb.append("   if (element !=null) {\n");
            sb.append("        element.scrollIntoView();\n");
            sb.append("   }\n");
            sb.append("}\n");
            sb.append("</script>");
        }
        sb.append("</body>");
        sb.append("</html>");

        return sb.toString();
    }

    private String buildLocalPrefixHTML() {

        List<String> list = new ArrayList<>();
        File cssFolder = PluginContentInstaller.INSTANCE.getCSSFolder();

        list.add(createLinkToFile(new File(cssFolder, "/font-awesome/css/font-awesome.min.css")));
        list.add(createLinkToFile(new File(cssFolder, "/dejavu/dejavu.css")));
        list.add(createLinkToFile(new File(cssFolder, "/MathJax/MathJax.js"), MATHJAX_CONFIG_QUERY));
        list.add(createLinkToFile(new File(cssFolder, "/default.css")));
        list.add(createLinkToFile(new File(cssFolder, "/coderay.css")));
        list.add(createLinkToFile(createFileToAutoRefreshJavascript()));

        StringBuilder prefixSb = new StringBuilder();
        prefixSb.append("<html>\n");
        prefixSb.append("<head>\n");
        prefixSb.append("  <meta charset=\"UTF-8\">\n");
        prefixSb.append("  <!--[if IE]><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"><![endif]-->\n");
        prefixSb.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        prefixSb.append("  <meta name=\"generator\" content=\"Eclipse Asciidoctor Editor\">\n");
        prefixSb.append("  <title>AsciiDoctor Editor temporary output</title>\n");
        for (String link : list) {
            prefixSb.append(link);
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

    private File createFileToAutoRefreshJavascript() {
        File addonsFolder = PluginContentInstaller.INSTANCE.getAddonsFolder();
        String scriptType=context.isTOCVisible() ? "-with-TOC" :"";
        return new File(addonsFolder, "/javascript/document-autorefresh"+scriptType+".js");
    }

    protected String createLinkToFile(File file) {
        return createLinkToFile(file, null);
    }

    /**
     * Creates the link or script tag for the given file.
     *
     * @param file  file to link to
     * @param query optional query string appended to the resulting URL, e.g.
     *              "?config=..." - may be <code>null</code>.
     *              <p>
     *              The query has to be appended here and must not be part of the
     *              file name: the URL is built with {@link File#toURI()}, which
     *              would encode the question mark, and the ".js" detection below
     *              would no longer match either.
     * @return html tag
     */
    protected String createLinkToFile(File file, String query) {
        String pathToFile;
        try {
            pathToFile = file.toURI().toURL().toExternalForm();
        } catch (MalformedURLException e) {
            pathToFile = file.getAbsolutePath();
        }
        String suffix = query == null ? "" : query;
        if (pathToFile.endsWith(".js")) {
            return "<script blockType=\"text/javascript\" src=\"" + pathToFile + suffix + "\"></script>\n";
        }
        return "<link rel=\"stylesheet\" href=\"" + pathToFile + suffix + "\">\n";
    }

}
