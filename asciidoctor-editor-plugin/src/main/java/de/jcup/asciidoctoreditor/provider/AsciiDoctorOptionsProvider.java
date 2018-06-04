package de.jcup.asciidoctoreditor.provider;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

import org.asciidoctor.Attributes;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;

public class AsciiDoctorOptionsProvider {

	private AsciiDoctorProviderContext context;

	AsciiDoctorOptionsProvider(AsciiDoctorProviderContext context) {
		if (context==null ){
			throw new IllegalArgumentException("context may never be null!");
		}
		this.context = context;
	}

	public Map<String, Object> createDefaultOptions() {
		/* @formatter:off*/
		Attributes attrs;
		if (context.outputFolder==null){
			throw new IllegalStateException("output folder not defined");
		}
		context.imageProvider.ensureImages();
		
		AttributesBuilder attrBuilder = AttributesBuilder.
				attributes().
					showTitle(true).
					sourceHighlighter("coderay").
					attribute("imagesoutdir", createAbsolutePath(context.targetImagesDir.toPath())).
				    attribute("icons", "font").
					attribute("source-highlighter","coderay").
					attribute("coderay-css", "style").
					attribute("env", "eclipse").
					attribute("env-eclipse");
		
		Map<String, Object> cachedAttributes = context.getAttributesProvider().getCachedAttributes();
		for (String key: cachedAttributes.keySet()){
			Object value = cachedAttributes.get(key);
			if (value!=null && value.toString().isEmpty()){
				if ("toc".equals(key)){
					// currently we always remove the TOC (we do show the TOC only by the internal boolean flag
					// also the TOC is not correctly positioned - (always on top instead of being at left side)
					continue;
				}
				attrBuilder.attribute(key,value);
			}
		}
		if (context.tocVisible){
			attrBuilder.attribute("toc","left");
			if (context.tocLevels>0){
				attrBuilder.attribute("toclevels",""+context.tocLevels);
			}
		}
		attrBuilder.imagesDir(context.targetImagesDir.getAbsolutePath());
		
		
		attrs=attrBuilder.get();
		if (context.outputFolder != null) {
			System.out.println("Tempfolder:" + context.outputFolder);
			attrs.setAttribute("outdir", createAbsolutePath(context.outputFolder));
		}
		File destionationFolder= null;
		if (context.outputFolder!=null){
			destionationFolder= context.outputFolder.toFile();
		}else{
			destionationFolder= context.baseDir;
		}
		
		OptionsBuilder opts = OptionsBuilder.options().
				toDir(destionationFolder).
				safe(SafeMode.UNSAFE).
				backend("html5").
				headerFooter(context.tocVisible).
				
				attributes(attrs).
				option("sourcemap", "true").
				baseDir(context.asciidocFile !=null ? context.asciidocFile.getParentFile(): context.baseDir);
		/* @formatter:on*/
		return opts.asMap();
	}

	protected String createAbsolutePath(Path path) {
		return path.toAbsolutePath().normalize().toString();
	}

	public void reset() {
		
	}

}
