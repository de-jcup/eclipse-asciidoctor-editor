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
package de.jcup.asciidoctoreditor.document.keywords;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.jcup.eclipse.commons.keyword.DocumentKeyWord;

public class DocumentKeyWords {
    private static final DocumentKeyWord[] ALL_KEYWORDS = createAllKeywords();
    private static final DocumentKeyWord[] ALL_KEYWORDS_EXCEPT_INCLUDES = createAllKeywordsExceptIncludes();

    public static DocumentKeyWord[] getAll() {
        return ALL_KEYWORDS;
    }

    public static DocumentKeyWord[] getAllExceptIncludes() {
        return ALL_KEYWORDS_EXCEPT_INCLUDES;
    }

    private static DocumentKeyWord[] createAllKeywordsExceptIncludes() {
        List<DocumentKeyWord> list = new ArrayList<>();
        addAllExceptIncludes(list);
        return list.toArray(new DocumentKeyWord[list.size()]);
    }

    private static void addAllExceptIncludes(List<DocumentKeyWord> list) {
        list.addAll(Arrays.asList(AsciiDoctorAdmonitionParagraphKeyWords.values()));
        list.addAll(Arrays.asList(AsciiDoctorAdmonitionBlockKeyWords.values()));
        list.addAll(Arrays.asList(AsciiDoctorSectionTitleKeyWords.values()));
        list.addAll(Arrays.asList(AsciiDoctorCommandKeyWords.values()));
        list.addAll(Arrays.asList(AsciiDoctorSpecialAttributesKeyWords.values()));
    }

    private static DocumentKeyWord[] createAllKeywords() {
        List<DocumentKeyWord> list = new ArrayList<>();
        addAllExceptIncludes(list);
        list.addAll(Arrays.asList(AsciiDoctorIncludeKeywords.values()));
        return list.toArray(new DocumentKeyWord[list.size()]);
    }
}
