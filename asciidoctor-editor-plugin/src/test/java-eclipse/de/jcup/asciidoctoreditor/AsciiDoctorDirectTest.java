package de.jcup.asciidoctoreditor;

import static org.asciidoctor.Asciidoctor.Factory.*;

import java.io.File;
import java.util.Map;

import org.asciidoctor.Attributes;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;
import org.junit.Test;

public class AsciiDoctorDirectTest {

	@Test
	public void test() {
		String html = create().convert("= headline", getDefaultOptions());
		System.out.println(html);
	}

	@Test
	public void can_coderay() {
		/* @formatter:off*/
		String content =
				"[source,ruby]              \n"+
				"----                       \n"+
				"require 'sinatra' // <1>   \n"+
                "                           \n"+
				"get '/hi' do // <2>        \n"+
				"  \"Hello World!\" // <3>    \n"+
				"end                        \n"+
				"----                       \n"+
				"<1> Library import         \n"+
				"<2> URL mapping            \n"+
				"<3> HTTP response body     \n";
	   /* @formatter:off*/
		String html = create().convert(content,getDefaultOptions());
		System.out.println(html);
	}
	
	
	private Map<String, Object> getDefaultOptions() {
		Attributes attrs = AttributesBuilder.attributes().showTitle(true).sourceHighlighter("coderay")
				.attribute("coderay-css", "style").attribute("env", "eclipse").attribute("env-eclipse").get();
		OptionsBuilder opts = OptionsBuilder.options().safe(SafeMode.UNSAFE).backend("html5").headerFooter(false)
				.attributes(attrs).option("sourcemap", "true").baseDir(new File("."));
		return opts.asMap();
	}
}
