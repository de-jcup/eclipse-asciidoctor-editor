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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;

import de.jcup.asciidoctoreditor.outline.AsciiDoctorEditorOutlineLabelProvider;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.asciidoctoreditor.ui.AsciidoctorIconConstants;
import de.jcup.eclipse.commons.PluginContextProvider;
import de.jcup.eclipse.commons.codeassist.ProposalInfoProvider;
import de.jcup.eclipse.commons.codeassist.ProposalProviderContentAssistSupport;

public class DynamicIncludeContentAssistSupport extends ProposalProviderContentAssistSupport {

    private static final String INCLUDE_PREFIX = "include::";

    private CodeAssistReferencedFilePathDescriptionCalculator descriptionCalculator = new CodeAssistReferencedFilePathDescriptionCalculator(new EditorFileRootParentFinder(),

            INCLUDE_PREFIX, '[', "Will include the file: ", "Step furher into folder: ");

    public DynamicIncludeContentAssistSupport(PluginContextProvider provider) {
        super(provider, new AsciidocReferenceProposalSupport(INCLUDE_PREFIX, new EditorFileParentAsBaseParentResolver(), new DynamicIncludesEnabledResolver(), new CodeAssistFileFilter()));
    }

    @Override
    protected ProposalInfoProvider createProposalInfoBuilder() {
        return new ProposalInfoProvider() {

            @Override
            public Object getProposalInfo(IProgressMonitor monitor, Object target) {
                return descriptionCalculator.calculateReferencedFilePathDescription(target);
            }

            @Override
            public Image getImage(Object target) {
                return AsciiDoctorEditorOutlineLabelProvider.getImage(AsciidoctorIconConstants.PATH_OUTLINE_ICON_INCLUDE);
            }
        };
    }

    private static class DynamicIncludesEnabledResolver implements EnableStateResolver {

        @Override
        public boolean isEnabled() {
            return AsciiDoctorEditorPreferences.getInstance().isDynamicCodeAssistForIncludesEnabled();
        }

    }

}
