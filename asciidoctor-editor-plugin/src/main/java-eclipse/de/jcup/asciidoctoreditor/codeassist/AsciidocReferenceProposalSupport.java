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
package de.jcup.asciidoctoreditor.codeassist;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.eclipse.ui.IEditorPart;

import de.jcup.asciidoctoreditor.AsciiDoctorEditor;
import de.jcup.eclipse.commons.codeassist.AbstractWordCodeCompletition;
import de.jcup.eclipse.commons.codeassist.ProposalProvider;
import de.jcup.eclipse.commons.ui.EclipseUtil;

public class AsciidocReferenceProposalSupport extends AbstractWordCodeCompletition {

    private AsciidocReferenceProposalCalculator calculator;
    private EnableStateResolver enableStateResolver;

    public AsciidocReferenceProposalSupport(String prefix, BaseParentDirResolver baseDirResolver, EnableStateResolver enableStateResolver, CodeAssistFileFilter fileFilter) {
        Objects.requireNonNull(prefix);
        Objects.requireNonNull(baseDirResolver);
        Objects.requireNonNull(enableStateResolver);
        Objects.requireNonNull(fileFilter);

        this.enableStateResolver = enableStateResolver;
        calculator = new AsciidocReferenceProposalCalculator(prefix, baseDirResolver, fileFilter);
    }

    @Override
    public Set<ProposalProvider> calculate(String text, int index) {
        IEditorPart activeEditor = EclipseUtil.getActiveEditor();
        if (!(activeEditor instanceof AsciiDoctorEditor) || enableStateResolver.isDisabled()) {
            return Collections.emptySet();
        }
        AsciiDoctorEditor editor = (AsciiDoctorEditor) activeEditor;
        File file = editor.getEditorFileOrNull();
        if (file == null) {
            return Collections.emptySet();
        }
        Set<ProposalProvider> set = new LinkedHashSet<ProposalProvider>();
        Set<AsciidocReferenceProposalData> asciidocReferenceProposalData = calculator.calculate(file, text, index);
        for (AsciidocReferenceProposalData d : asciidocReferenceProposalData) {
            set.add(new AsciidocIncludeProposalProvider(d));
        }
        return set;

    }

    @Override
    public void reset() {

    }

    public class AsciidocIncludeProposalProvider implements ProposalProvider {

        private AsciidocReferenceProposalData asciidocReferenceProposalData;

        private AsciidocIncludeProposalProvider(AsciidocReferenceProposalData asciidocReferenceProposalData) {
            this.asciidocReferenceProposalData = asciidocReferenceProposalData;
        }

        @Override
        public int compareTo(ProposalProvider o) {
            return 0;
        }

        @Override
        public List<String> getCodeTemplate() {
            return Arrays.asList(asciidocReferenceProposalData.getProposedCode());
        }

        @Override
        public String getLabel() {
            return asciidocReferenceProposalData.getLabel();
        }

    }

}
