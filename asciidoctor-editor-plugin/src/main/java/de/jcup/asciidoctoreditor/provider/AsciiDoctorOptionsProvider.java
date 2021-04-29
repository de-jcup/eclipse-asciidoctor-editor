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

import org.asciidoctor.Attributes;
import org.asciidoctor.OptionsBuilder;
import org.asciidoctor.SafeMode;

import de.jcup.asciidoctoreditor.asciidoc.AsciiDoctorBackendType;

public class AsciiDoctorOptionsProvider extends AbstractAsciiDoctorProvider {

    AsciiDoctorOptionsProvider(AsciiDoctorProviderContext context) {
        super(context);
    }

    public Map<String, Object> createOptionsContainingAttributes(AsciiDoctorBackendType backend, Attributes attributes) {
        /* @formatter:off*/

        File destionationFolder = getOutputFolder().toFile();
        /* @formatter:off */
        OptionsBuilder opts = OptionsBuilder.options().
                toDir(destionationFolder).
                safe(SafeMode.UNSAFE).
                backend(backend.getBackendString()).
                headerFooter(getContext().isTOCVisible()).
                attributes(attributes).
                option("sourcemap", "true").
                baseDir(getContext().getBaseDir());
        /* @formatter:on*/
        return opts.asMap();
        /* @formatter:off */
    }

    public void reset() {
        /* nothing to do */
    }

}
