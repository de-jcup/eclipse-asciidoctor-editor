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

import de.jcup.asciidoctoreditor.asciidoc.AsciiDoctorBackendType;
import de.jcup.asp.api.asciidoc.AsciidocOptions;
import de.jcup.asp.api.asciidoc.AsciidocOptionsBuilder;
import de.jcup.asp.api.asciidoc.AsciidocSafeMode;

public class AsciiDoctorOptionsProvider extends AbstractAsciiDoctorProvider {

    AsciiDoctorOptionsProvider(AsciiDoctorWrapperContext context) {
        super(context);
    }

    public AsciidocOptions createOptions(AsciiDoctorBackendType backend) {
        /* @formatter:off*/
        
        AsciidocOptionsBuilder builder = AsciidocOptions.builder();
        
        File destionationFolder = getOutputFolder().toFile();
        /* @formatter:off */
        builder.
                toDir(destionationFolder).
                safe(AsciidocSafeMode.UNSAFE).
                backend(backend.getBackendString()).
                headerFooter(getContext().isTOCVisible()).
                sourcemap(true).
                baseDir(getContext().getBaseDir()); // the context contains either calculated project base dir or a base directory from a asciidoctorconfig file.
        
        /* @formatter:on*/
        return builder.build();
        /* @formatter:off */
    }

    public void reset() {
        /* nothing to do */
    }

}
