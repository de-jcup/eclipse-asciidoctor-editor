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
import java.util.HashMap;
import java.util.Map;

import de.jcup.asciidoctoreditor.CustomEntrySupport;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocConfigFileSupport;
import de.jcup.asp.api.asciidoc.AsciidocAttributes;
import de.jcup.asp.api.asciidoc.AsciidocAttributesBuilder;

public class AsciiDoctorAttributesProvider extends AbstractAsciiDoctorProvider {

    public static final String IMAGE_OUTPUT_DIR_NAME = "img";
    private Map<String, Object> cachedAttributes;

    AsciiDoctorAttributesProvider(AsciiDoctorWrapperContext context) {
        super(context);
    }

    public AsciidocAttributes createAttributes() {
        /* @formatter:off */
        String absolutePathProjectBaseDir = getContext().getProjectBaseDir().getAbsolutePath();
        String absolutePathBaseDir = getContext().getBaseDir().getAbsolutePath();

        AsciidocAttributesBuilder attrBuilder = AsciidocAttributes.builder();
        
        attrBuilder.
                showTitle(true).
                noFooter(getContext().isNoFooter()).
                
                sourceHighlighter("coderay").
                customAttribute("eclipse-editor-projectbasedir",absolutePathProjectBaseDir).
                customAttribute("eclipse-editor-basedir",absolutePathBaseDir).
                customAttribute("icons", "font").
                customAttribute("env", "eclipse").
                customAttribute("env-eclipse",true);
         /* @formatter:on*/
        if (getContext().isTOCVisible()) {
            attrBuilder.customAttribute("toc", "left");
            if (getContext().tocLevels > 0) {
                attrBuilder.customAttribute("toclevels", "" + getContext().tocLevels);
            }
        } else {
            attrBuilder.customAttribute("!toc", "");
        }
        String outputFolderAbsolutePath = createAbsolutePath(getOutputFolder());
        attrBuilder.customAttribute("outdir", outputFolderAbsolutePath);
        /* if imagesdir is relative, convert to absolute */
        Object imagesDir = getCachedAttributesOverridenByCustomAttributesFromPreferences().get("imagesdir");
        if (imagesDir instanceof String) {
            String imagesDirString = (String) imagesDir;
            if (imagesDirString.startsWith(".")) {
                /* a relative path so convert to absolute one */
                File file = new File(absolutePathProjectBaseDir, imagesDirString);
                try {
                    attrBuilder.imagesDir(file.getCanonicalPath());
                } catch (IOException e) {
                    attrBuilder.imagesDir(file.getAbsolutePath());
                }
            }
        }

        
        /* handle output directory */
        File target = new File(outputFolderAbsolutePath, IMAGE_OUTPUT_DIR_NAME);
        attrBuilder.customAttribute("imagesoutdir", target.getAbsolutePath());

        /* finally handle custom entries from preferences */
        CustomEntrySupport attributesSupport = getContext().getProvider().getCustomAttributesEntrySupport();
        if ( attributesSupport.areCustomEntriesEnabled()) {
            Map<String, String> customAttributesFromPreferences = attributesSupport.fetchConfiguredEntriesAsMap();
            for (String customAttributeKey: customAttributesFromPreferences.keySet()) {
                String customAttributevalue = customAttributesFromPreferences.get(customAttributeKey);
                attrBuilder.customAttribute(customAttributeKey, customAttributevalue);
            }
        }
        return attrBuilder.build();
    }

    /**
     * 
     * @return cached attributes (but still overriden with custom parts from preferences!)
     */
    protected Map<String, Object> getCachedAttributesOverridenByCustomAttributesFromPreferences() {
        if (cachedAttributes == null) {
            cachedAttributes = resolveAttributes();
        }
        
        CustomEntrySupport attributesSupport = getContext().getProvider().getCustomAttributesEntrySupport();
        if (! attributesSupport.areCustomEntriesEnabled()) {
            return cachedAttributes;
        }
        
        /* overwrite with custom attributes configured in preferences */
        Map<String, String> configuredEntries = attributesSupport.fetchConfiguredEntriesAsMap();
        Map<String, Object> combined = new HashMap<String, Object>(cachedAttributes);
        combined.putAll(configuredEntries);

        return combined;
        
    }

    protected Map<String, Object> resolveAttributes() {
        AsciiDoctorWrapperContext context = getContext();

        Map<String, Object> map = getContext().getAsciiDoctor().resolveAttributes(context.getEditorFileOrNull());

        // now we have to apply the parts from config file as well:
        AsciiDocConfigFileSupport support = getContext().getConfigFileSupport();
        Map<String, Object> resolved = null;
        if (support==null) {
            resolved = new HashMap<String, Object>(map);
        }else {
            resolved = support.calculateResolvedMap(map, getContext().getConfigFiles());
        }
        return resolved;
    }

    protected String createAbsolutePath(Path path) {
        return path.toAbsolutePath().normalize().toString();
    }

    public void reset() {
        cachedAttributes = null;
    }

}
