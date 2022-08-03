/*
 * Copyright 2019 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.asciidoc;

import java.io.File;

import de.jcup.asciidoctoreditor.EditorType;
import de.jcup.asciidoctoreditor.UniqueEditorId;

public class WrapperConvertData {
    public EditorType targetType = EditorType.ASCIIDOC;
    /**
     * This is the file where the base directory calculation will be done! Also the
     * calculation of .asciidocconfig files, base attributes etc. will be done from
     * here!
     */
    private File asciiDocFile;
    private UniqueEditorId editorId;
    private boolean useHiddenFile;
    private File editorFileOrNull;
    private boolean internalPreview;

    public EditorType getTargetType() {
        return targetType;
    }

    public void setTargetType(EditorType targetType) {
        this.targetType = targetType;
    }

    public File getAsciiDocFile() {
        return asciiDocFile;
    }

    public void setAsciiDocFile(File asciiDocFile) {
        this.asciiDocFile = asciiDocFile;
    }

    public UniqueEditorId getEditorId() {
        return editorId;
    }

    public void setEditorId(UniqueEditorId editorId) {
        this.editorId = editorId;
    }

    public boolean isUseHiddenFile() {
        return useHiddenFile;
    }

    public void setUseHiddenFile(boolean useHiddenFile) {
        this.useHiddenFile = useHiddenFile;
    }

    public File getEditorFileOrNull() {
        return editorFileOrNull;
    }

    public void setEditorFileOrNull(File editorFileOrNull) {
        this.editorFileOrNull = editorFileOrNull;
    }

    public boolean isInternalPreview() {
        return internalPreview;
    }

    public void setInternalPreview(boolean internalPreview) {
        this.internalPreview = internalPreview;
    }
}