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

	/* @formatter:off*/
	public static final String CODERAY_RUBY_EXAMPLE =
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
	
	@Test
	public void test() {
		String html = create().convert("= headline", getDefaultOptions());
		System.out.println(html);
	}

	@Test
	public void can_coderay() {
		
		String html = create().convert(CODERAY_RUBY_EXAMPLE,getDefaultOptions());
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
