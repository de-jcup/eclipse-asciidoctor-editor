package de.jcup.asciidoctoreditor;

import static org.asciidoctor.Asciidoctor.Factory.*;

import java.util.HashMap;

import org.asciidoctor.Asciidoctor;
import org.junit.Test;

public class AsciiDoctorDependencyTest {

	
	@Test
	public void testRuntimeWorks(){
//		http://www.baeldung.com/asciidoctor
//		http://www.baeldung.com/asciidoctor-book
//		https://github.com/asciidoctor/asciidoctorj#converting-documents
		
		Asciidoctor asciidoctor = create();
		String html = asciidoctor.convert("= My first headline\\n\\nText", new HashMap<>());
		System.out.println(html);
	}
}
