package de.jcup.asciidoctoreditor;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

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
        /* FIXME ATR, 14.12.2018: this is not correct, when using installed asciidoctor css information should not comes comes from OSGI embedded */
//        if (! context.isUsingInstalledAsciiDoctor()){
            File unzipFolder = AsciiDoctorOSGIWrapper.INSTANCE.getLibsUnzipFolder();
            File cssFolder = AsciiDoctorOSGIWrapper.INSTANCE.getCSSFolder();
            File addonsFolder = AsciiDoctorOSGIWrapper.INSTANCE.getAddonsFolder();
            
            list.add(new File(unzipFolder, "/gems/asciidoctor-1.5.6.1/data/stylesheets/asciidoctor-default.css"));
            list.add(new File(unzipFolder, "/gems/asciidoctor-1.5.6.1/data/stylesheets/coderay-asciidoctor.css"));
            list.add(new File(cssFolder, "/font-awesome/css/font-awesome.min.css"));
            list.add(new File(cssFolder, "/dejavu/dejavu.css"));
            list.add(new File(cssFolder, "/MathJax/MathJax.js"));
            list.add(new File(addonsFolder, "/javascript/document-autorefresh.js"));
//        }

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
