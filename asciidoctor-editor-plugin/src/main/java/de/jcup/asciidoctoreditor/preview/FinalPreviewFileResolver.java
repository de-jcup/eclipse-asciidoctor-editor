/*
 * Copyright 2022 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.preview;

import java.io.File;
import java.io.IOException;

import de.jcup.asciidoctoreditor.EditorType;
import de.jcup.asciidoctoreditor.asciidoc.AsciiDocFileUtils;

/**
 * We always generate HTML 5 output for our previews. But for PlantUML and
 * Ditaa, we want to show always the SVG files inside the browser because those
 * can be zoomed. With this class it is possible to fetch the SVG file instead
 * of HTML. Also it is possible to enable a global feature toggle.
 * 
 * @author albert
 *
 */
public class FinalPreviewFileResolver {

    public File resolvePreviewFileFromGeneratedHTMLFile(File createdHTML5File, EditorType type) {
        if (type == null) {
            return createdHTML5File;
        }
        switch (type) {
        case DITAA:
            return fetchFirstSVGImage(createdHTML5File);
        case PLANTUML:
            return fetchFirstSVGImage(createdHTML5File);
        case ASCIIDOC:
        default:
            return createdHTML5File;

        }
    }

    private File fetchFirstSVGImage(File createdHTML5File) {
        try {
            String html5 = AsciiDocFileUtils.readAsciidocFile(createdHTML5File);
            String firstImgSource = parseFirstImageSource(html5);
            if (firstImgSource != null) {
                return new File(firstImgSource);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return createdHTML5File;
    }

    private String parseFirstImageSource(String html5) {
        if (html5==null) {
            return null;
        }
        int startIndex = html5.indexOf("<img");
        if (startIndex==-1) {
            return null;
        }
        int endIndex = html5.indexOf('>',startIndex);
        if (endIndex==-1) {
            return null;
        }
        
        String imgTag = html5.substring(startIndex,endIndex);
        int srcIndex=imgTag.indexOf("src");
        if (srcIndex==-1) {
            return null;
        }
        int first = imgTag.indexOf('"');
        if (first==-1) {
            return null;
        }
        int last = imgTag.indexOf('"', first+1);
        if (last==-1) {
            return null;
        }
        String path = imgTag.substring(first+1,last);
        return path;
    }

}
