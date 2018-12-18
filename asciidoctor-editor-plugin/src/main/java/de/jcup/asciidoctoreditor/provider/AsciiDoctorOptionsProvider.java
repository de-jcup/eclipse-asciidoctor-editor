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
        if (context == null) {
            throw new IllegalArgumentException("context may never be null!");
        }
        this.context = context;
    }

    public Map<String, Object> createDefaultOptions() {
        /* @formatter:off*/
		Attributes attrs;
		Path outputFolder = context.getOutputFolder();
		if (outputFolder==null){
			throw new IllegalStateException("output folder not defined");
		}
		context.getImageProvider().ensureImages();
		
		AttributesBuilder attrBuilder = AttributesBuilder.
				attributes().
					showTitle(true).
					sourceHighlighter("coderay").
					attribute("eclipse-editor-basedir",context.getBaseDir().getAbsolutePath()).
					attribute("imagesoutdir", createAbsolutePath(context.targetImagesDir.toPath())).
				    attribute("icons", "font").
					attribute("source-highlighter","coderay").
					attribute("coderay-css", "style").
					attribute("env", "eclipse").
					attribute("env-eclipse");
		 /* @formatter:on*/
        Map<String, Object> cachedAttributes = context.getAttributesProvider().getCachedAttributes();
        for (String key : cachedAttributes.keySet()) {
            Object value = cachedAttributes.get(key);
            if (value != null && value.toString().isEmpty()) {
                if ("toc".equals(key)) {
                    // currently we always remove the TOC (we do show the TOC
                    // only by the internal boolean flag
                    // also the TOC is not correctly positioned - (always on top
                    // instead of being at left side)
                    continue;
                }
                attrBuilder.attribute(key, value);
            }
        }
        if (context.isTOCVisible()) {
            attrBuilder.attribute("toc", "left");
            if (context.tocLevels > 0) {
                attrBuilder.attribute("toclevels", "" + context.tocLevels);
            }
        }
        attrBuilder.imagesDir(context.targetImagesDir.getAbsolutePath());

        attrs = attrBuilder.get();
        attrs.setAttribute("outdir", createAbsolutePath(outputFolder));

        File destionationFolder = outputFolder.toFile();

        OptionsBuilder opts = OptionsBuilder.options().toDir(destionationFolder).safe(SafeMode.UNSAFE).backend("html5")
                .headerFooter(context.isTOCVisible()).

                attributes(attrs).option("sourcemap", "true").baseDir(context.getBaseDir());
        /* @formatter:on*/
        return opts.asMap();
    }

    protected String createAbsolutePath(Path path) {
        return path.toAbsolutePath().normalize().toString();
    }

    public void reset() {

    }

}
