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


import de.jcup.asciidoctoreditor.document.keywords.PlantUMLColorDocumentKeywords;
import de.jcup.asciidoctoreditor.document.keywords.PlantUMLKeywordDocumentKeywords;
import de.jcup.asciidoctoreditor.document.keywords.PlantUMLMissingKeywordDocumentKeywords;
import de.jcup.asciidoctoreditor.document.keywords.PlantUMLPreprocessorDocumentKeywords;
import de.jcup.asciidoctoreditor.document.keywords.PlantUMLSkinparameterDocumentKeywords;
import de.jcup.asciidoctoreditor.document.keywords.PlantUMLTypeDocumentKeywords;
import de.jcup.eclipse.commons.PluginContextProvider;
import de.jcup.eclipse.commons.keyword.DocumentKeyWord;

public class PlantUMLKeywordContentAssistSupport extends AsciidocKeywordContentAssistSupport{

    public PlantUMLKeywordContentAssistSupport(PluginContextProvider provider) {
        super(provider);
    }
    
    protected void addAllAsciiDoctorKeyWords() {
        for (DocumentKeyWord keyword : PlantUMLColorDocumentKeywords.values()) {
            addKeyWord(keyword);
        }
        for (DocumentKeyWord keyword : PlantUMLKeywordDocumentKeywords.values()) {
            addKeyWord(keyword);
        }
        for (DocumentKeyWord keyword : PlantUMLMissingKeywordDocumentKeywords.values()) {
            addKeyWord(keyword);
        }
        for (DocumentKeyWord keyword : PlantUMLPreprocessorDocumentKeywords.values()) {
            addKeyWord(keyword);
        }
        for (DocumentKeyWord keyword : PlantUMLSkinparameterDocumentKeywords.values()) {
            addKeyWord(keyword);
        }
        for (DocumentKeyWord keyword : PlantUMLTypeDocumentKeywords.values()) {
            addKeyWord(keyword);
        }
    }
    
    
}