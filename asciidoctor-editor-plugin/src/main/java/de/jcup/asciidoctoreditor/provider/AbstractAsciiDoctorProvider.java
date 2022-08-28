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

import java.nio.file.Path;

public abstract class AbstractAsciiDoctorProvider {

    private AsciiDoctorWrapperContext context;

    AbstractAsciiDoctorProvider(AsciiDoctorWrapperContext context) {
        if (context == null) {
            throw new IllegalArgumentException("context may never be null!");
        }
        this.context = context;
    }

    AsciiDoctorWrapperContext getContext() {
        return context;
    }

    public Path getOutputFolder() {
        Path outputFolder = getContext().getOutputFolder();
        if (outputFolder == null) {
            throw new IllegalStateException("output folder not defined");
        }
        return outputFolder;
    }

    protected abstract void reset();
}
