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

import de.jcup.asciidoctoreditor.EclipseDevelopmentSettings;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDoctorBackendType;

public class AsciiDoctorOptionsProvider extends AbstractAsciiDoctorProvider {

    AsciiDoctorOptionsProvider(AsciiDoctorProjectProviderContext context) {
        super(context);
    }

    public Map<String, Object> createDefaultOptions(AsciiDoctorBackendType backend) {
        /* @formatter:off*/
		Attributes attrs;
		Path outputFolder = getContext().getOutputFolder();
		if (outputFolder==null){
			throw new IllegalStateException("output folder not defined");
		}
		
		AttributesBuilder attrBuilder = AttributesBuilder.
				attributes().
					showTitle(true).
					noFooter(getContext().isNoFooter()).
					sourceHighlighter("coderay").
					attribute("eclipse-editor-basedir",getContext().getRootDirectory().getAbsolutePath()).
				    attribute("icons", "font").
					attribute("source-highlighter","coderay").
					attribute("coderay-css", "style").
					attribute("env", "eclipse").
					attribute("env-eclipse");
		 /* @formatter:on*/

        Map<String, Object> cachedAttributes = getContext().getAttributesProvider().getCachedAttributes();
        for (String key : cachedAttributes.keySet()) {
            Object value = cachedAttributes.get(key);
            if (value == null || value.toString().isEmpty()) {
                continue;
            }
            if ("toc".equals(key)) {
                // currently we always remove the TOC (we do show the TOC
                // only by the internal boolean flag
                // also the TOC is not correctly positioned - (always on top
                // instead of being at left side)
                continue;
            }
            if ("basebackend-html".equals(key)) {
                if (AsciiDoctorBackendType.PDF.equals(backend)) {
                    // we ignore this for PDF
                    continue;
                }
            }
            attrBuilder.attribute(key, value);
        }
        // we always override "outfilesuffix" to have no unexpected output file endings
        // by users settings
        attrBuilder.attribute("outfilesuffix", backend.getOutfilesuffix());

        if (getContext().isTOCVisible()) {
            attrBuilder.attribute("toc", "left");
            if (getContext().tocLevels > 0) {
                attrBuilder.attribute("toclevels", "" + getContext().tocLevels);
            }
        }
        ImageHandlingMode imageHandlingMode = getContext().getImageHandlingMode();
        getContext().getImageCopyProvider().ensureImages();

        if (AsciiDoctorBackendType.PDF.equals(backend)) {
            /*
             * PDF is different: we need to resolve the path to images by location of
             * rendered file:
             */
            String absoluteImagePath = "" + cachedAttributes.get(AttributeSearchParameter.IMAGES_DIR_ATTRIBUTE.getName());
            if (absoluteImagePath.isEmpty() || ".".equals(absoluteImagePath) || "null".equals(absoluteImagePath)) {
                /* in PDF we need a absolute image dir set! */
                File editorFile = getContext().getEditorFileOrNull();
                if (EclipseDevelopmentSettings.DEBUG_LOGGING_ENABLED) {
                    getContext().getLogAdapter().logInfo("absolute path is not set ('" + absoluteImagePath + "' for editor file:" + editorFile);
                }
                if (editorFile != null) {
                    absoluteImagePath = editorFile.getParentFile().getAbsolutePath();
                    attrBuilder.attribute("imagesoutdir", absoluteImagePath);
                    attrBuilder.imagesDir(absoluteImagePath);
                    if (EclipseDevelopmentSettings.DEBUG_LOGGING_ENABLED) {
                        getContext().getLogAdapter().logInfo(">> changed absolute image path to " + absoluteImagePath);
                    }
                }

            }
        }

        if (imageHandlingMode == ImageHandlingMode.STORE_DIAGRAM_FILES_LOCAL) {
            String imagesoutpath = null;
            File editorFileOrNull = getContext().getEditorFileOrNull();
            if (editorFileOrNull != null) {
                imagesoutpath = createAbsolutePath(editorFileOrNull.getParentFile().toPath());
                attrBuilder.imagesDir(imagesoutpath);
                attrBuilder.attribute("imagesoutdir", imagesoutpath);
            }
        }

        attrs = attrBuilder.get();
        attrs.setAttribute("outdir", createAbsolutePath(outputFolder));

        File destionationFolder = outputFolder.toFile();
        /* @formatter:off */
        OptionsBuilder opts = OptionsBuilder.
                options().
                    toDir(destionationFolder).
                    safe(SafeMode.UNSAFE).
                    backend(backend.getBackendString()).
                    headerFooter(getContext().isTOCVisible()).
                    attributes(attrs).
                    option("sourcemap", "true").
                    baseDir(getContext().getRootDirectory());
        
        /* @formatter:on*/
        Map<String, Object> asMap = opts.asMap();
        return asMap;
    }

    protected String createAbsolutePath(Path path) {
        return path.toAbsolutePath().normalize().toString();
    }

    public void reset() {
        /* nothing to do */
    }

}
