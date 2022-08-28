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
package de.jcup.asciidoctoreditor;

import java.io.File;
import java.util.UUID;

import org.eclipse.core.runtime.IPath;

public class UniqueEditorId implements UniqueIdProvider {

    private String uniqueId;
    private String originFileLocationPath;

    public UniqueEditorId(IPath path) {
        if (path != null) {
            File file = path.toFile();
            originFileLocationPath = file.getAbsolutePath();
        } else {
            originFileLocationPath = "fallback_for_missing_file_" + System.nanoTime();
        }
        UUID nameUUIDFromBytes = UUID.nameUUIDFromBytes(originFileLocationPath.getBytes());
        uniqueId = nameUUIDFromBytes.toString();
    }

    public String getOriginFileLocationPath() {
        return originFileLocationPath;
    }

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public String toString() {
        return "UniqueEditorId [uniqueId=" + uniqueId + ", originFileLocationPath=" + originFileLocationPath + "]";
    }

   
}
