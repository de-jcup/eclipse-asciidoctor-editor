package de.jcup.asciidoctoreditor;

import static org.asciidoctor.Asciidoctor.Factory.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
import org.jruby.RubyInstanceConfig;
import org.jruby.javasupport.JavaEmbedUtils;

public class AsciiDoctorOSGIWrapper {

	private Asciidoctor asciidoctor;
	private Path tempFolder;
	private EclipseResourceHelper helper;

	public AsciiDoctorOSGIWrapper() {
		helper=EclipseResourceHelper.DEFAULT;

		// https://github.com/asciidoctor/asciidoctorj#using-asciidoctorj-in-an-osgi-environment
		RubyInstanceConfig config = new RubyInstanceConfig();
		config.setLoader(this.getClass().getClassLoader());
		/* @formatter:off*/
		JavaEmbedUtils.initialize(Arrays.asList(
				"META-INF/jruby.home/lib/ruby/2.0", 
				"gems/asciidoctor-1.5.6.1/lib",
				"gems/coderay-1.1.0/lib",
				"gems/erubis-2.7.0/lib",
				"gems/haml-4.0.5/lib",
				"gems/open-uri-cached-0.0.5/lib",
				"gems/slim-3.0.6/lib",
				"gems/temple-0.7.7/lib",
				"gems/thread_safe-0.3.6/lib",
				"gems/tilt-2.0.1/lib"
		), config);
		/* @formatter:on*/
		try {
			tempFolder = Files.createTempDirectory("ascii-doctor-eclipse");
			tempFolder.toFile().deleteOnExit();
		} catch (IOException e) {
			/* FIXME ATR, 15.03.2018: handle this exception!!! */
			e.printStackTrace();
		}
		asciidoctor = create(this.getClass().getClassLoader());
	}

	public String convertToHTML(String asciiDoc) {
		String html = asciidoctor.convert(asciiDoc, getDefaultOptions());
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head>");
		
		sb.append("<style>");
		try(FileInputStream defaultFOS=new FileInputStream(helper.getFileInPlugin("css/default.css"));
				FileInputStream coderayFOS=new FileInputStream(helper.getFileInPlugin("css/coderay.css"))){
			/* adopted from https://github.com/asciidoctor/asciidoctor-intellij-plugin :*/	
			String myInlineCss = IOUtils.toString(defaultFOS);
//	      myInlineCssDarcula = myInlineCss + IOUtils.toString(JavaFxHtmlPanel.class.getResourceAsStream("darcula.css"));
//	      myInlineCssDarcula += IOUtils.toString(JavaFxHtmlPanel.class.getResourceAsStream("coderay-darcula.css"));
			myInlineCss += IOUtils.toString(coderayFOS);
			sb.append(myInlineCss);
			
		}catch(IOException e){
			/* FIXME ATR, 15.03.2018: handle exception */
			e.printStackTrace();
		}
		
		sb.append("</style>");
		try{
			/* FIXME ATR, 15.03.2018: replacwe the regexp replaceAll with static one (at least) */
			File fontAwesomeCSSfile = helper.getFileInPlugin("css/font-awesome/css/font-awesome.min.css");
			String fontAwesomeCssPath = fontAwesomeCSSfile.toURI().toURL().toExternalForm();//fontAwesomeCSSfile.getAbsolutePath().replaceAll("\\\\", "/" );
			System.out.println(fontAwesomeCssPath);
			String fontAwesomeCssLink = "<link rel=\"stylesheet\" href=\"" + fontAwesomeCssPath+ "\">";
			sb.append(fontAwesomeCssLink);
			
			File dejavouFile = helper.getFileInPlugin("css/dejavu/dejavu.css");
			String dejavouPath = dejavouFile.getAbsolutePath().replaceAll("\\\\", "/" );
			String dejavuCssLink = "<link rel=\"stylesheet\" href=\"" + dejavouPath + "\">";
			sb.append(dejavuCssLink);
		}catch(IOException e){
			/* FIXME ATR, 15.03.2018: handle exception */
			e.printStackTrace();
		}
		sb.append("</head>");
		sb.append("<body>");
		sb.append(html);
		sb.append("</body>");
		sb.append("</html>");
		
		return sb.toString();
	}

	private Map<String, Object> getDefaultOptions() {
		Attributes attrs = AttributesBuilder.attributes().showTitle(true).sourceHighlighter("coderay")
				.attribute("coderay-css", "style").attribute("env", "eclipse").attribute("env-eclipse").get();
		if (tempFolder != null) {
			System.out.println("Tempfolder:"+tempFolder);
			attrs.setAttribute("outdir", tempFolder.toAbsolutePath().normalize().toString());
		}
		OptionsBuilder opts = OptionsBuilder.options().safe(SafeMode.UNSAFE).backend("html5").headerFooter(false)
				.attributes(attrs).option("sourcemap", "true").baseDir(new File("."));
		return opts.asMap();
	}
}
