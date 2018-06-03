package de.jcup.asciidoctoreditor;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

import org.asciidoctor.Attributes;
import org.asciidoctor.AttributesBuilder;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;

import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferenceConstants;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;

class AsciiDoctorOptionsSupport implements AsciiDoctorSupport {

	AsciiDoctorAttributesSupport attributeSupport;

	private AsciiDoctorSupportContext context;

	AsciiDoctorOptionsSupport(AsciiDoctorSupportContext context) {
		this.context = context;
	}

	public Map<String, Object> createDefaultOptions() {
		/* @formatter:off*/
		Attributes attrs;
		File targetImagesDir =null;
		if (context.outputFolder==null){
			throw new IllegalStateException("output folder not defined");
		}
		context.imageSupport.ensureImages();
		
		AttributesBuilder attrBuilder = AttributesBuilder.
				attributes().
					showTitle(true).
					sourceHighlighter("coderay").
					attribute("imagesoutdir", createAbsolutePath(targetImagesDir.toPath())).
				    attribute("icons", "font").
					attribute("source-highlighter","coderay").
					attribute("coderay-css", "style").
					attribute("env", "eclipse").
					attribute("env-eclipse");
		
		Map<String, Object> cachedAttributes = attributeSupport.getCachedAttributes();
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
			int tocLevels = AsciiDoctorEditorPreferences.getInstance().getIntegerPreference(AsciiDoctorEditorPreferenceConstants.P_EDITOR_TOC_LEVELS);
			if (tocLevels!=0){
				attrBuilder.attribute("toclevels",""+tocLevels);
			}
		}
		if (targetImagesDir!=null){
			attrBuilder.imagesDir(targetImagesDir.getAbsolutePath());
		}
		
		
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

}
