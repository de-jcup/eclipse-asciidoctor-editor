/*
 * Copyright 2021 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.codeassist;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;

import de.jcup.asciidoctoreditor.EclipseDevelopmentSettings;
import de.jcup.asciidoctoreditor.EclipseResourceHelper;

public class CodeAssistReferencedFilePathDescriptionCalculator {

    private String prefixToInspect;
    private char referenceFilePathEndMarker;
    private String headerForReferenceFile;
    private String headerForFolder;
    private RootParentFinder resolver;

    public CodeAssistReferencedFilePathDescriptionCalculator(RootParentFinder resolver, String prefixToInspect, char referenceFilePathEndMarker, String headerForReferenceFile,
            String headerForFolder) {
        this.resolver = resolver;
        this.prefixToInspect = prefixToInspect;
        this.referenceFilePathEndMarker = referenceFilePathEndMarker;
        this.headerForReferenceFile = headerForReferenceFile;
        this.headerForFolder = headerForFolder;
    }

    /**
     * If the target is a string, the string will be inspected. If it starts with
     * the defined prefix to inspect, the path after the prefix will be extracted
     * and the absolute path calculated. <br>
     * After this a description will be created. If this is not possible the given
     * target will be returned when a string, otherwise <code>null</code>
     * 
     * @param infoHeader
     * @param target
     * @return a string describing the referenced file path or <code>null</code>
     */
    public String calculateReferencedFilePathDescription(Object target) {
        if (!(target instanceof String)) {
            return null;
        }
        String targetAsString = (String) target;
        if (!targetAsString.startsWith(prefixToInspect)) {
            return targetAsString;
        }
        String converted = convert(targetAsString);
        if (converted != null) {
            return converted;
        }
        return targetAsString;
    }

    private String convert(String targetAsString) {
        if (!targetAsString.startsWith(prefixToInspect)) {
            return null;
        }
        String path = targetAsString.substring(prefixToInspect.length());
        int endMarkerIndex = path.indexOf(referenceFilePathEndMarker);
        if (endMarkerIndex > 0) {
            // we found a marker - so is a file reference (and not a folder). We must now
            // fetch only parts until end marker
            path = path.substring(0, endMarkerIndex);
        }

        File rootParentFile = resolver.findRootParent();
        if (rootParentFile == null) {
            return null;
        }

        File resulting = new File(rootParentFile, path);
        try {
            IFile iFile = EclipseResourceHelper.DEFAULT.toIFile(resulting);
            String calculatedWorkspaceLocation = null;
            if (iFile != null) {
                calculatedWorkspaceLocation = iFile.getFullPath().toPortableString();
            }
            File resultingParentFile = resulting.getParentFile();
            String realParentFolderName = resultingParentFile.toPath().toRealPath().getFileName().toString();

            String pathDescription = "<b>" + resulting.getName() + "</b><br><br><u>Current directory</u>:<br>" + realParentFolderName + "</b><br><br><u>Calculated workspace location:</u><br>"
                    + calculatedWorkspaceLocation;

            if (EclipseDevelopmentSettings.DEBUG_TOOLTIPS_ENABLED) {
                String realPath = resulting.toPath().toRealPath().toString();
                pathDescription = pathDescription + "<br><br><u>Absolute path:</u><br>" + realPath;
            }

            if (resulting.isDirectory()) {
                pathDescription = headerForFolder + pathDescription;
            } else {
                pathDescription = headerForReferenceFile + pathDescription;
            }
            return pathDescription;
        } catch (IOException e) {
            return null;
        }

    }
}
