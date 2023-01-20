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
import java.util.Map;

import de.jcup.asciidoctoreditor.EclipseDevelopmentSettings;

public class AsciiDoctorImageProvider extends AbstractAsciiDoctorProvider {
    private String cachedSourceImagesPath;

    AsciiDoctorImageProvider(AsciiDoctorWrapperContext context) {
        super(context);
    }

    public String getCachedSourceImagesPath() {
        if (cachedSourceImagesPath == null) {
            cachedSourceImagesPath = resolveImagesDirPath(getContext().getBaseDir());
        }
        return cachedSourceImagesPath;
    }

    protected String resolveImagesDirPath(File baseDir) {
        boolean debugLoggingEnabled = EclipseDevelopmentSettings.DEBUG_LOGGING_ENABLED;
        
        if (debugLoggingEnabled) {
            getContext().getLogAdapter().resetTimeDiff();
        }
        
        AsciiDoctorAttributesProvider attributesProvider = getContext().getAttributesProvider();
        Map<String, Object> cachedAttributes = attributesProvider.getCachedAttributesOverridenByCustomAttributesFromPreferences();
        Object imagesDir = cachedAttributes.get("imagesdir");

        String imagesDirPath = null;
        if (imagesDir != null) {
            imagesDirPath = imagesDir.toString();
            if (imagesDirPath.startsWith("./")) {
                File imagePathNew = new File(baseDir, imagesDirPath.substring(2));
                imagesDirPath = imagePathNew.getAbsolutePath();
            }
        } else {
            /*
             * fallback when not defined - as defined at
             * https://asciidoctor.org/docs/asciidoctor-pdf/#image-paths
             */
            imagesDirPath = baseDir.getAbsolutePath();
        }
        
        if (debugLoggingEnabled) {
            getContext().getLogAdapter().logTimeDiff("resolveImagesDirPath, baseDir:" + baseDir);
        }
        return imagesDirPath;
    }

    public void reset() {
        this.cachedSourceImagesPath = null;
    }

}
