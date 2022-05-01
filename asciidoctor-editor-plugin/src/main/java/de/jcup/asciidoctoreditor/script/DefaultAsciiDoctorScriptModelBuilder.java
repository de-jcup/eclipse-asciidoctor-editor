/*
 * Copyright 2017 Albert Tregnaghi
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
package de.jcup.asciidoctoreditor.script;

import static de.jcup.asciidoctoreditor.script.parser.SimpleReferenceParser.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.jcup.asciidoctoreditor.script.parser.SimpleHeadlineParser;
import de.jcup.asciidoctoreditor.script.parser.SimpleInlineAnchorParser;

/**
 * A asciidoc file model builder
 * 
 * @author Albert Tregnaghi
 *
 */
public class DefaultAsciiDoctorScriptModelBuilder implements AsciiDoctorScriptModelBuilder {

    private SimpleHeadlineParser headlineParser = new SimpleHeadlineParser();
    private SimpleInlineAnchorParser inlineAnchorParser = new SimpleInlineAnchorParser();

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.jcup.asciidoctoreditor.script.AsciiDoctorScriptModelBuilder#build(java.
     * lang.String)
     */
    @Override
    public AsciiDoctorScriptModel build(String asciidoctorScript) throws AsciiDoctorScriptModelException {
        AsciiDoctorScriptModel model = new AsciiDoctorScriptModel();

        Collection<AsciiDoctorHeadline> headlines = headlineParser.parse(asciidoctorScript);
        Collection<AsciiDoctorFileReference> includes = INCLUDE_PARSER.parse(asciidoctorScript);
        Collection<AsciiDoctorFileReference> images = IMAGE_PARSER.parse(asciidoctorScript);
        Collection<AsciiDoctorFileReference> plantuml = PLANTUML_PARSER.parse(asciidoctorScript);
        Collection<AsciiDoctorFileReference> ditaa = DITAA_PARSER.parse(asciidoctorScript);
        Collection<AsciiDoctorInlineAnchor> inlineAnchors = inlineAnchorParser.parse(asciidoctorScript);

        model.getHeadlines().addAll(headlines);
        model.getIncludes().addAll(includes);
        model.getInlineAnchors().addAll(inlineAnchors);
        model.getImages().addAll(images);

        model.getDiagrams().addAll(plantuml);
        model.getDiagrams().addAll(ditaa);

        handleHeadlinesWithAnchorsBefore(model);
        handleHeadlinesWithSameCalculatedIdsWhereNoIdSet(model);

        return model;
    }

    private void handleHeadlinesWithAnchorsBefore(AsciiDoctorScriptModel model) {
        /* headlines having an anchor before do use the ID of the anchor! */
        for (AsciiDoctorHeadline headline : model.getHeadlines()) {
            int headlinePosition = headline.getPosition();

            for (AsciiDoctorInlineAnchor anchor : model.getInlineAnchors()) {
                int anchorEnd = anchor.getEnd();
                if (anchorEnd + 2 == headlinePosition) { /* +2 necessary because of new line+one more */
                    headline.setId(anchor.getId());
                    break;
                }
            }
        }

    }

    /*
     * Handle at last - when other id calculation is done!
     */
    private void handleHeadlinesWithSameCalculatedIdsWhereNoIdSet(AsciiDoctorScriptModel model) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        Set<String> firstTagged = new HashSet<>();
        for (AsciiDoctorHeadline headline : model.getHeadlines()) {
            String calculatedId = headline.getCalculatedId();
            if (calculatedId == null) {
                continue; // should never happen but...
            }
            /* always increment and get back */
            Integer count = map.compute(calculatedId, (k, v) -> v == null ? 1 : v + 1);
            /* when id is already set - do not change it */
            if (headline.isIdSet()) {
                continue;
            }
            if (firstTagged.contains(calculatedId)) {
                headline.setId(calculatedId + "_" + count);
            } else {
                headline.setId(calculatedId);
                firstTagged.add(calculatedId);
            }
        }

    }

    protected int getIndexWhereGraphvizBecomesNecessary(String asciidoctorScript) {
        int index = asciidoctorScript.indexOf("[plantuml");
        if (index == -1) {
            index = asciidoctorScript.indexOf("[graphviz");
        }
        return index;
    }

}
