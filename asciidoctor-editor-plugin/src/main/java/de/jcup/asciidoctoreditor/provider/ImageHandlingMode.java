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

public enum ImageHandlingMode {
    /*
     * inspect imagesdir attribute - if not set handles relativ pathes, generated
     * images are stored inside temp folder, so not visible in workspace
     */
    DEFAULT,

    /**
     * Like DEFAULT, but this variant stores generated diagram files inside
     * workspace - will be used by plantuml editor when enabled in preferences
     */
    STORE_DIAGRAM_FILES_LOCAL,;

}