package de.jcup.asciidoctoreditor.other;

import static org.asciidoctor.Asciidoctor.Factory.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Attributes;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
import org.junit.Test;

public class AsciiDoctorDependencyTest {

	
	@Test
	public void testRuntimeWorks(){
//		http://www.baeldung.com/asciidoctor
//		http://www.baeldung.com/asciidoctor-book
//		https://github.com/asciidoctor/asciidoctorj#converting-documents
		
		Asciidoctor asciidoctor = create();
		
		
		
		String html = asciidoctor.convert("= My first headline", getDefaultOptions());
		System.out.println(html);
		
	}
	
	 private Map<String, Object> getDefaultOptions() {
		    Attributes attrs = AttributesBuilder.attributes().showTitle(true)
		      .sourceHighlighter("coderay").attribute("coderay-css", "style")
		      .attribute("env", "idea").attribute("env-idea").get();

//		    if (imagesPath != null) {
//		      final AsciiDocApplicationSettings settings = AsciiDocApplicationSettings.getInstance();
//		      if (settings.getAsciiDocPreviewSettings().getHtmlPanelProviderInfo().getClassName().equals(JavaFxHtmlPanelProvider.class.getName())) {
//		        attrs.setAttribute("outdir", imagesPath.toAbsolutePath().normalize().toString());
//		      }
//		    }
		    OptionsBuilder opts = OptionsBuilder.options().safe(SafeMode.UNSAFE).backend("html5").headerFooter(false).attributes(attrs).option("sourcemap", "true")
		      .baseDir(new File(".")).destinationDir(new File("./bin/output-asciidoctor"));
		    return opts.asMap();
		  }
}
