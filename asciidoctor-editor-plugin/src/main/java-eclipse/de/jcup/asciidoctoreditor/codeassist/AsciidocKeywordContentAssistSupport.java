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

import static de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferenceConstants.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.graphics.Image;

import de.jcup.asciidoctoreditor.document.keywords.AsciiDoctorCommandKeyWords;
import de.jcup.asciidoctoreditor.document.keywords.AsciiDoctorIncludeKeywords;
import de.jcup.asciidoctoreditor.document.keywords.DocumentKeyWords;
import de.jcup.asciidoctoreditor.document.keywords.StartLineAndHavingDoubleColonsDocumentKeyword;
import de.jcup.asciidoctoreditor.preferences.AsciiDoctorEditorPreferences;
import de.jcup.eclipse.commons.PluginContextProvider;
import de.jcup.eclipse.commons.WordListBuilder;
import de.jcup.eclipse.commons.codeassist.ProposalInfoProvider;
import de.jcup.eclipse.commons.codeassist.ProposalProviderContentAssistSupport;
import de.jcup.eclipse.commons.codeassist.ProposalProviderSupport;
import de.jcup.eclipse.commons.codeassist.SimpleWordCodeCompletion;
import de.jcup.eclipse.commons.codeassist.SimpleWordListBuilder;
import de.jcup.eclipse.commons.keyword.DocumentKeyWord;

public class AsciidocKeywordContentAssistSupport extends ProposalProviderContentAssistSupport {

    private static final SimpleWordListBuilder WORD_LIST_BUILDER = new SimpleWordListBuilder();
    private static final NoWordListBuilder NO_WORD_BUILDER = new NoWordListBuilder();

    private AsciidocKeywordLabelProvider labelProvider = new AsciidocKeywordLabelProvider();
    private SimpleWordCodeCompletion simpleWordCompletion;

    public AsciidocKeywordContentAssistSupport(PluginContextProvider provider) {
        super(provider, new SimpleWordCodeCompletion());
        this.simpleWordCompletion = (SimpleWordCodeCompletion) completion;
    }

    @Override
    protected ProposalInfoProvider createProposalInfoBuilder() {
        return new ProposalInfoProvider() {

            @Override
            public Object getProposalInfo(IProgressMonitor monitor, Object target) {
                if (!(target instanceof String)) {
                    return null;
                }
                String word = (String) target;
                for (DocumentKeyWord keyword : DocumentKeyWords.getAllExceptIncludes()) {

                    if (word.equalsIgnoreCase(keyword.getText())) {
                        return keyword.getTooltip();
                    }
                }

                return null;
            }

            @Override
            public Image getImage(Object target) {
                return labelProvider.getImage(target);
            }
        };
    }

    @Override
    protected void prepareCompletion(ProposalProviderSupport completion) {
        
        AsciiDoctorEditorPreferences preferences = AsciiDoctorEditorPreferences.getInstance();
        boolean addKeyWords = preferences.getBooleanPreference(P_CODE_ASSIST_ADD_KEYWORDS);
        boolean addSimpleWords = preferences.getBooleanPreference(P_CODE_ASSIST_ADD_SIMPLEWORDS);

        if (addSimpleWords) {
            simpleWordCompletion.setWordListBuilder(WORD_LIST_BUILDER);
        } else {
            simpleWordCompletion.setWordListBuilder(NO_WORD_BUILDER);
        }
        if (addKeyWords) {
            addAllAsciiDoctorKeyWords();
        }
    }

    protected void addAllAsciiDoctorKeyWords() {
        for (DocumentKeyWord keyword : DocumentKeyWords.getAll()) {
            addKeyWord(keyword);
        }
    }

    protected void addKeyWord(DocumentKeyWord keyword) {
        if (keyword == AsciiDoctorIncludeKeywords.INCLUDE) {
            /* is done by snippet */
            return;
        }
        String text = keyword.getText();
        if (keyword instanceof StartLineAndHavingDoubleColonsDocumentKeyword) {
            if (keyword instanceof AsciiDoctorIncludeKeywords) {
                text += "fileName";
                if (keyword == AsciiDoctorIncludeKeywords.PLANTUML) {
                    /* we add different examples */
                    simpleWordCompletion.add(text + ".plantuml[format=svg, title=\"title\"]");
                    simpleWordCompletion.add(text + ".plantuml[]");
                    simpleWordCompletion.add(text + ".puml[]");
                    simpleWordCompletion.add(text + ".iuml[]");
                    simpleWordCompletion.add(text + ".pu[]");
                    return;
                } else if (keyword == AsciiDoctorIncludeKeywords.DITAA) {
                    /* we add different examples */
                    simpleWordCompletion.add(text + ".ditaa[]");
                    simpleWordCompletion.add(text + ".ditaa[format=png, title=\"title\"]");
                    return;
                }

            } else if (keyword == AsciiDoctorCommandKeyWords.IFDEF || keyword == AsciiDoctorCommandKeyWords.IFNDEF) {
                text += "attributeName";
            } else if (keyword == AsciiDoctorCommandKeyWords.IMAGE) {
//              == PNG Image
//              image::asciidoctor-editor-logo.png[title="AsciiDoctor Editor Logo" opts="inline"]
//              == SVG image
//              image::if_7_Pen_write_writer_2991007.svg[title="A SVG pen for writers...." opts="interactive,inline", 200,200]
                /* we add different examples */
                text += "imageName";
                simpleWordCompletion.add(text + ".png[]");
                simpleWordCompletion.add(text + ".png[title=\"title\" opts=\"inline\"]");
                simpleWordCompletion.add(text + ".svg[title=\"title\" title=\"title\" opts=\"interactive,inline\" width=\"200\" height=\"200\"]");
                simpleWordCompletion.add(text + ".svg[title=\"title\" opts=\"interactive,inline\"]");
                return;
            }
            text += "[]";
        }
        simpleWordCompletion.add(text);
    }

    private static class NoWordListBuilder implements WordListBuilder {

        private NoWordListBuilder() {

        }

        private List<String> list = new ArrayList<>(0);

        @Override
        public List<String> build(String source) {
            return list;
        }

    }

}