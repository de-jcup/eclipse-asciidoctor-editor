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
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.asciidoctor.Attributes;
import org.asciidoctor.AttributesBuilder;

import de.jcup.asciidoctoreditor.asciidoc.AsiidocConfigFileSupport;

public class AsciiDoctorAttributesProvider extends AbstractAsciiDoctorProvider {

    public static final String IMAGE_OUTPUT_DIR_NAME = "img";
    private Map<String, Object> cachedAttributes;

    AsciiDoctorAttributesProvider(AsciiDoctorProviderContext context) {
        super(context);
    }

    public Attributes createAttributes() {
        /* @formatter:off */
        Attributes attrs;
        String absolutePathBaseDir = getContext().getBaseDir().getAbsolutePath();
        AttributesBuilder attrBuilder = AttributesBuilder.
                attributes().
                    showTitle(true).
                    noFooter(getContext().isNoFooter()).
                    
                    sourceHighlighter("coderay").
                    
                    attribute("eclipse-editor-basedir",absolutePathBaseDir).
                    attribute("icons", "font").
                    attribute("source-highlighter","coderay").
                    attribute("coderay-css", "style").
                    attribute("env", "eclipse").
                    attribute("env-eclipse");
         /* @formatter:on*/
        if (getContext().isTOCVisible()) {
            attrBuilder.attribute("toc", "left");
            if (getContext().tocLevels > 0) {
                attrBuilder.attribute("toclevels", "" + getContext().tocLevels);
            }
        }else {
            attrBuilder.attribute("!toc","");
        }
        attrs = attrBuilder.get();
        String outputFolderAbsolutePath = createAbsolutePath(getOutputFolder());
        attrs.setAttribute("outdir", outputFolderAbsolutePath);
        
        /* if imagesdir is relative, convert to absolute*/
        Object imagesDir = getCachedAttributes().get("imagesdir");
        if (imagesDir instanceof String) {
            String imagesDirString = (String) imagesDir;
            if (imagesDirString.startsWith(".")) {
                /* a relative path so convert to absolute one*/
                File file = new File(absolutePathBaseDir, imagesDirString);
                try {
                    attrs.setAttribute("imagesdir", file.getCanonicalPath());
                } catch (IOException e) {
                    attrs.setAttribute("imagesdir", file.getAbsolutePath());
                }
            }
        }
        
        handleImagesOutDirAttribute(attrs, absolutePathBaseDir, outputFolderAbsolutePath);
        
        return attrs;
    }

    private void handleImagesOutDirAttribute(Attributes attrs, String absolutePathBaseDir, String outputFolderAbsolutePath) {
        File target = new File(outputFolderAbsolutePath,IMAGE_OUTPUT_DIR_NAME);
        attrs.setAttribute("imagesoutdir", target.getAbsolutePath());
    }

    protected Map<String, Object> getCachedAttributes() {
        if (cachedAttributes == null) {
            cachedAttributes = resolveAttributes();
        }
        return cachedAttributes;
    }

    protected Map<String, Object> resolveAttributes() {
        AsciiDoctorProviderContext context = getContext();
        
        Map<String, Object> map = getContext().getAsciiDoctor().resolveAttributes(context.getAsciiDocFile());

        // now we have to apply the parts from config file as well:
        AsiidocConfigFileSupport support = getContext().getConfigFileSupport();
        return support.calculateResolvedMap(map, getContext().getConfigFiles());
    }

    protected String createAbsolutePath(Path path) {
        return path.toAbsolutePath().normalize().toString();
    }

    public void reset() {
        cachedAttributes = null;
    }

}
