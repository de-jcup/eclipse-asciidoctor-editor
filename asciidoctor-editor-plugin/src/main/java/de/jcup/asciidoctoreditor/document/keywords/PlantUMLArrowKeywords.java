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
package de.jcup.asciidoctoreditor.document.keywords;

import de.jcup.eclipse.commons.keyword.DocumentKeyWord;

/**
 * 
 * https://plantuml.com/sequence-diagram
 */
public enum PlantUMLArrowKeywords implements DocumentKeyWord {

    ARROW_0("->"),

    ARROW_1("[->"),

    ARROW_2("[o->"),

    ARROW_3("[o->o"),

    ARROW_4("[x->"),

    ARROW_5("[<-"),

    ARROW_6("[x<-"),

    ARROW_7("->]"),

    ARROW_8("->o]"),

    ARROW_9("o->o]"),

    ARROW_10("->x]"),

    ARROW_11("<-]"),

    ARROW_12("x<-]"),

    ARROW_13("<-"),

    ARROW_14("-->"),

    ARROW_15("--->"),

    ARROW_16("<--"),

    ARROW_17("<---"),

    ARROW_18("<|--"),

    ARROW_19("<|-"),

    ARROW_20("*--"),

    ARROW_21("o--"),

    ARROW_22(".."),

    ARROW_23("-0-"),

    ARROW_24("-(0)-"),

    ARROW_25("-(0-"),

    ARROW_26("-0)-"),

    ;

    private String text;

    private PlantUMLArrowKeywords(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public boolean isBreakingOnEof() {
        return true;
    }

    @Override
    public String getTooltip() {
        return "This is an arrow in plantuml. Please refer to online documentation for more information";
    }

    @Override
    public String getLinkToDocumentation() {
        return "http://plantuml.com";
    }

}
