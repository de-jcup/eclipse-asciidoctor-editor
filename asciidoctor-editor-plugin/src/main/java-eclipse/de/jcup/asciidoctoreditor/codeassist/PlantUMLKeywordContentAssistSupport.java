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